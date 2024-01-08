package data.dto

import domain.model.UserRatingInfo
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserRatingEntity(id: EntityID<Long>) : LongEntity(id) {
    var userId by UsersRatingsTable.userId
    var username by UsersRatingsTable.username
    var rating by UsersRatingsTable.rating

    companion object : LongEntityClass<UserRatingEntity>(UsersRatingsTable)

    fun toUserRatingInfo(): UserRatingInfo {
        return UserRatingInfo(
            userId = userId,
            username = username,
            rating = rating
        )
    }
}