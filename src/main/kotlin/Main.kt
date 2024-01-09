import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.User
import data.RatingRepository
import data.RatingRepositoryImpl
import util.Constants
import util.Jobs
import util.PropertiesHelper
import util.Stickers
import kotlin.math.absoluteValue

fun main(args: Array<String>) {
    val propertiesHelper = PropertiesHelper(propertiesFilePath = args[0])
    val ratingsRepository: RatingRepository = RatingRepositoryImpl(dbPath = propertiesHelper.getDbPath())

    val bot = bot {
        token = propertiesHelper.getBotToken()

        dispatch {
            command(Constants.COMMAND_MY_RATING) {
                message.from?.let { user ->
                    val info = ratingsRepository.getUserRating(groupId = message.chat.id, userId = user.id)
                    val userSocialCredit = info?.rating ?: 0L

                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "Товарищ ${user.printableName}, партия сообщить, что твой социальный рейтинг составлять $userSocialCredit",
                        disableNotification = true
                    )
                }
            }

            command(Constants.COMMAND_RATING) {
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
                    disableNotification = true
                )
            }

            message {
                if (message.sticker == null || message.replyToMessage?.from == null) {
                    return@message
                }

                val isUserReplyingThemself = message.from?.id == message.replyToMessage?.from?.id
                if (isUserReplyingThemself) {
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "\uD83D\uDEAB Партия запрещать изменять свой рейтинг. Великий лидер Xi есть следить за тобой!",
                        disableNotification = true
                    )

                    return@message
                }

                val botUser = bot.getMe().get()
                val isUserReplyingSocialCreditBot = message.replyToMessage?.from?.username == botUser.username
                if (isUserReplyingSocialCreditBot) {
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "\uD83D\uDEAB Простой товарищ не может изменять рейтинг великий партия! Великий лидер Xi есть следить за тобой!",
                        disableNotification = true
                    )

                    return@message
                }

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
                                val messageBuilder =   StringBuilder().apply {
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
    print("ID: ${sticker?.fileUniqueId}")

    return when {
        Stickers.plusSocialCreditStickers.contains(sticker?.fileUniqueId) -> Constants.DEFAULT_PLUS_CREDIT
        Stickers.minusSocialCreditStickers.contains(sticker?.fileUniqueId) -> Constants.DEFAULT_MINUS_CREDIT
        Stickers.plusRicePlateStickers.contains(sticker?.fileUniqueId) -> Constants.DEFAULT_PLUS_RICE_PLATE_CREDIT
        else -> null
    }
}

private val User.printableName: String
    get() = username ?: ""