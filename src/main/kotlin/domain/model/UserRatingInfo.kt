package domain.model

data class UserRatingInfo(
    val userId: Long,
    val username: String,
    val rating: Long
)