package util

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.ParseMode
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
                text = "The Party informs that Comrade *${user.firstName}* has $userSocialCredit social credits.",
                disableNotification = true,
                parseMode = ParseMode.MARKDOWN
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
                text = "The Party informs that Comrade *${repliedUser.firstName}* has $userSocialCredit social credits.",
                disableNotification = true,
                parseMode = ParseMode.MARKDOWN
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
            stringBuilder.append("Every comrade has 0 social credits. Be careful, *great Leader Xi* is watching over you!")
        } else {
            stringBuilder.apply {
                append("_\uD83D\uDCE3Comrades, listen carefully!_")
                append("\n")
                append("_\uD83D\uDCDCIn the name of our *great leader Xi*, the party has published a list of the top comrades based on their social credits:_")
                append("\n\n")
                append("||") // Spoiler Markdown
            }

            groupSocialCreditsList.forEachIndexed { index, userSocialCreditsInfo ->
                stringBuilder.apply {
                    userSocialCreditsInfo.apply {
                        val socialClass = SocialClass.getComradeSocialClass(socialCredits)
                        append("${index + 1}. $firstName ― $socialClass: $socialCredits credits")
                    }
                    append("\n")
                }
            }

            stringBuilder.append("||") // Spoiler Markdown
        }

        bot.sendMessage(
            chatId = ChatId.fromId(message.chat.id),
            text = stringBuilder.toString(),
            disableNotification = true,
            parseMode = ParseMode.MARKDOWN
        )
    }
}