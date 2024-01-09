package data.dto

import domain.model.UserRatingsHistory
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserRatingsHistoryEntity(id: EntityID<Long>) : LongEntity(id) {
    var groupId by UserRatingsHistoryTable.groupId
    var userId by UserRatingsHistoryTable.userId
    var createdAt by UserRatingsHistoryTable.createdAt
    var modifiedAt by UserRatingsHistoryTable.modifiedAt

    companion object : LongEntityClass<UserRatingsHistoryEntity>(UserRatingsHistoryTable)

    fun toUserRatingsHistory(): UserRatingsHistory {
        return UserRatingsHistory(
            groupId = groupId,
            userId = userId,
            createdAt = createdAt,
            modifiedAt = modifiedAt
        )
    }
}