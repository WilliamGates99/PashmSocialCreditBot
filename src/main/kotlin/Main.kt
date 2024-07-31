import data.repositories.RatingRepositoryImpl
import dev.inmo.micro_utils.coroutines.subscribe
import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.api.telegramBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.animation
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.reply_to_message
import dev.inmo.tgbotapi.extensions.utils.shortcuts.animationMessages
import dev.inmo.tgbotapi.extensions.utils.shortcuts.stickerMessages
import dev.inmo.tgbotapi.extensions.utils.shortcuts.textMessages
import dev.inmo.tgbotapi.types.chat.*
import dev.inmo.tgbotapi.utils.RiskFeature
import domain.repositories.RatingRepository
import io.ktor.client.engine.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
import util.MessageHelper.sendStickerSet
import util.MessageHelper.sendUpdateUserSocialCreditResultMessage
import util.MessageHelper.sendWomenGif
import util.MessageHelper.sendZeroChangeTrollMessage
import util.PropertiesHelper
import util.Stickers.getSocialCreditChange
import java.net.Authenticator
import java.net.PasswordAuthentication
import java.util.*

@OptIn(RiskFeature::class)
fun main(args: Array<String>) {
    val propertiesHelper = PropertiesHelper(propertiesFilePath = args[0])
    val ratingRepository: RatingRepository = RatingRepositoryImpl(dbPath = propertiesHelper.getDbPath())

    val telegramBot = telegramBot(
        token = propertiesHelper.getBotToken()
    ) {
        Authenticator.setDefault(object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication? {
                val isRequestSentToProxy = requestingHost.lowercase(
                    Locale.US
                ) == propertiesHelper.getProxyHost().lowercase(Locale.US)

                return if (isRequestSentToProxy) PasswordAuthentication(
                    /* userName = */ propertiesHelper.getProxyUsername(),
                    /* password = */ propertiesHelper.getProxyPassword().toCharArray()
                ) else null
            }
        })

        engine {
            proxy = ProxyBuilder.socks(
                host = propertiesHelper.getProxyHost(),
                port = propertiesHelper.getProxyPort().toInt()
            )
        }
    }

    runBlocking {
        launch(context = Dispatchers.IO) {
            telegramBot.buildBehaviourWithLongPolling {
                onCommand(command = Constants.COMMAND_SEND_TEXT_ART_XI_JINPING) { message ->
                    sendXiJinpingTextArt(message)
                }

                onCommand(command = Constants.COMMAND_SEND_TEXT_ART_WINNIE_THE_POOH) { message ->
                    sendWinnieThePoohTextArt(message)
                }

                onCommand(command = Constants.COMMAND_SHOW_MY_CREDITS) { message ->
                    when (message.chat) {
                        is GroupChatImpl -> showMyCredits(message, ratingRepository)
                        is SupergroupChatImpl -> showMyCredits(message, ratingRepository)
                        is ForumChatImpl -> showMyCredits(message, ratingRepository)
                        is PrivateChatImpl -> sendNotGroupMessage(message)
                        else -> Unit
                    }
                }

                onCommand(command = Constants.COMMAND_SHOW_OTHERS_CREDITS) { message ->
                    when (message.chat) {
                        is GroupChatImpl -> showOthersCredits(message, ratingRepository)
                        is SupergroupChatImpl -> showOthersCredits(message, ratingRepository)
                        is ForumChatImpl -> showOthersCredits(message, ratingRepository)
                        is PrivateChatImpl -> sendNotGroupMessage(message)
                        else -> Unit
                    }
                }

                onCommand(command = Constants.COMMAND_SHOW_COMRADES_RANK) { message ->
                    when (message.chat) {
                        is GroupChatImpl -> showGroupSocialCreditsList(message, ratingRepository)
                        is SupergroupChatImpl -> showGroupSocialCreditsList(message, ratingRepository)
                        is ForumChatImpl -> showGroupSocialCreditsList(message, ratingRepository)
                        is PrivateChatImpl -> sendNotGroupMessage(message)
                        else -> Unit
                    }
                }

                animationMessages().subscribe(scope = this@launch) { gifMessage ->
                    when (gifMessage.chat) {
                        is PrivateChatImpl -> println("Gif Animation = ${gifMessage.animation}")
                        else -> Unit
                    }
                }

                // TODO: BEGIN OF TEMP
//                imageMessages().subscribe(scope = this@launch) { imageMessage ->
//                    imageMessage.content.text
//                    when (imageMessage.chat) {
//                        is PrivateChatImpl -> {
//                            println("image message = $imageMessage")
//                            val caption = imageMessage.content.text
//                        }
//                        else -> Unit
//                    }
//                }
                // TODO: END OF TEMP

                textMessages().subscribe(scope = this@launch) { message ->
                    val shouldSendStickerSet = message.content.text.lowercase(Locale.US) == MESSAGE_GET_STICKER_SET
                    if (shouldSendStickerSet) {
                        when (message.chat) {
                            is PrivateChatImpl -> sendStickerSet(message)
                            else -> Unit
                        }
                        return@subscribe
                    }

                    val shouldSendLongLiveTheKingSticker = when {
                        message.content.text.lowercase(Locale.US).contains(MESSAGE_LONG_LIVE_THE_KING) -> true
                        message.content.text.lowercase(Locale.US).contains(MESSAGE_KING_MASOUD) -> true
                        message.content.text.lowercase(Locale.US).contains(MESSAGE_BIG_MASOUD) -> true
                        message.content.text.lowercase(Locale.US).contains(MESSAGE_MASOUD) -> true
                        // message.content.caption?.lowercase(Locale.US)?.contains(MESSAGE_LONG_LIVE_THE_KING) == true -> true TODO: ADD FOR IMAGE, VOICE, VIDEO, ETC
                        // message.content.caption?.lowercase(Locale.US)?.contains(MESSAGE_KING_MASOUD) == true -> true TODO: ADD FOR IMAGE, VOICE, VIDEO, ETC
                        // message.content.caption?.lowercase(Locale.US)?.contains(MESSAGE_BIG_MASOUD) == true -> true TODO: ADD FOR IMAGE, VOICE, VIDEO, ETC
                        // message.content.caption?.lowercase(Locale.US)?.contains(MESSAGE_MASOUD) == true -> true TODO: ADD FOR IMAGE, VOICE, VIDEO, ETC
                        else -> false
                    }
                    if (shouldSendLongLiveTheKingSticker) {
                        when (message.chat) {
                            is GroupChatImpl -> sendLongLiveTheKingSticker(message)
                            is SupergroupChatImpl -> sendLongLiveTheKingSticker(message)
                            is ForumChatImpl -> sendLongLiveTheKingSticker(message)
                            else -> Unit
                        }
                        return@subscribe
                    }

                    val shouldSendWomenGif = when {
                        message.content.text.lowercase(Locale.US).contains(MESSAGE_WOMEN_COFFEE_1) -> true
                        message.content.text.lowercase(Locale.US).contains(MESSAGE_WOMEN_COFFEE_2) -> true
                        message.content.text.lowercase(Locale.US).contains(MESSAGE_WOMEN) -> true
                        message.content.text.lowercase(Locale.US).contains(MESSAGE_WOMEN_BRAIN) -> true
//                        message.caption?.lowercase(Locale.US)?.contains(MESSAGE_WOMEN_COFFEE_1) == true -> true
//                        message.caption?.lowercase(Locale.US)?.contains(MESSAGE_WOMEN_COFFEE_2) == true -> true
//                        message.caption?.lowercase(Locale.US)?.contains(MESSAGE_WOMEN) == true -> true
//                        message.caption?.lowercase(Locale.US)?.contains(MESSAGE_WOMEN_BRAIN) == true -> true
                        else -> false
                    }
                    if (shouldSendWomenGif) {
                        when (message.chat) {
                            is GroupChatImpl -> sendWomenGif(message)
                            is SupergroupChatImpl -> sendWomenGif(message)
                            is ForumChatImpl -> sendWomenGif(message)
                            else -> Unit
                        }
                        return@subscribe
                    }
                }

                stickerMessages().subscribe(scope = this@launch) { stickerMessage ->
                    when (stickerMessage.chat) {
                        is PrivateChatImpl -> println("Sticker Content = ${stickerMessage.content}")
                        else -> Unit
                    }
                }

                stickerMessages().subscribe(scope = this@launch) { stickerMessage ->
                    val isStickerReplyingToMessage = stickerMessage.reply_to_message?.from != null
                    if (!isStickerReplyingToMessage) {
                        return@subscribe
                    }

                    val socialCreditsChange = stickerMessage.getSocialCreditChange() ?: return@subscribe

                    val isUserReplyingThemself = stickerMessage.from?.id == stickerMessage.reply_to_message?.from?.id
                    if (isUserReplyingThemself) {
                        when (stickerMessage.chat) {
                            is GroupChatImpl -> sendCreditingYourselfProhibitionMessage(stickerMessage)
                            is SupergroupChatImpl -> sendCreditingYourselfProhibitionMessage(stickerMessage)
                            is ForumChatImpl -> sendCreditingYourselfProhibitionMessage(stickerMessage)
                            is PrivateChatImpl -> sendNotGroupMessage(stickerMessage)
                            else -> Unit
                        }
                        return@subscribe
                    }

                    val botUser = telegramBot.getMe()
                    val isUserReplyingToSocialCreditBot = stickerMessage.reply_to_message
                        ?.from?.username == botUser.username
                    if (isUserReplyingToSocialCreditBot) {
                        when (stickerMessage.chat) {
                            is GroupChatImpl -> sendCreditingSocialCreditBotProhibitionMessage(stickerMessage)
                            is SupergroupChatImpl -> sendCreditingSocialCreditBotProhibitionMessage(stickerMessage)
                            is ForumChatImpl -> sendCreditingSocialCreditBotProhibitionMessage(stickerMessage)
                            is PrivateChatImpl -> sendNotGroupMessage(stickerMessage)
                            else -> Unit
                        }
                        return@subscribe
                    }

                    val isReplyingToBot = when (stickerMessage.reply_to_message?.from) {
                        is ExtendedBot -> true
                        is CommonBot -> true
                        else -> false
                    }
                    if (isReplyingToBot) {
                        when (stickerMessage.chat) {
                            is GroupChatImpl -> sendCreditingBotProhibitionMessage(stickerMessage)
                            is SupergroupChatImpl -> sendCreditingBotProhibitionMessage(stickerMessage)
                            is ForumChatImpl -> sendCreditingBotProhibitionMessage(stickerMessage)
                            is PrivateChatImpl -> sendNotGroupMessage(stickerMessage)
                            else -> Unit
                        }
                        return@subscribe
                    }

                    if (socialCreditsChange == 0L) {
                        when (stickerMessage.chat) {
                            is GroupChatImpl -> sendZeroChangeTrollMessage(stickerMessage)
                            is SupergroupChatImpl -> sendZeroChangeTrollMessage(stickerMessage)
                            is ForumChatImpl -> sendZeroChangeTrollMessage(stickerMessage)
                            is PrivateChatImpl -> sendNotGroupMessage(stickerMessage)
                            else -> Unit
                        }
                        return@subscribe
                    } else {
                        when (stickerMessage.chat) {
                            is GroupChatImpl -> sendUpdateUserSocialCreditResultMessage(
                                message = stickerMessage,
                                socialCreditsChange = socialCreditsChange,
                                ratingRepository = ratingRepository
                            )
                            is SupergroupChatImpl -> sendUpdateUserSocialCreditResultMessage(
                                message = stickerMessage,
                                socialCreditsChange = socialCreditsChange,
                                ratingRepository = ratingRepository
                            )
                            is ForumChatImpl -> sendUpdateUserSocialCreditResultMessage(
                                message = stickerMessage,
                                socialCreditsChange = socialCreditsChange,
                                ratingRepository = ratingRepository
                            )
                            is PrivateChatImpl -> sendNotGroupMessage(stickerMessage)
                            else -> Unit
                        }
                        return@subscribe
                    }
                }
            }
        }
    }
}