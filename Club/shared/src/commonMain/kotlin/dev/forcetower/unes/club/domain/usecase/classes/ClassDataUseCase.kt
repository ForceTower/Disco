package dev.forcetower.unes.club.domain.usecase.classes

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import dev.forcetower.unes.club.domain.repository.local.ClassRepository

class ClassDataUseCase internal constructor(
    private val repository: ClassRepository
) {
    @NativeCoroutines
    fun groupDetails(groupId: Long) = repository.getClassData(groupId)

    @NativeCoroutines
    fun materials(groupId: Long) = repository.getMaterials(groupId)

    @NativeCoroutines
    fun absences(groupId: Long) = repository.getAbsences(groupId)

    @NativeCoroutines
    fun groupItems(groupId: Long) = repository.getItems(groupId)

    @NativeCoroutines
    fun fetchDataFor(groupId: Long) = repository.fetchData(groupId)
}