package dev.forcetower.unes.club.data.processor

import com.benasher44.uuid.uuid4
import dev.forcetower.unes.club.data.storage.database.ClassAbsence
import dev.forcetower.unes.club.data.storage.database.GeneralDB
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.util.primitives.toLong
import dev.forcetower.unes.singer.data.model.dto.LectureMissed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class MissedLectureProcessor(
    private val general: GeneralDB,
    private val database: GeneralDatabase,
    // local database profile id
    private val profileId: Long,
    // local database group id :)
    private val groupId: Long,
    private val absences: List<LectureMissed>,
    private val notify: Boolean
) {
    suspend fun execute() = withContext(Dispatchers.IO) {
        database.transactionWithResult {
            executeInTransaction()
        }
    }

    private fun executeInTransaction() {
        val group = database.classGroupQueries.findById(groupId).executeAsOneOrNull() ?: return
        val classId = group.classId

//        val before = database.classAbsenceDao().getDirectWithDetails(classId)
        database.classAbsenceQueries.resetAbsenceForClass(classId)

        val mapped = absences.map { absence ->
            ClassAbsence(
                id = 0L,
                description = absence.lecture.subject ?: "Sem descrição",
                date = absence.lecture.date ?: "Sem data",
                notified = (!notify).toLong(),
                profileId = profileId,
                grouping = group.group,
                classId = classId,
                sequence = absence.lecture.ordinal.toLong(),
                uuid = uuid4().toString()
            )
        }

        general.classAbsenceDao.insert(mapped)

//        val after = database.classAbsenceDao.getDirectWithDetails(classId)

        // before: [A, B, C, D]
        // after:  [B, C, D, E]

        // new:    [E]
//        val new = after.filter { a -> before.any { !it.isSame(a) } }
        // removed [A]
//        val removed = before.filter { b -> after.any { !it.isSame(b) } }

//        new.forEach {
//            // TODO Show notification
//        }
//
//        removed.forEach {
//            // TODO Show notification
//        }

        if (!notify) database.classAbsenceQueries.markAllNotified()
    }
}