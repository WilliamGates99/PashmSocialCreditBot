package data.dto

import domain.model.UserRatingInfo
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserSocialCreditEntity(id: EntityID<Long>) : LongEntity(id) {
    var groupId by UsersSocialCreditsTable.groupId
    var groupTitle by UsersSocialCreditsTable.groupTitle
    var userId by UsersSocialCreditsTable.userId
    var username by UsersSocialCreditsTable.username
    var firstName by UsersSocialCreditsTable.firstName
    var socialCredits by UsersSocialCreditsTable.socialCredits
    var createdAt by UsersSocialCreditsTable.createdAt
    var modifiedAt by UsersSocialCreditsTable.modifiedAt

    companion object : LongEntityClass<UserSocialCreditEntity>(UsersSocialCreditsTable)

    fun toUserRatingInfo(): UserRatingInfo {
        return UserRatingInfo(
            groupId = groupId,
            groupTitle = groupTitle,
            userId = userId,
            username = username,
            firstName = firstName,
            socialCredits = socialCredits,
            createdAt = createdAt,
            modifiedAt = modifiedAt
        )
    }
}