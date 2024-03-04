package util

import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.TelegramFile
import data.RatingRepository
import util.Constants.MIN_SOCIAL_CREDITS_FOR_PROUD_PARTY_MESSAGE
import util.Constants.RATING_COOL_DOWN_IN_MINUTES
import util.Constants.SOCIAL_CREDITS_FOR_EXECUTION_MESSAGE
import util.Constants.THROWABLE_MESSAGE_COOL_DOWN

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
            text = "\uD83D\uDEABThe party prohibits crediting yourself. *Great Leader Xi* is watching over you!",
            replyToMessageId = message.messageId,
            disableNotification = true,
            parseMode = ParseMode.MARKDOWN
        )
    }

    fun MessageHandlerEnvironment.sendCreditingSocialCreditBotProhibitionMessage(message: Message) {
        bot.sendMessage(
            chatId = ChatId.fromId(message.chat.id),
            text = "\uD83D\uDEABAn ordinary comrade can't change the credits of the great party! *Great Leader Xi* is watching over you!",
            replyToMessageId = message.messageId,
            disableNotification = true,
            parseMode = ParseMode.MARKDOWN
        )
    }

    fun MessageHandlerEnvironment.sendCreditingBotProhibitionMessage(message: Message) {
        bot.sendMessage(
            chatId = ChatId.fromId(message.chat.id),
            text = "\uD83D\uDEABThe party prohibits crediting other bots. *Great Leader Xi* is watching over you!",
            replyToMessageId = message.messageId,
            disableNotification = true,
            parseMode = ParseMode.MARKDOWN
        )
    }

    private fun MessageHandlerEnvironment.sendCoolDownMessage(message: Message) {
        bot.sendMessage(
            chatId = ChatId.fromId(message.chat.id),
            text = "Slow down, comrade! One vote for the same comrade every *$RATING_COOL_DOWN_IN_MINUTES minutes*.",
            replyToMessageId = message.messageId,
            disableNotification = true,
            parseMode = ParseMode.MARKDOWN
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

    fun MessageHandlerEnvironment.sendZeroChangeTrollMessage(message: Message) {
        bot.sendMessage(
            chatId = ChatId.fromId(message.chat.id),
            text = "±0 social credits does nothing, you fucking *RETARD*.",
            replyToMessageId = message.messageId,
            disableNotification = true,
            parseMode = ParseMode.MARKDOWN
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

            val messageSenderStatus = chatMember?.status
            if (messageSenderStatus != null) {
                val previousSocialCredits = ratingRepository.getUserSocialCredits(
                    groupId = groupId,
                    userId = userId
                )?.socialCredits ?: 0L

                val updateUserSocialCreditsResult = ratingRepository.updateUserSocialCredits(
                    messageSenderId = messageSender.id,
                    messageSenderStatus = messageSenderStatus,
                    groupId = groupId,
                    groupTitle = groupTitle ?: "-",
                    userId = userId,
                    username = username ?: "-",
                    firstName = firstName,
                    socialCreditsChange = socialCreditsChange
                )

                updateUserSocialCreditsResult.onSuccess { userSocialCreditsInfo ->
                    val currentSocialCredits = userSocialCreditsInfo.socialCredits
                    val isSendingToUyghurCamp = previousSocialCredits >= 0L && currentSocialCredits < 0L
                    val isReturningFromUyghurCamp = previousSocialCredits < 0L && currentSocialCredits >= 0L

                    @Suppress("ConvertTwoComparisonsToRangeCheck")
                    val isExecutingComrade = previousSocialCredits > SOCIAL_CREDITS_FOR_EXECUTION_MESSAGE &&
                            currentSocialCredits <= SOCIAL_CREDITS_FOR_EXECUTION_MESSAGE

                    @Suppress("ConvertTwoComparisonsToRangeCheck")
                    val isRevivingComrade = previousSocialCredits <= SOCIAL_CREDITS_FOR_EXECUTION_MESSAGE &&
                            SOCIAL_CREDITS_FOR_EXECUTION_MESSAGE < currentSocialCredits

                    val messageBuilder = StringBuilder().apply {
                        when {
                            currentSocialCredits >= MIN_SOCIAL_CREDITS_FOR_PROUD_PARTY_MESSAGE -> {
                                append("\uD83E\uDEE1The party is proud of comrade *$firstName* with $currentSocialCredits social credits.")
                            }
                            isExecutingComrade -> {
                                append("\uD83D\uDE24The Party has had enough of comrade *$firstName* with $currentSocialCredits social credits. Even the Uyghur camp couldn't discipline this asshole. Comrade *will be executed* at dawn.☠\uFE0F")
                                append("\n\n")
                                append("Enjoy your last meal comrade.\uD83C\uDF46")
                            }
                            isRevivingComrade -> {
                                append("\uD83D\uDE2EWow! Comrade $firstName* is miraculously revived with $currentSocialCredits social credits.")
                            }
                            currentSocialCredits <= SOCIAL_CREDITS_FOR_EXECUTION_MESSAGE -> {
                                append("Executed comrade *$firstName* has $currentSocialCredits social credits.")
                            }
                            currentSocialCredits < 0 -> {
                                append("\uD83D\uDE1EWow! Comrade *$firstName* is disappointing the party with $currentSocialCredits social credits.")
                            }
                            else -> {
                                append("Comrade *$firstName* has $currentSocialCredits social credits.")
                            }
                        }

                        if (isSendingToUyghurCamp) {
                            val uyghurJob = Jobs.uyghurCampJobs.random()
                            append("\n\n")
                            append("\uD83C\uDFD5The party has decided to send comrade to an Uyghur camp where he will be $uyghurJob.")
                            append("\n\n")
                            append("\uD83D\uDC6E\uD83C\uDFFBWorking in the camp will discipline bad citizens.‼\uFE0F")
                        }

                        if (isReturningFromUyghurCamp) {
                            append("\n\n\n")
                            append("\uD83C\uDFE1The party has decided to return comrade from the Uyghur camp. Be careful from now on!\uD83D\uDC6E\uD83C\uDFFB\u200D♂\uFE0F")
                        }
                    }

                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = messageBuilder.toString(),
                        disableNotification = true,
                        parseMode = ParseMode.MARKDOWN
                    )

                    if (isSendingToUyghurCamp) {
                        bot.sendAnimation(
                            chatId = ChatId.fromId(message.chat.id),
                            animation = TelegramFile.ByFileId(Gifs.POOH_AND_CJ_FILE_ID),
                            disableNotification = true
                        )
                    }

                    if (isExecutingComrade) {
                        bot.sendAnimation(
                            chatId = ChatId.fromId(message.chat.id),
                            animation = TelegramFile.ByFileId(Gifs.EXECUTION_FILE_ID),
                            disableNotification = true
                        )
                    }

                    if (isRevivingComrade) {
                        bot.sendAnimation(
                            chatId = ChatId.fromId(message.chat.id),
                            animation = TelegramFile.ByFileId(Gifs.REVIVALE_FILE_ID),
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
                    val shouldSendCoolDownMessage = it.message == THROWABLE_MESSAGE_COOL_DOWN
                    if (shouldSendCoolDownMessage) {
                        when (messageSenderStatus) {
                            Constants.CHAT_MEMBER_STATUS_CREATOR -> sendCoolDownMessage(message)
                            Constants.CHAT_MEMBER_STATUS_ADMIN -> sendCoolDownMessage(message)
                            Constants.CHAT_MEMBER_STATUS_MEMBER -> sendMemberPermissionMessage(message)
                        }
                    }
                }
            } else {
                sendMemberPermissionMessage(message)
            }
        }
    }
}