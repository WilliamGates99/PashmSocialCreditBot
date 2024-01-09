package presentation

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.User
import data.RatingRepository
import data.RatingRepositoryImpl
import util.CommandHelper.sendNotGroupMessage
import util.CommandHelper.sendStickerSet
import util.CommandHelper.showMyCredits
import util.CommandHelper.showOthersCredits
import util.MessageHelper.sendCreditingBotProhibitionMessage
import util.MessageHelper.sendCreditingSocialCreditBotProhibitionMessage
import util.MessageHelper.sendCreditingYourselfProhibitionMessage
import util.MessageHelper.sendNotGroupMessage
import util.*
import kotlin.math.absoluteValue

fun main(args: Array<String>) {
    val propertiesHelper = PropertiesHelper(propertiesFilePath = args[0])
    val ratingsRepository: RatingRepository = RatingRepositoryImpl(dbPath = propertiesHelper.getDbPath())

    val bot = bot {
        token = propertiesHelper.getBotToken()

        dispatch {
            command(Constants.COMMAND_GET_STICKER_SET) {
                sendStickerSet(message)
            }

            command(Constants.COMMAND_SHOW_MY_CREDITS) {
                when (message.chat.type) {
                    ChatTypes.SUPERGROUP.value -> showMyCredits(message, ratingsRepository)
                    ChatTypes.GROUP.value -> showMyCredits(message, ratingsRepository)
                    ChatTypes.PRIVATE.value -> sendNotGroupMessage(message)
                }
            }

            command(Constants.COMMAND_SHOW_OTHERS_CREDITS) {
                when (message.chat.type) {
                    ChatTypes.SUPERGROUP.value -> showOthersCredits(message, ratingsRepository)
                    ChatTypes.GROUP.value -> showOthersCredits(message, ratingsRepository)
                    ChatTypes.PRIVATE.value -> sendNotGroupMessage(message)
                }
            }

            // TODO: UPDATE RANK COMMAND
            command(Constants.COMMAND_SHOW_CITIZENS_RANK) {
                val ratings = ratingsRepository
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
                        ChatTypes.SUPERGROUP.value -> sendCreditingYourselfProhibitionMessage(message)
                        ChatTypes.GROUP.value -> sendCreditingYourselfProhibitionMessage(message)
                        ChatTypes.PRIVATE.value -> sendNotGroupMessage(message)
                    }
                    return@message
                }

                val botUser = bot.getMe().get()
                val isUserReplyingSocialCreditBot = message.replyToMessage?.from?.username == botUser.username
                if (isUserReplyingSocialCreditBot) {
                    when (message.chat.type) {
                        ChatTypes.SUPERGROUP.value -> sendCreditingSocialCreditBotProhibitionMessage(message)
                        ChatTypes.GROUP.value -> sendCreditingSocialCreditBotProhibitionMessage(message)
                        ChatTypes.PRIVATE.value -> sendNotGroupMessage(message)
                    }
                    return@message
                }

                val isReplyingToBot = message.replyToMessage?.from?.isBot == true
                if (isReplyingToBot) {
                    when (message.chat.type) {
                        ChatTypes.SUPERGROUP.value -> sendCreditingBotProhibitionMessage(message)
                        ChatTypes.GROUP.value -> sendCreditingBotProhibitionMessage(message)
                        ChatTypes.PRIVATE.value -> sendNotGroupMessage(message)
                    }
                    return@message
                }

                // TODO: FROM HERE
                val socialCreditChange = message.getSocialCreditChange() ?: return@message

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

                    // TODO: UPDATE
                    when (chatMember?.status) {
                        Constants.CHAT_MEMBER_STATUS_CREATOR, Constants.CHAT_MEMBER_STATUS_ADMIN -> {
                            val previousCredit = ratingsRepository.getUserRating(groupId, userId)?.rating ?: 0L
                            val updateUserRatingResult = ratingsRepository.updateUserRating(
                                messageSenderId = messageSender.id,
                                groupId = groupId,
                                groupTitle = groupTitle ?: "-",
                                userId = userId,
                                username = username ?: "-",
                                firstName = firstName,
                                ratingChange = socialCreditChange
                            )

                            val socialCreditChangeText = if (socialCreditChange > 0) {
                                "Плюс ${socialCreditChange.absoluteValue} социальный рейтинг для ${targetUser.printableName}. Партия гордится тобой \uD83D\uDC4D"
                            } else {
                                "Минус ${socialCreditChange.absoluteValue} социальный рейтинг для ${targetUser.printableName}. Ты разочаровываешь партию \uD83D\uDE1E"
                            }

                            updateUserRatingResult.onSuccess { userRatingInfo ->
                                val sendToUyghurCampText = sendToUyghurCampIfNeeded(
                                    previousCredit = previousCredit,
                                    currentCredit = userRatingInfo.rating,
                                    user = targetUser
                                )

//                    val messageBuilder = StringBuilder().apply {
//                        append(socialCreditChangeText)
//                        append("\n")
//                        append("Текущий социальный рейтинг: ")
//                        append(userRatingInfo.rating)
//
//                        sendToUyghurCampText?.let {
//                            append("\n\n")
//                            append(it)
//                        }
//                    }

                                val messageBuilder = StringBuilder().apply {
                                    append("\uD83C\uDF45previous user credit = $previousCredit\n")
                                    append("\uD83C\uDF45social credit change = ${if (socialCreditChange > 0) "+$socialCreditChange" else "$socialCreditChange"}\n\n")

                                    append("\uD83C\uDF45rating info:\n")
                                    userRatingInfo.apply {
                                        append("groupId = $groupId\n")
                                        append("groupTitle = $groupTitle\n")
                                        append("userId = $userId\n")
                                        append("username = $username\n")
                                        append("firstName = $firstName\n")
                                        append("rating = $rating\n")
                                        append("createdAt = $createdAt\n")
                                        append("modifiedAt = $modifiedAt")
                                    }
                                }

                                bot.sendMessage(
                                    chatId = ChatId.fromId(message.chat.id),
                                    text = messageBuilder.toString(),
                                    disableNotification = true
                                )
                            }

                            updateUserRatingResult.onFailure {
                                val messageBuilder = StringBuilder().apply {
                                    append("cooldown is not over yet")
                                }

                                bot.sendMessage(
                                    chatId = ChatId.fromId(message.chat.id),
                                    text = messageBuilder.toString(),
                                    disableNotification = true,
                                    replyToMessageId = message.messageId
                                )
                            }
                        }
                        Constants.CHAT_MEMBER_STATUS_MEMBER -> {
                            val messageBuilder = StringBuilder().apply {
                                append("کاکا سیاه نادان به مزرعه پنبه برگرد")
                            }

                            bot.sendMessage(
                                chatId = ChatId.fromId(message.chat.id),
                                text = messageBuilder.toString(),
                                disableNotification = true,
                                replyToMessageId = message.messageId
                            )
                        }
                        else -> {
                            val messageBuilder = StringBuilder().apply {
                                append("کاکا سیاه نادان به مزرعه پنبه برگرد")
                            }

                            bot.sendMessage(
                                chatId = ChatId.fromId(message.chat.id),
                                text = messageBuilder.toString(),
                                disableNotification = true,
                                replyToMessageId = message.messageId
                            )
                        }
                    }
                }
            }
        }
    }
    bot.startPolling()
}

