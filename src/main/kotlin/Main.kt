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

fun main() {
    val ratingsRepository: RatingRepository = RatingRepositoryImpl(dbPath = PropertiesHelper.getDbPath())

    val bot = bot {
        token = PropertiesHelper.getBotToken()

        dispatch {
            command(Constants.COMMAND_MY_RATING) {
                message.from?.let { user ->
                    val info = ratingsRepository.getUserRating(user.id)
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

                if (message.from?.id == message.replyToMessage?.from?.id) {
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "\uD83D\uDEAB Партия запрещать изменять свой рейтинг. Великий лидер Xi есть следить за тобой!",
                        disableNotification = true
                    )

                    return@message
                }

                if (message.replyToMessage?.from?.username == "CCPSocialCreditBot") { // Dirty hack, IDK how to get self ID
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "\uD83D\uDEAB Простой товарищ не может изменять рейтинг великий партия! Великий лидер Xi есть следить за тобой!",
                        disableNotification = true
                    )

                    return@message
                }

                val socialCreditChange = message.getSocialCreditChange() ?: return@message

                message.replyToMessage?.from?.let { user ->
                    val previousCredit = ratingsRepository.getUserRating(user.id)?.rating ?: 0L
                    val info = ratingsRepository.updateUserRating(user.id, user.username ?: "-", socialCreditChange)

                    val socialCreditChangeText = if (socialCreditChange > 0) {
                        "Плюс ${socialCreditChange.absoluteValue} социальный рейтинг для ${user.printableName}. Партия гордится тобой \uD83D\uDC4D"
                    } else {
                        "Минус ${socialCreditChange.absoluteValue} социальный рейтинг для ${user.printableName}. Ты разочаровываешь партию \uD83D\uDE1E"
                    }

                    val sendToUyghurCamp = sendToUyghurCampIfNeeded(
                        previousCredit = previousCredit,
                        currentCredit = info.rating,
                        user = user
                    )

                    val messageBuilder = StringBuilder().apply {
                        append(socialCreditChangeText)
                        append("\n")
                        append("Текущий социальный рейтинг: ")
                        append(info.rating)

                        sendToUyghurCamp?.let {
                            append("\n\n")
                            append(it)
                        }
                    }


                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = messageBuilder.toString(),
                        disableNotification = true
                    )
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