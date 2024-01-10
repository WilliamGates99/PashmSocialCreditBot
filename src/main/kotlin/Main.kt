import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.message
import data.RatingRepository
import data.RatingRepositoryImpl
import util.ChatTypes
import util.CommandHelper.sendNotGroupMessage
import util.CommandHelper.sendStickerSet
import util.CommandHelper.showGroupSocialCreditsList
import util.CommandHelper.showMyCredits
import util.CommandHelper.showOthersCredits
import util.Constants
import util.Constants.MESSAGE_LONG_LIVE_THE_KING
import util.Constants.MESSAGE_WOMEN
import util.MessageHelper.sendCreditingBotProhibitionMessage
import util.MessageHelper.sendCreditingSocialCreditBotProhibitionMessage
import util.MessageHelper.sendCreditingYourselfProhibitionMessage
import util.MessageHelper.sendLongLiveTheKingSticker
import util.MessageHelper.sendNotGroupMessage
import util.MessageHelper.sendUpdateUserSocialCreditResultMessage
import util.MessageHelper.sendWomenGif
import util.PropertiesHelper
import util.Stickers.getSocialCreditChange
import java.util.*

fun main(args: Array<String>) {
    val propertiesHelper = PropertiesHelper(propertiesFilePath = args[0])
    val ratingRepository: RatingRepository = RatingRepositoryImpl(dbPath = propertiesHelper.getDbPath())

    val telegramBot = bot {
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

            command(Constants.COMMAND_SHOW_COMRADES_RANK) {
                when (message.chat.type) {
                    ChatTypes.SUPERGROUP.value, ChatTypes.GROUP.value -> {
                        showGroupSocialCreditsList(message, ratingRepository)
                    }
                    ChatTypes.PRIVATE.value -> sendNotGroupMessage(message)
                }
            }

            message {
                val messageContainsLongLiveTheKing = message.text?.lowercase(Locale.US) == MESSAGE_LONG_LIVE_THE_KING
                val captionContainsLongLiveTheKing = message.caption?.lowercase(Locale.US) == MESSAGE_LONG_LIVE_THE_KING
                val shouldSendLongLiveTheKingSticker = messageContainsLongLiveTheKing || captionContainsLongLiveTheKing
                if (shouldSendLongLiveTheKingSticker) {
                    sendLongLiveTheKingSticker(message)
                    return@message
                }

                val messageContainsWomen = message.text?.lowercase(Locale.US) == MESSAGE_WOMEN
                val captionContainsWomen = message.caption?.lowercase(Locale.US) == MESSAGE_WOMEN
                val shouldSendWomenGif = messageContainsWomen || captionContainsWomen
                if (shouldSendWomenGif) {
                    sendWomenGif(message)
                    return@message
                }

                if (message.sticker == null || message.replyToMessage?.from == null) {
                    return@message
                }

                val isUserReplyingThemself = message.from?.id == message.replyToMessage?.from?.id
                if (isUserReplyingThemself) {
                    when (message.chat.type) {
                        ChatTypes.SUPERGROUP.value, ChatTypes.GROUP.value -> {
                            sendCreditingYourselfProhibitionMessage(message)
                        }
                        ChatTypes.PRIVATE.value -> sendNotGroupMessage(message)
                    }
                    return@message
                }

                val botUser = bot.getMe().get()
                val isUserReplyingSocialCreditBot = message.replyToMessage?.from?.username == botUser.username
                if (isUserReplyingSocialCreditBot) {
                    when (message.chat.type) {
                        ChatTypes.SUPERGROUP.value, ChatTypes.GROUP.value -> {
                            sendCreditingSocialCreditBotProhibitionMessage(message)
                        }
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
                        val socialCreditsChange = message.getSocialCreditChange() ?: return@message

                        sendUpdateUserSocialCreditResultMessage(
                            message = message,
                            socialCreditsChange = socialCreditsChange,
                            ratingRepository = ratingRepository
                        )
                    }
                    ChatTypes.PRIVATE.value -> sendNotGroupMessage(message)
                }
            }
        }
    }

    telegramBot.startPolling()
}