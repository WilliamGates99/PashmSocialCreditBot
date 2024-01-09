package domain.model

data class UserRatingInfo(
    val groupId: Long,
    val groupTitle: String,
    val userId: Long,
    val username: String,
    val firstName: String,
    val rating: Long,
    val createdAt: Long,
    val modifiedAt: Long
)