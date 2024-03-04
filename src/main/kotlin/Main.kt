import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.message
import data.RatingRepository
import data.RatingRepositoryImpl
import util.ChatTypes
import util.CommandHelper.sendNotGroupMessage
import util.CommandHelper.sendWinnieThePoohTextArt
import util.CommandHelper.sendXiJinpingTextArt
import util.CommandHelper.showGroupSocialCreditsList
import util.CommandHelper.showMyCredits
import util.CommandHelper.showOthersCredits
import util.Constants
import util.Constants.MESSAGE_BIG_MASOUD
import util.Constants.MESSAGE_GET_STICKER_SET
import util.Constants.MESSAGE_KING_MASOUD
import util.Constants.MESSAGE_LONG_LIVE_THE_KING
import util.Constants.MESSAGE_MASOUD
import util.Constants.MESSAGE_WOMEN
import util.Constants.MESSAGE_WOMEN_BRAIN
import util.Constants.MESSAGE_WOMEN_COFFEE_1
import util.Constants.MESSAGE_WOMEN_COFFEE_2
import util.MessageHelper.sendCreditingBotProhibitionMessage
import util.MessageHelper.sendCreditingSocialCreditBotProhibitionMessage
import util.MessageHelper.sendCreditingYourselfProhibitionMessage
import util.MessageHelper.sendLongLiveTheKingSticker
import util.MessageHelper.sendNotGroupMessage
import util.MessageHelper.sendStickerSet
import util.MessageHelper.sendUpdateUserSocialCreditResultMessage
import util.MessageHelper.sendWomenGif
import util.MessageHelper.sendZeroChangeTrollMessage
import util.PropertiesHelper
import util.Stickers.getSocialCreditChange
import java.util.*

fun main(args: Array<String>) {
    val propertiesHelper = PropertiesHelper(propertiesFilePath = args[0])
    val ratingRepository: RatingRepository = RatingRepositoryImpl(dbPath = propertiesHelper.getDbPath())

    val telegramBot = bot {
        token = propertiesHelper.getBotToken()

        dispatch {
            command(Constants.COMMAND_SEND_TEXT_ART_XI_JINPING) {
                sendXiJinpingTextArt(message)
            }

            command(Constants.COMMAND_SEND_TEXT_ART_WINNIE_THE_POOH) {
                sendWinnieThePoohTextArt(message)
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
                when (message.chat.type) {
                    ChatTypes.SUPERGROUP.value, ChatTypes.GROUP.value -> Unit
                    ChatTypes.PRIVATE.value -> {
                        println("Gif File ID = ${message.animation?.fileId}")
                    }
                }

                val shouldSendStickerSet = message.text?.lowercase(Locale.US) == MESSAGE_GET_STICKER_SET
                if (shouldSendStickerSet) {
                    when (message.chat.type) {
                        ChatTypes.SUPERGROUP.value, ChatTypes.GROUP.value -> Unit
                        ChatTypes.PRIVATE.value -> sendStickerSet(message)
                    }
                    return@message
                }

                val shouldSendLongLiveTheKingSticker = when {
                    message.text?.lowercase(Locale.US)?.contains(MESSAGE_LONG_LIVE_THE_KING) == true -> true
                    message.text?.lowercase(Locale.US)?.contains(MESSAGE_KING_MASOUD) == true -> true
                    message.text?.lowercase(Locale.US)?.contains(MESSAGE_BIG_MASOUD) == true -> true
                    message.text?.lowercase(Locale.US)?.contains(MESSAGE_MASOUD) == true -> true
                    message.caption?.lowercase(Locale.US)?.contains(MESSAGE_LONG_LIVE_THE_KING) == true -> true
                    message.caption?.lowercase(Locale.US)?.contains(MESSAGE_KING_MASOUD) == true -> true
                    message.caption?.lowercase(Locale.US)?.contains(MESSAGE_BIG_MASOUD) == true -> true
                    message.caption?.lowercase(Locale.US)?.contains(MESSAGE_MASOUD) == true -> true
                    else -> false
                }
                if (shouldSendLongLiveTheKingSticker) {
                    when (message.chat.type) {
                        ChatTypes.SUPERGROUP.value, ChatTypes.GROUP.value -> sendLongLiveTheKingSticker(message)
                        ChatTypes.PRIVATE.value -> Unit
                    }
                    return@message
                }

                val shouldSendWomenGif = when {
                    message.text?.lowercase(Locale.US)?.contains(MESSAGE_WOMEN_COFFEE_1) == true -> true
                    message.text?.lowercase(Locale.US)?.contains(MESSAGE_WOMEN_COFFEE_2) == true -> true
                    message.text?.lowercase(Locale.US)?.contains(MESSAGE_WOMEN) == true -> true
                    message.text?.lowercase(Locale.US)?.contains(MESSAGE_WOMEN_BRAIN) == true -> true
                    message.caption?.lowercase(Locale.US)?.contains(MESSAGE_WOMEN_COFFEE_1) == true -> true
                    message.caption?.lowercase(Locale.US)?.contains(MESSAGE_WOMEN_COFFEE_2) == true -> true
                    message.caption?.lowercase(Locale.US)?.contains(MESSAGE_WOMEN) == true -> true
                    message.caption?.lowercase(Locale.US)?.contains(MESSAGE_WOMEN_BRAIN) == true -> true
                    else -> false
                }
                if (shouldSendWomenGif) {
                    when (message.chat.type) {
                        ChatTypes.SUPERGROUP.value, ChatTypes.GROUP.value -> sendWomenGif(message)
                        ChatTypes.PRIVATE.value -> Unit
                    }
                    return@message
                }

                if (message.sticker == null || message.replyToMessage?.from == null) {
                    return@message
                }

                val socialCreditsChange = message.getSocialCreditChange() ?: return@message

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

                if (socialCreditsChange == 0L) {
                    when (message.chat.type) {
                        ChatTypes.SUPERGROUP.value, ChatTypes.GROUP.value -> sendZeroChangeTrollMessage(message)
                        ChatTypes.PRIVATE.value -> sendNotGroupMessage(message)
                    }
                    return@message
                }

                when (message.chat.type) {
                    ChatTypes.SUPERGROUP.value, ChatTypes.GROUP.value -> {
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