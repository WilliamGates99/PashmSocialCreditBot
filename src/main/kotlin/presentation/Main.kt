package presentation

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId
import data.RatingRepository
import data.RatingRepositoryImpl
import util.ChatTypes
import util.CommandHelper.sendNotGroupMessage
import util.CommandHelper.sendStickerSet
import util.CommandHelper.showMyCredits
import util.CommandHelper.showOthersCredits
import util.Constants
import util.MessageHelper.sendCreditingBotProhibitionMessage
import util.MessageHelper.sendCreditingSocialCreditBotProhibitionMessage
import util.MessageHelper.sendCreditingYourselfProhibitionMessage
import util.MessageHelper.sendNotGroupMessage
import util.MessageHelper.sendUpdateUserSocialCreditResultMessage
import util.PropertiesHelper
import util.Stickers.getSocialCreditChange

fun main(args: Array<String>) {
    val propertiesHelper = PropertiesHelper(propertiesFilePath = args[0])
    val ratingRepository: RatingRepository = RatingRepositoryImpl(dbPath = propertiesHelper.getDbPath())

    val bot = bot {
        token = propertiesHelper.getBotToken()

        dispatch {
            command(Constants.COMMAND_GET_STICKER_SET) {
                sendStickerSet(message)
            }

            command(Constants.COMMAND_SHOW_MY_CREDITS) {
                when (message.chat.type) {
                    ChatTypes.SUPERGROUP.value, ChatTypes.GROUP.value -> showMyCredits(message, ratingRepository)
                    ChatTypes.PRIVATE.value -> sendNotGroupMessage(message)
                }
            }

            command(Constants.COMMAND_SHOW_OTHERS_CREDITS) {
                when (message.chat.type) {
                    ChatTypes.SUPERGROUP.value, ChatTypes.GROUP.value -> showOthersCredits(message, ratingRepository)
                    ChatTypes.PRIVATE.value -> sendNotGroupMessage(message)
                }
            }

            // TODO: UPDATE RANK COMMAND
            command(Constants.COMMAND_SHOW_CITIZENS_RANK) {
                val ratings = ratingRepository
                    .getRatingsList()
                    .associate { info ->
                        info.username to info.rating
                    }

                val stringBuilder = StringBuilder().apply {
                    append("⚡ Товарищ, слушай внимательно великий лидер Xi!\n")
                    append("\uD83D\uDCC8 Партия публиковать списки социальный рейтинг:\n\n")
                }

                ratings.forEach { (username, credit) ->
                    if (credit > 0) {
                        stringBuilder.append("\uD83D\uDC4D Партия гордится товарищ $username с рейтинг $credit\n")
                    } else {
                        stringBuilder.append("\uD83D\uDC4E Ну и ну! Товарищ $username разочаровывай партия своим рейтинг $credit\n")
                    }
                }

                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    text = stringBuilder.toString(),
                    disableNotification = true,
                    replyToMessageId = message.messageId
                )
            }

            message {
                if (message.sticker == null || message.replyToMessage?.from == null) {
                    return@message
                }

                val isUserReplyingThemself = message.from?.id == message.replyToMessage?.from?.id
                if (isUserReplyingThemself) {
                    when (message.chat.type) {
                        ChatTypes.SUPERGROUP.value, ChatTypes.GROUP.value -> sendCreditingYourselfProhibitionMessage(
                            message
                        )
                        ChatTypes.PRIVATE.value -> sendNotGroupMessage(message)
                    }
                    return@message
                }

                val botUser = bot.getMe().get()
                val isUserReplyingSocialCreditBot = message.replyToMessage?.from?.username == botUser.username
                if (isUserReplyingSocialCreditBot) {
                    when (message.chat.type) {
                        ChatTypes.SUPERGROUP.value, ChatTypes.GROUP.value -> sendCreditingSocialCreditBotProhibitionMessage(
                            message
                        )
                        ChatTypes.PRIVATE.value -> sendNotGroupMessage(message)
                    }
                    return@message
                }

                val isReplyingToBot = message.replyToMessage?.from?.isBot == true
                if (isReplyingToBot) {
                    when (message.chat.type) {
                        ChatTypes.SUPERGROUP.value, ChatTypes.GROUP.value -> sendCreditingBotProhibitionMessage(message)
                        ChatTypes.PRIVATE.value -> sendNotGroupMessage(message)
                    }
                    return@message
                }

                when (message.chat.type) {
                    ChatTypes.SUPERGROUP.value, ChatTypes.GROUP.value -> {
                        val socialCreditChange = message.getSocialCreditChange() ?: return@message

                        sendUpdateUserSocialCreditResultMessage(
                            message = message,
                            socialCreditChange = socialCreditChange,
                            ratingRepository = ratingRepository
                        )
                    }
                    ChatTypes.PRIVATE.value -> sendNotGroupMessage(message)
                }
            }
        }
    }
    bot.startPolling()
}