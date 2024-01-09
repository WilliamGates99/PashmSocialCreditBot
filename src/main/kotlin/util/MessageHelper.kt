package util

import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import data.RatingRepository
import kotlin.math.absoluteValue

object MessageHelper {

    fun MessageHandlerEnvironment.sendNotGroupMessage(message: Message) {
        bot.sendMessage(
            chatId = ChatId.fromId(message.chat.id),
            text = "Du-uh, The Social Credit System only works in groups.",
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
            text = "\uD83D\uDEABThe party prohibits crediting other robots. Great Leader Xi is watching over you!",
            replyToMessageId = message.messageId,
            disableNotification = true
        )
    }

    fun MessageHandlerEnvironment.sendCoolDownMessage(message: Message) {
        bot.sendMessage(
            chatId = ChatId.fromId(message.chat.id),
            text = "Slow down, comrade! One vote for the same comrade every 5 minutes.",
            replyToMessageId = message.messageId,
            disableNotification = true
        )
    }

    fun MessageHandlerEnvironment.sendMemberPermissionMessage(message: Message) {
        bot.sendMessage(
            chatId = ChatId.fromId(message.chat.id),
            text = "کاکا سیاه نادان به مزرعه پنبه برگرد",
            replyToMessageId = message.messageId,
            disableNotification = true
        )
    }

    fun MessageHandlerEnvironment.sendUpdateUserSocialCreditResultMessage(
        message: Message,
        socialCreditChange: Long,
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
                    val previousCredit = ratingRepository.getUserRating(groupId, userId)?.rating ?: 0L
                    val updateUserRatingResult = ratingRepository.updateUserRating(
                        messageSenderId = messageSender.id,
                        groupId = groupId,
                        groupTitle = groupTitle ?: "-",
                        userId = userId,
                        username = username ?: "-",
                        firstName = firstName,
                        ratingChange = socialCreditChange
                    )

                    val socialCreditChangeText = if (socialCreditChange > 0) {
                        "Plus ${socialCreditChange.absoluteValue} social credits for ${targetUser.firstName}. The party is proud of you comrade\uD83E\uDEE1"
                    } else {
                        "Minus ${socialCreditChange.absoluteValue} social credits for ${targetUser.firstName}. You're disappointing the party comrade\uD83D\uDE1E"
                    }

                    updateUserRatingResult.onSuccess { userRatingInfo ->
                        val sendToUyghurCampText = Jobs.sendToUyghurCampIfNeeded(
                            previousSocialCredit = previousCredit,
                            currentSocialCredit = userRatingInfo.rating,
                            user = targetUser
                        )

                        val messageBuilder = StringBuilder().apply {
                            append(socialCreditChangeText)
                            append("\n")
                            append("Current Social Credits: ")
                            append(userRatingInfo.rating)

                            sendToUyghurCampText?.let {
                                append("\n\n")
                                append(it)
                            }
                        }

                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            text = messageBuilder.toString(),
                            disableNotification = true
                        )
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