package data

import domain.model.UserSocialCreditsInfo

interface RatingRepository {

    fun getGroupSocialCreditsList(groupId: Long): List<UserSocialCreditsInfo>

    fun getUserSocialCredits(
        groupId: Long,
        userId: Long
    ): UserSocialCreditsInfo?

    fun updateUserSocialCredits(
        messageSenderId: Long,
        messageSenderStatus: String,
        groupId: Long,
        groupTitle: String,
        userId: Long,
        username: String,
        firstName: String,
        socialCreditsChange: Long
    ): Result<UserSocialCreditsInfo>
}