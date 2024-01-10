package util

import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.TelegramFile
import data.RatingRepository
import util.Constants.MIN_SOCIAL_CREDITS_FOR_PROUD_PARTY_MESSAGE
import kotlin.math.absoluteValue

object MessageHelper {

    fun MessageHandlerEnvironment.sendStickerSet(message: Message) {
        bot.sendSticker(
            chatId = ChatId.fromId(message.chat.id),
            sticker = Stickers.STICKER_SET_FILE_ID,
            disableNotification = true,
            replyMarkup = null
        )
    }

    fun MessageHandlerEnvironment.sendLongLiveTheKingSticker(message: Message) {
        bot.sendSticker(
            chatId = ChatId.fromId(message.chat.id),
            sticker = Stickers.HOLY_KING_FILE_ID,
            replyToMessageId = message.messageId,
            disableNotification = true,
            replyMarkup = null
        )
    }

    fun MessageHandlerEnvironment.sendWomenGif(message: Message) {
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
                    val previousSocialCredits = ratingRepository.getUserSocialCredits(
                        groupId = groupId,
                        userId = userId
                    )?.socialCredits ?: 0L

                    val updateUserSocialCreditsResult = ratingRepository.updateUserSocialCredits(
                        messageSenderId = messageSender.id,
                        groupId = groupId,
                        groupTitle = groupTitle ?: "-",
                        userId = userId,
                        username = username ?: "-",
                        firstName = firstName,
                        socialCreditsChange = socialCreditsChange
                    )

                    val socialCreditChangeText = if (socialCreditsChange > 0) {
                        "Plus ${socialCreditsChange.absoluteValue} social credits for $firstName."
                    } else {
                        "Minus ${socialCreditsChange.absoluteValue} social credits for $firstName."
                    }

                    updateUserSocialCreditsResult.onSuccess { userSocialCreditsInfo ->
                        val currentSocialCredits = userSocialCreditsInfo.socialCredits
                        val isSendingToUyghurCamp = previousSocialCredits >= 0L && currentSocialCredits < 0L
                        val isReturningFromUyghurCamp = previousSocialCredits < 0L && currentSocialCredits >= 0L

                        val messageBuilder = StringBuilder().apply {
                            append(socialCreditChangeText)
                            append("\n")
                            append("Current Social Credits: ")
                            append(currentSocialCredits)

                            when {
                                currentSocialCredits >= MIN_SOCIAL_CREDITS_FOR_PROUD_PARTY_MESSAGE -> {
                                    append("\n\n")
                                    append("\uD83E\uDEE1The party is proud of you comrade.")
                                }
                                currentSocialCredits < 0 -> {
                                    append("\n\n")
                                    append("\uD83D\uDE1EYou're disappointing the party comrade.")
                                }
                            }

                            if (isSendingToUyghurCamp) {
                                val uyghurJob = Jobs.uyghurCampJobs.random()
                                append("\n\n\n")
                                append("\uD83C\uDF34The party has decided to send Comrade $firstName to a Uyghur camp where he will be $uyghurJob. The Party is taking care of the bad comrades.\uD83D\uDC6E\uD83C\uDFFB\u200D♂\uFE0F")
                            }

                            if (isReturningFromUyghurCamp) {
                                append("\n\n\n")
                                append("\uD83C\uDFE1The party has decided to return comrade $firstName from the Uyghur camp. Be careful from now on!\uD83D\uDC6E\uD83C\uDFFB\u200D♂\uFE0F")
                            }
                        }

                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            text = messageBuilder.toString(),
                            disableNotification = true
                        )

                        if (isSendingToUyghurCamp) {
                            bot.sendAnimation(
                                chatId = ChatId.fromId(message.chat.id),
                                animation = TelegramFile.ByFileId(Gifs.POOH_AND_CJ_FILE_ID),
                                disableNotification = true
                            )
                        }

                        if (currentSocialCredits >= Constants.MIN_SOCIAL_CREDITS_FOR_PROUD_PARTY_GIF) {
                            bot.sendAnimation(
                                chatId = ChatId.fromId(message.chat.id),
                                animation = TelegramFile.ByFileId(Gifs.JOHN_XINA_FILE_ID),
                                disableNotification = true
                            )
                        }
                    }

                    updateUserSocialCreditsResult.onFailure {
                        sendCoolDownMessage(message)
                    }
                }
                Constants.CHAT_MEMBER_STATUS_MEMBER -> sendMemberPermissionMessage(message)
                else -> sendMemberPermissionMessage(message)
            }
        }
    }
}