package data

import domain.model.UserRatingInfo

interface RatingRepository {

    fun getRatingsList(): List<UserRatingInfo>

    fun getUserRating(
        groupId: Long,
        userId: Long
    ): UserRatingInfo?

    fun updateUserRating(
        messageSenderId: Long,
        groupId: Long,
        groupTitle: String,
        userId: Long,
        username: String,
        firstName: String,
        socialCreditsChange: Long
    ): Result<UserRatingInfo>
}