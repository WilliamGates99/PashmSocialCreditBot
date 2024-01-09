package domain.model

data class UserRatingsHistory(
    val groupId: Long,
    val userId: Long,
    val createdAt: Long,
    val modifiedAt: Long
)