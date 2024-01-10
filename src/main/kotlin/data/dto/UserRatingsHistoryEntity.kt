package data.dto

import domain.model.UserRatingsHistory
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserRatingsHistoryEntity(id: EntityID<Long>) : LongEntity(id) {
    var groupId by UserRatingsHistoryTable.groupId
    var raterUserId by UserRatingsHistoryTable.raterUserId
    var targetUserId by UserRatingsHistoryTable.targetUserId
    var createdAt by UserRatingsHistoryTable.createdAt
    var modifiedAt by UserRatingsHistoryTable.modifiedAt

    companion object : LongEntityClass<UserRatingsHistoryEntity>(UserRatingsHistoryTable)

    fun toUserRatingsHistory(): UserRatingsHistory {
        return UserRatingsHistory(
            groupId = groupId,
            raterUserId = raterUserId,
            targetUserId = targetUserId,
            createdAt = createdAt,
            modifiedAt = modifiedAt
        )
    }
}