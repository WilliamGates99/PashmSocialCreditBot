package presentation

import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message

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
}