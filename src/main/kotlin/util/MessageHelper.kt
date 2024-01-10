package util

import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.TelegramFile
import data.RatingRepository
import kotlin.math.absoluteValue

object MessageHelper {

    fun MessageHandlerEnvironment.sendLongLiveTheKingSticker(message:Message){
        bot.sendSticker(
            chatId = ChatId.fromId(message.chat.id),
            sticker = Stickers.HOLY_KING_FILE_ID,
            replyToMessageId = message.messageId,
            disableNotification = true,
            replyMarkup = null
        )
    }

    fun MessageHandlerEnvironment.sendWomenGif(message: Message){
        bot.sendAnimation(
            chatId = ChatId.fromId(message.chat.id),
            animation = TelegramFile.ByFileId(Gifs.WOMEN_FILE_ID),
            replyToMessageId = message.messageId,
            disableNotification = true
        )
    }

    fun MessageHandlerEnvironment.sendNotGroupMessage(message: Message) {
        bot.sendMessage(
            chatId = ChatId.fromId(message.chat.id),
            text = "\uD83D\uDE44The Social Credit System only works in groups.",
            disableNotification = true
        )
    }

    fun MessageHandlerEnvironment.sendCreditingYourselfProhibitionMessage(message: Message) {
        bot.sendMessage(
            chatId = ChatId.fromId(message.chat.id),
            text = "\uD83D\uDEABThe party prohibits crediting yourself. Great Leader Xi is watching over you!",
            replyToMessageId = message.messageId,
            disableNotification = true
        )
    }

    fun MessageHandlerEnvironment.sendCreditingSocialCreditBotProhibitionMessage(message: Message) {
        bot.sendMessage(
            chatId = ChatId.fromId(message.chat.id),
            text = "\uD83D\uDEABAn ordinary comrade can't change the credits of a great party! Great Leader Xi is watching over you!",
            replyToMessageId = message.messageId,
            disableNotification = true
        )
    }

    fun MessageHandlerEnvironment.sendCreditingBotProhibitionMessage(message: Message) {
        bot.sendMessage(
            chatId = ChatId.fromId(message.chat.id),
            text = "\uD83D\uDEABThe party prohibits crediting bots. Great Leader Xi is watching over you!",
            replyToMessageId = message.messageId,
            disableNotification = true
        )
    }

    private fun MessageHandlerEnvironment.sendCoolDownMessage(message: Message) {
        bot.sendMessage(
            chatId = ChatId.fromId(message.chat.id),
            text = "Slow down, comrade! One vote for the same comrade every 5 minutes.",
            replyToMessageId = message.messageId,
            disableNotification = true
        )
    }

    private fun MessageHandlerEnvironment.sendMemberPermissionMessage(message: Message) {
        bot.sendAnimation(
            chatId = ChatId.fromId(message.chat.id),
            animation = TelegramFile.ByFileId(Gifs.COTTON_FARM_FILE_ID),
            replyToMessageId = message.messageId,
            disableNotification = true
        )
    }

    fun MessageHandlerEnvironment.sendUpdateUserSocialCreditResultMessage(
        message: Message,
        socialCreditsChange: Long,
        ratingRepository: RatingRepository
    ) {
        message.replyToMessage?.from?.let { targetUser ->
            val messageSender = message.from
            val groupId = message.chat.id
            val groupTitle = message.chat.title
            val userId = targetUser.id
            val username = targetUser.username
            val firstName = targetUser.firstName

            val chatMember = messageSender?.let { user ->
                bot.getChatMember(chatId = ChatId.fromId(groupId), userId = user.id).get()
            }

            when (chatMember?.status) {
                Constants.CHAT_MEMBER_STATUS_CREATOR, Constants.CHAT_MEMBER_STATUS_ADMIN -> {
                    val previousCredit = ratingRepository.getUserRating(groupId, userId)?.socialCredits ?: 0L
                    val updateUserRatingResult = ratingRepository.updateUserRating(
                        messageSenderId = messageSender.id,
                        groupId = groupId,
                        groupTitle = groupTitle ?: "-",
                        userId = userId,
                        username = username ?: "-",
                        firstName = firstName,
                        socialCreditsChange = socialCreditsChange
                    )

                    val socialCreditChangeText = if (socialCreditsChange > 0) {
                        "Plus ${socialCreditsChange.absoluteValue} social credits for ${targetUser.firstName}."
                    } else {
                        "Minus ${socialCreditsChange.absoluteValue} social credits for ${targetUser.firstName}."
                    }

                    updateUserRatingResult.onSuccess { userRatingInfo ->
                        val sendToUyghurCampText = Jobs.sendToUyghurCampIfNeeded(
                            previousSocialCredit = previousCredit,
                            currentSocialCredit = userRatingInfo.socialCredits,
                            user = targetUser
                        )

                        val messageBuilder = StringBuilder().apply {
                            append(socialCreditChangeText)
                            append("\n")
                            append("Current Social Credits: ")
                            append(userRatingInfo.socialCredits)
                            append("\n\n")

                            when {
                                userRatingInfo.socialCredits > 100 -> {
                                    append("\uD83E\uDEE1The party is proud of you comrade.")
                                }
                                userRatingInfo.socialCredits < 0 -> {
                                    append("\uD83D\uDE1EYou're disappointing the party comrade.")
                                }
                            }

                            sendToUyghurCampText?.let {
                                append("\n\n\n")
                                append(it)
                            }
                        }

                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            text = messageBuilder.toString(),
                            disableNotification = true
                        )

                        if (sendToUyghurCampText != null) {
                            bot.sendAnimation(
                                chatId = ChatId.fromId(message.chat.id),
                                animation = TelegramFile.ByFileId(Gifs.POOH_AND_CJ_FILE_ID),
                                disableNotification = true
                            )
                        }

                        if (userRatingInfo.socialCredits >= Constants.MIN_SOCIAL_CREDITS_FOR_PROUD_PARTY_GIF) {
                            bot.sendAnimation(
                                chatId = ChatId.fromId(message.chat.id),
                                animation = TelegramFile.ByFileId(Gifs.POOH_DANCING_FILE_ID),
                                disableNotification = true
                            )
                        }
                    }

                    updateUserRatingResult.onFailure {
                        sendCoolDownMessage(message)
                    }
                }
                Constants.CHAT_MEMBER_STATUS_MEMBER -> sendMemberPermissionMessage(message)
                else -> sendMemberPermissionMessage(message)
            }
        }
    }
}