private fun sendToUyghurCampIfNeeded(previousCredit: Long, currentCredit: Long, user: User): String? {
    return when {
        previousCredit >= 0L && currentCredit < 0L -> {
            val job = Jobs.uyghurCampJobs.random()
            "\uD83C\uDF34 Партия отправлять товарищ ${user.printableName} в санаторий для уйгур $job. Партия заботься о простой товарищ! \uD83D\uDC6E️"
        }

        previousCredit < 0L && currentCredit >= 0L -> {
            "\uD83C\uDFE1 Партия возвращать товарищ ${user.printableName} из санаторий для уйгур. Впредь будь аккуратный! \uD83D\uDC6E️"
        }

        else -> null
    }
}

private fun Message.getSocialCreditChange(): Long? {
    println("sticker fileUniqueId: ${sticker?.fileUniqueId}")
    println("sticker fileId: ${sticker?.fileId}")

    return when {
        Stickers.plusSocialCreditStickers.contains(sticker?.fileUniqueId) -> Constants.DEFAULT_PLUS_CREDIT
        Stickers.minusSocialCreditStickers.contains(sticker?.fileUniqueId) -> Constants.DEFAULT_MINUS_CREDIT
        Stickers.plusRicePlateStickers.contains(sticker?.fileUniqueId) -> Constants.DEFAULT_PLUS_RICE_PLATE_CREDIT
        else -> null
    }
}

private val User.printableName: String
    get() = username ?: ""