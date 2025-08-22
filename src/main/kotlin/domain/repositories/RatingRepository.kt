package domain.repositories

import dev.inmo.tgbotapi.types.chat.member.ChatMember
import domain.model.UserSocialCreditsInfo

interface RatingRepository {

    fun initDatabase()

    fun createDbSchemas()

    fun getGroupSocialCreditsList(groupId: Long): List<UserSocialCreditsInfo>

    fun getUserSocialCredits(
        groupId: Long,
        userId: Long
    ): UserSocialCreditsInfo?

    fun updateUserSocialCredits(
        messageSenderId: Long,
        messageSenderStatus: ChatMember.Status,
        groupId: Long,
        groupTitle: String,
        userId: Long,
        username: String,
        firstName: String,
        socialCreditsChange: Long
    ): Result<UserSocialCreditsInfo>
}