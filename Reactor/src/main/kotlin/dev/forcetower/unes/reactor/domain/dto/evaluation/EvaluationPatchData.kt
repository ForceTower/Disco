package dev.forcetower.unes.reactor.domain.dto.evaluation

import com.google.gson.annotations.SerializedName

data class EvaluationPatchData(
    val semester: Int,
    val score: Double,
    val course: Long?,
    val stats: List<EvaluationDisciplineData>
)

data class EvaluationDisciplineData(
    @SerializedName("code")
    val disciplineCode: String,
    @SerializedName("discipline_name")
    val disciplineName: String,
    @SerializedName("credits")
    val disciplineCredits: Int,
    @SerializedName("group")
    val disciplineGroup: String,
    @SerializedName("semester")
    val sagresSemesterId: Long,
    @SerializedName("semester_name")
    val semesterName: String,
    @SerializedName("teacher")
    val teacherName: String,
    @SerializedName("teacherEmail")
    val teacherEmail: String?,
    @SerializedName("grade")
    val finalGrade: Double?,
    @SerializedName("partial_score")
    val partialGrade: Double?,
    @SerializedName("info")
    val values: List<EvaluationSimpleGrade>
)

data class EvaluationSimpleGrade(
    val name: String? = null,
    val date: String? = null,
    val value: String? = null
)

