package dev.forcetower.unes.singer.data.model.dto

import dev.forcetower.unes.singer.data.model.base.DisciplineCompleteDTO

data class DisciplineData(
    val id: Long,
    val disciplineId: Long,
    val name: String,
    val code: String,
    val program: String?,
    val hours: Int,
    val department: String?,
    val classes: List<DisciplineClass>,
    val evaluations: List<ClassEvaluation>,
    val result: DisciplineResult?
) {
    companion object {
        internal fun createFromDTO(it: DisciplineCompleteDTO): DisciplineData {
            return DisciplineData(
                it.id,
                it.activity.id,
                it.activity.name.trim(),
                it.activity.code.trim(),
                it.activity.program?.trim(),
                it.activity.hours,
                it.activity.department?.name?.trim(),
                it.classes.items.map { clazz ->
                    DisciplineClass(
                        clazz.id,
                        clazz.description.trim(),
                        clazz.type.trim(),
                        clazz.teachers?.items?.firstOrNull()?.person,
                        clazz.groupDetails.hours,
                        clazz.groupDetails.program?.trim(),
                        clazz.allocations?.items ?: emptyList(),
                        clazz.lectures?.items?.map { lecture ->
                            Lecture.fromDTO(lecture)
                        } ?: emptyList()
                    )
                },
                it.evaluations?.items?.map { evaluation ->
                    ClassEvaluation(
                        evaluation.name,
                        evaluation.grades?.items?.map { grade ->
                            ClassGrade(
                                grade.ordinal,
                                grade.name.trim(),
                                grade.nameShort.trim(),
                                grade.date?.trim(),
                                grade.weight,
                                grade.grade?.value
                            )
                        } ?: emptyList()
                    )
                } ?: emptyList(),
                it.result
            )
        }
    }
}