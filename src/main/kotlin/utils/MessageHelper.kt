package utils

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.chat.members.getChatMember
import dev.inmo.tgbotapi.extensions.api.send.media.sendAnimation
import dev.inmo.tgbotapi.extensions.api.send.media.sendSticker
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.reply_to_message
import dev.inmo.tgbotapi.requests.abstracts.InputFile
import dev.inmo.tgbotapi.types.ReplyParameters
import dev.inmo.tgbotapi.types.chat.ForumChatImpl
import dev.inmo.tgbotapi.types.chat.GroupChatImpl
import dev.inmo.tgbotapi.types.chat.SupergroupChatImpl
import dev.inmo.tgbotapi.types.chat.member.ChatMember
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.abstracts.Message
import dev.inmo.tgbotapi.types.message.content.TextedMediaContent
import dev.inmo.tgbotapi.utils.RiskFeature
import domain.repositories.RatingRepository
import utils.Constants.MESSAGE_BIG_MASOUD
import utils.Constants.MESSAGE_KING_MASOUD
import utils.Constants.MESSAGE_LONG_LIVE_THE_KING
import utils.Constants.MESSAGE_MASOUD
import utils.Constants.MESSAGE_WOMEN
import utils.Constants.MESSAGE_WOMEN_BRAIN
import utils.Constants.MESSAGE_WOMEN_COFFEE_1
import utils.Constants.MESSAGE_WOMEN_COFFEE_2
import utils.Constants.MIN_SOCIAL_CREDITS_FOR_PROUD_PARTY_MESSAGE
import utils.Constants.RATING_COOL_DOWN_IN_MINUTES
import utils.Constants.SOCIAL_CLASS_CREDIT_NEGATIVE_1000
import utils.Constants.SOCIAL_CREDITS_FOR_EXECUTION_MESSAGE
import utils.Constants.THROWABLE_MESSAGE_COOL_DOWN
import java.util.*

object MessageHelper {

    suspend fun TelegramBot.sendStickerSet(message: Message) {
        sendSticker(
            sticker = InputFile.fromId(id = Stickers.STICKER_SET_FILE_ID),
            chat = message.chat,
            disableNotification = true
        )
    }

    fun shouldSendLongLiveTheKingSticker(message: ContentMessage<TextedMediaContent>) = when {
        message.content.text?.lowercase(Locale.US)
            ?.contains(MESSAGE_LONG_LIVE_THE_KING) == true -> true
        message.content.text?.lowercase(Locale.US)?.contains(MESSAGE_KING_MASOUD) == true -> true
        message.content.text?.lowercase(Locale.US)?.contains(MESSAGE_BIG_MASOUD) == true -> true
        message.content.text?.lowercase(Locale.US)?.contains(MESSAGE_MASOUD) == true -> true
        else -> false
    }

    fun shouldSendWomenGif(message: ContentMessage<TextedMediaContent>) = when {
        message.content.text?.lowercase(Locale.US)?.contains(MESSAGE_WOMEN_COFFEE_1) == true -> true
        message.content.text?.lowercase(Locale.US)?.contains(MESSAGE_WOMEN_COFFEE_2) == true -> true
        message.content.text?.lowercase(Locale.US)?.contains(MESSAGE_WOMEN) == true -> true
        message.content.text?.lowercase(Locale.US)?.contains(MESSAGE_WOMEN_BRAIN) == true -> true
        else -> false
    }

    suspend fun TelegramBot.sendLongLiveTheKingSticker(message: Message) {
        sendSticker(
            sticker = InputFile.fromId(id = Stickers.HOLY_KING_FILE_ID),
            chat = message.chat,
            replyParameters = ReplyParameters(message = message),
            disableNotification = true
        )
    }

    suspend fun TelegramBot.sendWomenGif(message: Message) {
        sendAnimation(
            animation = InputFile.fromId(id = Gifs.WOMEN_FILE_ID),
            chat = message.chat,
            replyParameters = ReplyParameters(message = message),
            disableNotification = true
        )
    }

