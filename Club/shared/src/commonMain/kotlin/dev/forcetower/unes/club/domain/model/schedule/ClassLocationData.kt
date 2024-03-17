package dev.forcetower.unes.club.domain.model.schedule

import dev.forcetower.unes.club.data.storage.database.Class
import dev.forcetower.unes.club.data.storage.database.ClassGroup
import dev.forcetower.unes.club.data.storage.database.ClassLocation
import dev.forcetower.unes.club.data.storage.database.Discipline

data class ClassLocationData(
    val location: ClassLocation,
    val group: ClassGroup,
    val clazz: Class,
    val discipline: Discipline
)