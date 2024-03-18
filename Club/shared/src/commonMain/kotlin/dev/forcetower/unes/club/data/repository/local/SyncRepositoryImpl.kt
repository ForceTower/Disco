package dev.forcetower.unes.club.data.repository.local

import dev.forcetower.unes.club.data.processor.DisciplinesProcessor
import dev.forcetower.unes.club.data.processor.LectureProcessor
import dev.forcetower.unes.club.data.processor.MessagesProcessor
import dev.forcetower.unes.club.data.processor.MissedLectureProcessor
import dev.forcetower.unes.club.data.processor.SemestersProcessor
import dev.forcetower.unes.club.data.storage.database.GeneralDB
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.domain.model.auth.LoginFailReason
import dev.forcetower.unes.club.domain.model.auth.LoginState
import dev.forcetower.unes.club.domain.model.sync.SyncResult
import dev.forcetower.unes.club.domain.repository.local.SyncRepository
import dev.forcetower.unes.club.util.primitives.asBoolean
import dev.forcetower.unes.singer.Singer
import dev.forcetower.unes.singer.domain.exception.InvalidLoginCredentialException
import dev.forcetower.unes.singer.domain.exception.LoginDevException
import dev.forcetower.unes.singer.domain.model.Authorization
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class SyncRepositoryImpl(
    private val database: GeneralDatabase,
    private val general: GeneralDB,
    private val singer: Singer
) : SyncRepository {
    override suspend fun sync(loadDetails: Boolean): SyncResult = withContext(Dispatchers.IO) {
        try {
            doSync(loadDetails)
        } catch (e: Exception) {
            SyncResult.OtherError(e.message ?: "Fail empty message", e)
        }
    }

    private suspend fun doSync(loadDetails: Boolean): SyncResult {
        val access = database.accessQueries.selectAccess().executeAsOneOrNull() ?: return SyncResult.NoOp
        if (!access.valid.asBoolean()) {
            return SyncResult.NoOp
        }

        database.gradeQueries.markAllNotified()
        database.messageQueries.setAllNotified()
        database.classMaterialQueries.markAllNotified()

        val auth = Authorization(access.username, access.password)
        val login = runCatching { singer.me(auth) }
            .onFailure {
                if (it is InvalidLoginCredentialException) {
                    return SyncResult.InvalidCredentials
                }

                if (it is LoginDevException) {
                    return SyncResult.LoginError(it.message ?: "Login dev error", it)
                }
            }

        val person = login.getOrThrow()
        val profileId = general.profileDao.insert(person)

        singer.setDefaultAuthorization(auth)

        val messages = singer.messages(person.id)
        MessagesProcessor(messages, database, true).execute()

        val semesters = singer.semesters(person.id)
        SemestersProcessor(semesters, database).execute()

        val current = semesters.maxByOrNull { it.start } ?: return SyncResult.InvalidSemester
        val disciplines = singer.grades(person.id, current.id)
        val semester = database.semesterQueries.selectSemester(current.id).executeAsOne()
        DisciplinesProcessor(general, disciplines, semester.id, profileId, false).execute()

        if (!loadDetails) return SyncResult.Completed

        val classes = disciplines.flatMap { it.classes }

        classes.map { clazz -> clazz.id to runCatching { singer.lectures(clazz.id, 0, 0) } }
            .forEach { pair ->
                val (id, result) = pair
                val lectures = result.getOrNull()
                if (lectures != null) {
                    val group = database.classGroupQueries.findByPlarformId(id).executeAsOneOrNull()
                    if (group != null) {
                        LectureProcessor(general, group.id, lectures, true).execute()
                    }
                }
            }

        classes.map { clazz -> clazz.id to runCatching { singer.absences(person.id, clazz.id, 0, 0) } }
            .forEach { pair ->
                val (id, outcome) = pair
                val absences = outcome.getOrNull()
                if (absences != null) {
                    val group = database.classGroupQueries.findByPlarformId(id).executeAsOneOrNull()
                    if (group != null) {
                        MissedLectureProcessor(general, database, profileId, group.id, absences, true).execute()
                    }
                }
            }

        return SyncResult.Completed
    }
}