package dev.forcetower.unes.club.data.storage.database.dao

import dev.forcetower.unes.club.data.storage.database.GeneralDatabase
import dev.forcetower.unes.club.data.storage.database.Profile
import dev.forcetower.unes.club.extensions.toTitleCase
import dev.forcetower.unes.club.util.primitives.toLong
import dev.forcetower.unes.singer.data.model.dto.Person

class ProfileDao(
    private val database: GeneralDatabase
) {
    fun insert(person: Person, platformCourseValue: String?): Long {
        val name = person.name.trim().toTitleCase()
        val me = selectMeDirect()
        if (me != null) {
            updateProfileName(name)
            updateProfileMockStatus(false)
            updateProfilePlatformId(person.id)
            person.email?.trim()?.lowercase()?.let {
                updateProfileEmail(it)
            }
            if (platformCourseValue != null) {
                updateProfilePlatformCourseValue(platformCourseValue.toTitleCase())
            }
            return me.id
        } else {
            return insert(
                Profile(
                    id = 0L,
                    name = name,
                    email = person.email?.trim(),
                    platformId = person.id,
                    me = 1L,
                    calcScore = 0.0,
                    course = 0L,
                    imageUrl = null,
                    mocked = 0L,
                    score = 0.0,
                    platformCourseValue = platformCourseValue?.trim()?.toTitleCase()
                )
            )
        }
    }

    private fun updateProfileEmail(email: String) {
        database.profileQueries.updateEmail(email)
    }

    private fun updateProfilePlatformCourseValue(platformCourseValue: String?) {
        database.profileQueries.updatePlatformCourseValue(platformCourseValue?.trim())
    }

    private fun updateProfilePlatformId(platformId: Long) {
        database.profileQueries.updatePlatformId(platformId)
    }

    private fun updateProfileMockStatus(mocked: Boolean) {
        database.profileQueries.updateMockedStatus(mocked.toLong())
    }

    private fun updateProfileName(name: String) {
        database.profileQueries.updateName(name)
    }

    private fun selectMeDirect(): Profile? {
        return database.profileQueries.selectMe().executeAsOneOrNull()
    }

    private fun insert(profile: Profile): Long {
        return database.transactionWithResult {
            database.profileQueries.insertReplace(
                profile.id,
                profile.name,
                profile.email,
                profile.score,
                profile.calcScore,
                profile.course,
                profile.imageUrl,
                profile.platformId,
                profile.me,
                profile.mocked,
                profile.platformCourseValue
            )
            database.profileQueries.lastInsertedRow().executeAsOne()
        }
    }

    fun updateCalculatedScore(score: Double) {
        database.profileQueries.updateCalcScore(score)
    }
}