    suspend fun TelegramBot.sendNotGroupMessage(message: Message) {
        sendMessage(
            text = "\uD83D\uDE44The Social Credit System only works in groups.",
            chat = message.chat,
            disableNotification = true
        )
    }

    suspend fun TelegramBot.sendCreditingYourselfProhibitionMessage(message: Message) {
        sendMessage(
            text = "\uD83D\uDEABThe party prohibits crediting yourself. *Great Leader Xi* is watching over you!",
            chat = message.chat,
            replyParameters = ReplyParameters(message = message),
            disableNotification = true,
            parseMode = MarkdownParseMode
        )
    }

    suspend fun TelegramBot.sendCreditingSocialCreditBotProhibitionMessage(message: Message) {
        sendMessage(
            text = "\uD83D\uDEABAn ordinary comrade can't change the credits of the great party! *Great Leader Xi* is watching over you!",
            chat = message.chat,
            replyParameters = ReplyParameters(message = message),
            disableNotification = true,
            parseMode = MarkdownParseMode
        )
    }

    suspend fun TelegramBot.sendCreditingBotProhibitionMessage(message: Message) {
        sendMessage(
            text = "\uD83D\uDEABThe party prohibits crediting other bots. *Great Leader Xi* is watching over you!",
            chat = message.chat,
            replyParameters = ReplyParameters(message = message),
            disableNotification = true,
            parseMode = MarkdownParseMode
        )
    }

    private suspend fun TelegramBot.sendCoolDownMessage(message: Message) {
        sendMessage(
            text = "Slow down, comrade! One vote for the same comrade every *$RATING_COOL_DOWN_IN_MINUTES minutes*.",
            chat = message.chat,
            replyParameters = ReplyParameters(message = message),
            disableNotification = true,
            parseMode = MarkdownParseMode
        )
    }

    private suspend fun TelegramBot.sendMemberPermissionMessage(message: Message) {
        sendAnimation(
            chat = message.chat,
            animation = InputFile.fromId(id = Gifs.COTTON_FARM_FILE_ID),
            replyParameters = ReplyParameters(message = message),
            disableNotification = true
        )
    }

    suspend fun TelegramBot.sendZeroChangeTrollMessage(message: Message) {
        sendMessage(
            text = "±0 social credits does nothing, you fucking *RETARD*.",
            chat = message.chat,
            replyParameters = ReplyParameters(message = message),
            disableNotification = true,
            parseMode = MarkdownParseMode
        )
    }

