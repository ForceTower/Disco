package dev.forcetower.unes.club.data.processor

import com.benasher44.uuid.uuid4
import dev.forcetower.unes.club.data.storage.database.ClassItem
import dev.forcetower.unes.club.data.storage.database.ClassMaterial
import dev.forcetower.unes.club.data.storage.database.GeneralDB
import dev.forcetower.unes.club.util.primitives.toLong
import dev.forcetower.unes.singer.data.model.dto.Lecture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class LectureProcessor(
    private val database: GeneralDB,
    // local database group id :)
    private val groupId: Long,
    private val lectures: List<Lecture>,
    private val notify: Boolean
) {
    suspend fun execute() = withContext(Dispatchers.IO) {
        database.transactionWithResult {
            executeInTransaction()
        }
    }

    fun executeInTransaction() {
        lectures.forEach { lecture ->
            val current = database.classItemDao.getItemByIdentifiers(groupId, lecture.ordinal)
            val classId = if (current != null) {
                val copied = current.copy(
                    date = lecture.date,
                    situation = lecture.situation.asLectureSituation(),
                    subject = lecture.subject,
                    numberOfMaterials = lecture.materials.size.toLong(),
                    materialLinks = ""
                )
                database.classItemDao.update(copied)
                current.id
            } else {
                database.classItemDao.insert(
                    ClassItem(
                        id = 0L,
                        number = lecture.ordinal.toLong(),
                        groupId = groupId,
                        date = lecture.date,
                        isNew = true.toLong(),
                        situation = lecture.situation.asLectureSituation(),
                        subject = lecture.subject,
                        numberOfMaterials = lecture.materials.size.toLong(),
                        materialLinks = ""
                    )
                )
            }

            lecture.materials.forEach { material ->
                val mat = database.classMaterialDao.getMaterialsByIdentifiers(material.description, material.url, groupId)
                if (mat != null) {
                    database.classMaterialDao.update(mat.copy(classItemId = classId))
                } else {
                    database.classMaterialDao.insert(
                        ClassMaterial(
                            id = 0L,
                            name = material.description,
                            isNew = true.toLong(),
                            notified = (!notify).toLong(),
                            groupId = groupId,
                            link = material.url,
                            classItemId = classId,
                            uuid = uuid4().toString()
                        )
                    )
                }
            }
        }
    }

    private fun Int.asLectureSituation(): String {
        return when (this) {
            2 -> "realizada"
            else -> "pendente"
        }
    }
}