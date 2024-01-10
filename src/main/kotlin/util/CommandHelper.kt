package util

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import data.RatingRepository

object CommandHelper {

    fun CommandHandlerEnvironment.sendStickerSet(message: Message) {
        bot.sendSticker(
            chatId = ChatId.fromId(message.chat.id),
            sticker = Stickers.STICKER_SET_FILE_ID,
            replyToMessageId = message.messageId,
            disableNotification = true,
            replyMarkup = null
        )
    }

    fun CommandHandlerEnvironment.sendNotGroupMessage(message: Message) {
        bot.sendMessage(
            chatId = ChatId.fromId(message.chat.id),
            text = "\uD83D\uDE44The Social Credit System only works in groups.",
            disableNotification = true
        )
    }

    fun CommandHandlerEnvironment.showMyCredits(
        message: Message,
        ratingRepository: RatingRepository
    ) {
        message.from?.let { user ->
            val userRatingInfo = ratingRepository.getUserRating(
                groupId = message.chat.id,
                userId = user.id
            )
            val userSocialCredit = userRatingInfo?.socialCredits ?: 0L

            bot.sendMessage(
                chatId = ChatId.fromId(message.chat.id),
                text = "The Party informs that Comrade ${user.firstName} has $userSocialCredit social credits.",
                disableNotification = true
            )
        }
    }

    fun CommandHandlerEnvironment.showOthersCredits(
        message: Message,
        ratingRepository: RatingRepository
    ) {
        val repliedUser = message.replyToMessage?.from
        if (repliedUser == null) {
            bot.sendMessage(
                chatId = ChatId.fromId(message.chat.id),
                text = "⚠\uFE0FReply to someone with /credits to find out their social credits!",
                replyToMessageId = message.messageId,
                disableNotification = true
            )
            return
        } else {
            val isUserReplyingThemself = message.from?.id == repliedUser.id
            if (isUserReplyingThemself) {
                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    text = "⚠\uFE0FUse /mycredits command to find out your social credits!",
                    replyToMessageId = message.messageId,
                    disableNotification = true
                )
                return
            }

            val userRatingInfo = ratingRepository.getUserRating(
                groupId = message.chat.id,
                userId = repliedUser.id
            )
            val userSocialCredit = userRatingInfo?.socialCredits ?: 0L

            bot.sendMessage(
                chatId = ChatId.fromId(message.chat.id),
                text = "The Party informs that Comrade ${repliedUser.firstName} has $userSocialCredit social credits.",
                disableNotification = true
            )
        }
    }
}