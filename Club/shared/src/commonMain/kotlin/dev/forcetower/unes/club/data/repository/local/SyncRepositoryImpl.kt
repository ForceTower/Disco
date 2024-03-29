package dev.forcetower.unes.club.data.repository.local

import dev.forcetower.unes.club.data.processor.DisciplinesProcessor
import dev.forcetower.unes.club.data.processor.LectureProcessor
import dev.forcetower.unes.club.data.processor.MessagesProcessor
import dev.forcetower.unes.club.data.processor.MissedLectureProcessor
import dev.forcetower.unes.club.data.processor.SemestersProcessor
import dev.forcetower.unes.club.data.service.client.SyncService
import dev.forcetower.unes.club.data.storage.database.GeneralDB
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.data.storage.database.PlatformCourse
import dev.forcetower.unes.club.data.storage.database.SyncRegistry
import dev.forcetower.unes.club.domain.model.sync.SyncResult
import dev.forcetower.unes.club.domain.repository.local.SyncRepository
import dev.forcetower.unes.club.extensions.toTitleCase
import dev.forcetower.unes.club.util.date.parseZonedDateTime
import dev.forcetower.unes.club.util.primitives.asBoolean
import dev.forcetower.unes.club.util.primitives.toLong
import dev.forcetower.unes.singer.Singer
import dev.forcetower.unes.singer.domain.exception.InvalidLoginCredentialException
import dev.forcetower.unes.singer.domain.exception.LoginDevException
import dev.forcetower.unes.singer.domain.model.Authorization
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.toInstant

class SyncRepositoryImpl(
    private val database: GeneralDatabase,
    private val general: GeneralDB,
    private val service: SyncService,
    private val singer: Singer
) : SyncRepository {
    override suspend fun sync(loadDetails: Boolean): SyncResult = withContext(Dispatchers.IO) {
        val registry = general.syncRegistryDao.create("Snowpiercer")
        try {
            doSync(loadDetails, registry)
        } catch (e: Exception) {
            general.syncRegistryDao.finish(
                registry,
                1,
                30,
                0,
                e.message ?: "Erro desconhecido durante execução"
            )
            SyncResult.OtherError(e.message ?: "Fail empty message", e)
        }
    }

    override fun getSyncRegistry(): Flow<List<SyncRegistry>> {
        return general.syncRegistryDao.selectAll()
    }

    override suspend fun fetchServerSync() {
        val result = service.history()
        withContext(Dispatchers.IO) {
            database.transaction {
                result.forEach { item ->
                    println(item.startAt)
                    val (start, startOffset) = parseZonedDateTime(item.startAt)
                    val startedAt = start.toInstant(startOffset).toEpochMilliseconds()

                    val finishedAt = item.finishedAt?.let {
                        val (finish, finishOffset) = parseZonedDateTime(it)
                        finish.toInstant(finishOffset).toEpochMilliseconds()
                    }

                    database.syncRegistryQueries.insertReplace(
                        id = 0,
                        item.id,
                        startedAt,
                        finishedAt,
                        item.completed.toLong(),
                        item.success?.toLong() ?: 0,
                        item.error?.toLong() ?: 0,
                        item.executor,
                        item.message ?: "Nenhuma mensagem deixada",
                        0
                    )
                }
            }
        }
    }

    private suspend fun doSync(loadDetails: Boolean, registry: SyncRegistry): SyncResult {
        val access = database.accessQueries.selectAccess().executeAsOneOrNull() ?: run {
            general.syncRegistryDao.finish(
                registry,
                completed = 1,
                error = 1,
                success = 0,
                message = "Credenciais de acesso não encontradas"
            )
            return SyncResult.NoOp
        }
        if (!access.valid.asBoolean()) {
            general.syncRegistryDao.finish(
                registry,
                completed = 1,
                error = 2,
                success = 0,
                message = "Credenciais de acesso inválidas"
            )
            return SyncResult.NoOp
        }

        database.gradeQueries.markAllNotified()
        database.messageQueries.setAllNotified()
        database.classMaterialQueries.markAllNotified()

        val auth = Authorization(access.username, access.password)
        val login = runCatching { singer.me(auth) }
            .onFailure {
                if (it is InvalidLoginCredentialException) {
                    general.syncRegistryDao.finish(
                        registry,
                        completed = 1,
                        error = 3,
                        success = 0,
                        message = "Credenciais de acesso incorretas"
                    )
                    return SyncResult.InvalidCredentials
                }

                if (it is LoginDevException) {
                    general.syncRegistryDao.finish(
                        registry,
                        completed = 1,
                        error = 4,
                        success = 0,
                        message = "Erro de processamento do login"
                    )
                    return SyncResult.LoginError(it.message ?: "Login dev error", it)
                }
            }

        val person = login.getOrThrow()
        singer.setDefaultAuthorization(auth)

        val course = runCatching {
            singer.course(person.id)
        }.getOrNull()

        val profileId = general.profileDao.insert(person, course?.name)

        if (course != null) {
            database.platformCourseQueries.insertItem(
                course.id, course.name.toTitleCase(), course.resumedName
            )
        }

        val messages = singer.messages(person.id)
        MessagesProcessor(messages, database, false).execute()

        val semesters = singer.semesters(person.id)
        SemestersProcessor(semesters, database).execute()

        val current = semesters.maxByOrNull { it.start } ?: return SyncResult.InvalidSemester
        val disciplines = singer.grades(person.id, current.id)
        val semester = database.semesterQueries.selectSemester(current.id).executeAsOne()
        DisciplinesProcessor(general, disciplines, semester.id, profileId, true).execute()

        if (!loadDetails) {
            general.syncRegistryDao.finish(
                registry,
                completed = 1,
                error = 0,
                success = 1,
                message = "Finalizado com sucesso"
            )
            return SyncResult.Completed
        }

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

        general.syncRegistryDao.finish(
            registry,
            completed = 1,
            error = 0,
            success = 1,
            message = "Finalizado com sucesso. Detalhes atualizados"
        )

        return SyncResult.Completed
    }
}