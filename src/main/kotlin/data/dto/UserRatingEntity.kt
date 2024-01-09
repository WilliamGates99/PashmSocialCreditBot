package data.dto

import domain.model.UserRatingInfo
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserRatingEntity(id: EntityID<Long>) : LongEntity(id) {
    var groupId by UsersRatingsTable.groupId
    var groupTitle by UsersRatingsTable.groupTitle
    var userId by UsersRatingsTable.userId
    var username by UsersRatingsTable.username
    var firstName by UsersRatingsTable.firstName
    var rating by UsersRatingsTable.rating
    var createdAt by UsersRatingsTable.createdAt
    var modifiedAt by UsersRatingsTable.modifiedAt

    companion object : LongEntityClass<UserRatingEntity>(UsersRatingsTable)

    fun toUserRatingInfo(): UserRatingInfo {
        return UserRatingInfo(
            groupId = groupId,
            groupTitle = groupTitle,
            userId = userId,
            username = username,
            firstName = firstName,
            rating = rating,
            createdAt = createdAt,
            modifiedAt = modifiedAt
        )
    }
}