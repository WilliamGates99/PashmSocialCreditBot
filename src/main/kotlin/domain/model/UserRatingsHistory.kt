package domain.model

data class UserRatingsHistory(
    val groupId: Long,
    val raterUserId: Long,
    val targetUserId: Long,
    val createdAt: Long,
    val modifiedAt: Long
)