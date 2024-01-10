package util

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import data.RatingRepository
import util.Constants.COMMAND_SHOW_MY_CREDITS
import util.Constants.COMMAND_SHOW_OTHERS_CREDITS

object CommandHelper {

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
            val userRatingInfo = ratingRepository.getUserSocialCredits(
                groupId = message.chat.id,
                userId = user.id
            )
            val userSocialCredit = userRatingInfo?.socialCredits ?: 0L

            bot.sendMessage(
                chatId = ChatId.fromId(message.chat.id),
                text = "The Party informs that Comrade <b>${user.firstName}</b> has $userSocialCredit social credits.",
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
                text = "⚠\uFE0FReply to someone with /$COMMAND_SHOW_OTHERS_CREDITS to find out their social credits!",
                replyToMessageId = message.messageId,
                disableNotification = true
            )
            return
        } else {
            val isUserReplyingThemself = message.from?.id == repliedUser.id
            if (isUserReplyingThemself) {
                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    text = "⚠\uFE0FUse /$COMMAND_SHOW_MY_CREDITS command to find out your social credits!",
                    replyToMessageId = message.messageId,
                    disableNotification = true
                )
                return
            }

            val userRatingInfo = ratingRepository.getUserSocialCredits(
                groupId = message.chat.id,
                userId = repliedUser.id
            )
            val userSocialCredit = userRatingInfo?.socialCredits ?: 0L

            bot.sendMessage(
                chatId = ChatId.fromId(message.chat.id),
                text = "The Party informs that Comrade <b>${repliedUser.firstName}</b> has $userSocialCredit social credits.",
                disableNotification = true
            )
        }
    }

    fun CommandHandlerEnvironment.showGroupSocialCreditsList(
        message: Message,
        ratingRepository: RatingRepository
    ) {
        val stringBuilder = StringBuilder()
        val groupSocialCreditsList = ratingRepository
            .getGroupSocialCreditsList(groupId = message.chat.id)

        if (groupSocialCreditsList.isEmpty()) {
            stringBuilder.append("Every comrade has 0 social credits. Be careful, <b>great Leader Xi</b> is watching over you!")
        } else {
            stringBuilder.apply {
                append("\uD83D\uDCE3Comrades, listen carefully!")
                append("\n")
                append("\uD83D\uDCDCIn the name of our <b>great leader Xi</b>, the party has published a list of the top comrades based on their social credits:")
                append("\n\n\n")
            }

            groupSocialCreditsList.forEachIndexed { index, userSocialCreditsInfo ->
                stringBuilder.apply {
                    userSocialCreditsInfo.apply { append("${index + 1}. $firstName — $socialCredits") }
                    append("\n")
                }
            }
        }

        bot.sendMessage(
            chatId = ChatId.fromId(message.chat.id),
            text = stringBuilder.toString(),
            disableNotification = true
        )
    }
}