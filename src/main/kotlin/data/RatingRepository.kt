package data

import domain.model.UserRatingInfo

interface RatingRepository {

    fun updateUserRating(
        userId: Long,
        username: String,
        ratingChange: Long
    ): UserRatingInfo

    fun getUserRating(userId: Long): UserRatingInfo?

    fun getRatingsList(): List<UserRatingInfo>
}