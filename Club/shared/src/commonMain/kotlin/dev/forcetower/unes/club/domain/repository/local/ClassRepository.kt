package dev.forcetower.unes.club.domain.repository.local

import dev.forcetower.unes.club.data.storage.database.ClassAbsence
import dev.forcetower.unes.club.data.storage.database.ClassItem
import dev.forcetower.unes.club.data.storage.database.ClassMaterial
import dev.forcetower.unes.club.domain.model.disciplines.ClassGroupData
import kotlinx.coroutines.flow.Flow

internal interface ClassRepository {
    fun getClassData(groupId: Long): Flow<ClassGroupData?>
    fun getMaterials(groupId: Long): Flow<List<ClassMaterial>>
    fun getItems(groupId: Long): Flow<List<ClassItem>>
    fun getAbsences(groupId: Long): Flow<List<ClassAbsence>>
    fun fetchData(groupId: Long): Flow<Unit>
}