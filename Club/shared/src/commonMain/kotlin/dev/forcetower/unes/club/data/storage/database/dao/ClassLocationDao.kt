package dev.forcetower.unes.club.data.storage.database.dao

import dev.forcetower.unes.club.data.storage.database.ClassLocation
import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.data.storage.database.Profile
import dev.forcetower.unes.club.util.primitives.toLong

class ClassLocationDao(
    private val database: GeneralDatabase
) {
    fun putNewSchedule(allocations: List<ClassLocation>) {
        if (allocations.isEmpty()) return

        val profile = getMeProfile()
        profile ?: return

        val hidden = getHiddenLocations()
        wipeScheduleProfile(profile.id)

        allocations.forEach { insert(it) }
        hidden.forEach { setClassHiddenHidden(true, it.groupId, it.day, it.startsAt, it.endsAt, it.profileId) }
    }

    private fun setClassHiddenHidden(
        hidden: Boolean,
        groupId: Long,
        day: String,
        startsAt: String,
        endsAt: String,
        profileId: Long
    ) {
        database.classLocationQueries.setClassHidden(hidden.toLong(), groupId, day, startsAt, endsAt, profileId)
    }

    private fun insert(location: ClassLocation) {
        database.classLocationQueries.insertIgnore(
            location.id,
            location.groupId,
            location.profileId,
            location.startsAt,
            location.endsAt,
            location.day,
            location.room,
            location.modulo,
            location.campus,
            location.uuid,
            location.hiddenOnSchedule,
            location.startsAtInt,
            location.endsAtInt,
            location.dayInt
        )
    }

    private fun wipeScheduleProfile(profileId: Long) {
        database.classLocationQueries.wipeProfileSchedule(profileId)
    }

    private fun getHiddenLocations(): List<ClassLocation> {
        return database.classLocationQueries.selectHiddenSchedule().executeAsList()
    }

    private fun getMeProfile(): Profile? {
        return database.profileQueries.selectMe().executeAsOneOrNull()
    }
}