    @OptIn(RiskFeature::class)
    suspend fun TelegramBot.sendUpdateUserSocialCreditResultMessage(
        message: Message,
        socialCreditsChange: Long,
        ratingRepository: RatingRepository
    ) {
        message.reply_to_message?.from?.let { targetUser ->
            val messageSender = message.from
            val groupId = message.chat.id.chatId.long
            val groupTitle = when (message.chat) {
                is GroupChatImpl -> (message.chat as GroupChatImpl).title
                is SupergroupChatImpl -> (message.chat as SupergroupChatImpl).title
                is ForumChatImpl -> (message.chat as ForumChatImpl).title
                else -> "-"
            }
            val userId = targetUser.id.chatId.long
            val username = targetUser.username?.withoutAt ?: "-"
            val firstName = targetUser.firstName

            val chatMember = messageSender?.let { user ->
                getChatMember(
                    chatId = message.chat.id,
                    user = user
                )
            }

            val messageSenderStatus = chatMember?.status
            if (messageSenderStatus != null) {
                val previousSocialCredits = ratingRepository.getUserSocialCredits(
                    groupId = groupId,
                    userId = userId
                )?.socialCredits ?: 0L

                val isGivingNegativeCreditToExecutedComrade = previousSocialCredits <= SOCIAL_CLASS_CREDIT_NEGATIVE_1000
                        && socialCreditsChange < 0

                if (isGivingNegativeCreditToExecutedComrade) {
                    sendMessage(
                        text = "\uD83D\uDE35There's no comrade by this name in the party.",
                        chat = message.chat,
                        disableNotification = true,
                        parseMode = MarkdownParseMode
                    )
                    return
                }

                val updateUserSocialCreditsResult = ratingRepository.updateUserSocialCredits(
                    messageSenderId = messageSender.id.chatId.long,
                    messageSenderStatus = messageSenderStatus,
                    groupId = groupId,
                    groupTitle = groupTitle,
                    userId = userId,
                    username = username,
                    firstName = firstName,
                    socialCreditsChange = socialCreditsChange
                )

                updateUserSocialCreditsResult.onSuccess { userSocialCreditsInfo ->
                    val currentSocialCredits = userSocialCreditsInfo.socialCredits
                    val isSendingToUyghurCamp = previousSocialCredits >= 0L && currentSocialCredits < 0L
                    val isReturningFromUyghurCamp = previousSocialCredits < 0L && currentSocialCredits >= 0L

                    val isExecutingComrade = SOCIAL_CREDITS_FOR_EXECUTION_MESSAGE in
                            currentSocialCredits..<previousSocialCredits

                    val isRevivingComrade = SOCIAL_CREDITS_FOR_EXECUTION_MESSAGE in
                            previousSocialCredits..<currentSocialCredits

                    val messageBuilder = StringBuilder().apply {
                        when {
                            currentSocialCredits >= MIN_SOCIAL_CREDITS_FOR_PROUD_PARTY_MESSAGE -> {
                                append("\uD83E\uDEE1The party is proud of comrade *$firstName* with $currentSocialCredits social credits.")
                            }
                            isRevivingComrade -> {
                                append("\uD83D\uDE2EWow! Comrade *$firstName* is miraculously revived with $currentSocialCredits social credits.")
                            }
                            isExecutingComrade -> {
                                append("\uD83D\uDE24The Party has had enough of comrade *$firstName* with $currentSocialCredits social credits. Even the Uyghur camp couldn't discipline this asshole. Comrade *will be executed* at dawn.☠\uFE0F")
                                append("\n\n")
                                append("Enjoy your last meal comrade.\uD83C\uDF46")
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

                    sendMessage(
                        text = messageBuilder.toString(),
                        chat = message.chat,
                        disableNotification = true,
                        parseMode = MarkdownParseMode
                    )

                    if (isSendingToUyghurCamp) {
                        sendAnimation(
                            animation = InputFile.fromId(id = Gifs.POOH_AND_CJ_FILE_ID),
                            chat = message.chat,
                            disableNotification = true
                        )
                    }

                    if (isExecutingComrade) {
                        sendAnimation(
                            animation = InputFile.fromId(id = Gifs.EXECUTION_FILE_ID),
                            chat = message.chat,
                            disableNotification = true
                        )
                    }

                    if (isRevivingComrade) {
                        sendAnimation(
                            animation = InputFile.fromId(id = Gifs.REVIVALE_FILE_ID),
                            chat = message.chat,
                            disableNotification = true
                        )
                    }

                    if (currentSocialCredits >= Constants.MIN_SOCIAL_CREDITS_FOR_PROUD_PARTY_GIF) {
                        sendAnimation(
                            animation = InputFile.fromId(id = Gifs.JOHN_XINA_FILE_ID),
                            chat = message.chat,
                            disableNotification = true
                        )
                    }
                }

                updateUserSocialCreditsResult.onFailure {
                    println(it.message)

                    val shouldSendCoolDownMessage = it.message == THROWABLE_MESSAGE_COOL_DOWN
                    if (shouldSendCoolDownMessage) {
                        when (messageSenderStatus) {
                            ChatMember.Status.Creator -> sendCoolDownMessage(message)
                            ChatMember.Status.Administrator -> sendCoolDownMessage(message)
                            ChatMember.Status.Member -> sendMemberPermissionMessage(message)
                            else -> Unit
                        }
                    }
                }
            } else {
                sendMemberPermissionMessage(message)
            }
        }
    }
}