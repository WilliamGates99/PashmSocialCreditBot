package utils

import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.reply_to_message
import dev.inmo.tgbotapi.types.ReplyParameters
import dev.inmo.tgbotapi.types.chat.CommonBot
import dev.inmo.tgbotapi.types.chat.CommonUser
import dev.inmo.tgbotapi.types.chat.ExtendedBot
import dev.inmo.tgbotapi.types.message.HTMLParseMode
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.types.message.abstracts.Message
import dev.inmo.tgbotapi.utils.RiskFeature
import domain.repositories.RatingRepository
import utils.Constants.COMMAND_SHOW_MY_CREDITS
import utils.Constants.COMMAND_SHOW_OTHERS_CREDITS
import kotlin.random.Random

@OptIn(RiskFeature::class)
object CommandHelper {

    suspend fun BehaviourContext.sendXiJinpingTextArt(message: Message) {
        when (Random.nextBoolean()) {
            true -> {
                sendMessage(
                    text = """
                        ⣿⣿⠟⠋⠄⠄⠄⠄⠄⠄⠄⢁⠈⢻⢿⣿⣿⣿⣿⣿
                        ⣿⣿⠃⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠈⡀⠭⢿⣿⣿
                        ⣿⡟⠄⢀⣾⣿⣿⣿⣷⣶⣿⣷⣶⣶⡆⠄⠄⠄⣿⣿
                        ⣿⡇⢀⣼⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠄⠄⢸⣿⣿
                        ⣿⣇⣼⣿⣿⠿⠶⠙⣿⡟⠡⣴⣿⣽⣿⣧⠄⢸⣿⣿
                        ⣿⣿⣾⣿⣿⣟⣭⣾⣿⣷⣶⣶⣴⣶⣿⣿⢄⣿⣿⣿
                        ⣿⣿⣿⣿⣿⡟⣩⣿⣿⣿⡏⢻⣿⣿⣿⣿⣿⣿⣿⣿
                        ⣿⣿⣿⣹⡋⠘⠷⣦⣀⣠⡶⠁⠈⠋⠄⣿⣿⣿⣿⣿
                        ⣿⣿⣿⣍⠃⣴⣶⡔⠒⠄⣠⢀⠄⠄⠄⡨⣿⣿⣿⣿
                        ⣿⣿⣿⣿⣦⡘⠿⣷⣿⠿⠟⠃⠄⠄⣠⡇⠈⠻⣿⣿
                        ⣿⡿⠟⠋⢁⣷⣠⠄⠄⠄⠄⣀⣠⣾⡟⠄⠄⠄⠄⠉
                        ⠁⠄⠄⠄⢸⣿⣿⡯⢓⣴⣾⣿⣿⡟⠄⠄⠄⠄⠄⠄
                        ⠄⠄⠄⠄⣿⡟⣷⠄⠹⣿⣿⣿⡿⠁⠄⠄⠄⠄⠄⠄
                        ⠄⠄⠄⣸⣿⡷⡇⠄⣴⣾⣿⣿⠃⠄⠄⠄⠄⠄⠄⠄
                        ⠄⠄⠄⣿⣿⠃⣦⣄⣿⣿⣿⠇⠄⠄⠄⠄⠄⠄⠄⠄
                        ⠄⠄⢸⣿⠗⢈⡶⣷⣿⣿⡏⠄⠄⠄⠄⠄⠄⠄⠄⠄
                    """.trimIndent(),
                    chat = message.chat,
                    disableNotification = true
                )
            }
            false -> {
                sendMessage(
                    text = """
                        ⠀⠀⣠⣴⣻⣻⣻⣻⣻⣻⣻⡾⣷⡄⡀⠀⠀⠀⠀⠀
                        ⠀⠀⣼⣻⣻⣻⣻⣻⣻⣻⣻⣻⣻⣻⣷⢿⣒⡀⠀⠀
                        ⠀⢠⣻⡿⠁⠀⠀⠀⠈⠉⠀⠈⠉⠉⢹⣻⣻⣻⠀⠀
                        ⠀⢸⡿⠃⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣻⣻⡇⠀⠀
                        ⠀⠸⠃⠀⠀⣀⣉⣦⠀⢠⣞⠋⠀⠂⠀⠘⣻⡇⠀⠀
                        ⠀⠀⠁⠀⠀⠠⠒⠁⠀⠈⠉⠉⠋⠉⠀⠀⡻⠀⠀⠀
                        ⠀⠀⠀⠀⠀⢠⠖⠀⠀⠀⢰⡄⠀⠀⠀⠀⠀⠀⠀⠀
                        ⠀⠀⠀⠆⢴⣧⣈⠙⠿⠟⢉⣾⣷⣴⣻⠀⠀⠀⠀⠀
                        ⠀⠀⠀⠲⣼⠋⠉⢫⣭⣻⠟⡿⣻⣻⣻⢗⠀⠀⠀⠀
                        ⠀⠀⠀⠀⠙⢧⣀⠈⠀⣀⣠⣼⣻⣻⠟⢸⣷⣄⠀⠀
                        ⠀⢀⣠⣴⡾⠈⠟⣻⣻⣻⣻⠿⠟⠁⢠⣻⣻⣻⣻⣶
                        ⣾⣻⣻⣻⡇⠀⠀⢐⡬⠋⠁⠀⠀⢠⣻⣻⣻⣻⣻⣻
                        ⣻⣻⣻⣻⠀⢠⠈⣻⣆⠀⠀⠀⢀⣾⣻⣻⣻⣻⣻⣻
                        ⣻⣻⣻⠇⠀⢈⢸⣻⠋⠁⠀⠀⣼⣻⣻⣻⣻⣻⣻⣻
                        ⣻⣻⣻⠀⠀⣼⠙⠻⠀⠀⠀⣸⣻⣻⣻⣻⣻⣻⣻⣻
                        ⣻⣻⡇⠀⣨⡷⢉⠈⠀⠀⢰⣻⣻⣻⣻⣻⣻⣻⣻⣻
                    """.trimIndent(),
                    chat = message.chat,
                    disableNotification = true
                )
            }
        }
    }

    suspend fun BehaviourContext.sendWinnieThePoohTextArt(message: Message) {
        when (Random.nextBoolean()) {
            true -> {
                sendMessage(
                    text = """
                        ⣿⢟⣛⣛⣛⠿⣿⣿⡿⠿⠿⠿⠿⢿⣿⡟⣵⣶⣮⠻⣿
                        ⡏⣿⣿⣿⣿⢗⣨⣵⣶⣿⣿⣿⣿⣶⣮⠁⣿⣿⣿⡇⣿
                        ⣿⣬⣛⢛⣵⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⢿⣿⣿⢇⣿
                        ⣿⡿⣣⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⢿⣿
                        ⣿⡸⢫⣽⣿⣯⣻⣿⣿⣿⣿⣿⣶⣹⣿⣿⣿⣿⣿⣾⣿
                        ⣿⡷⣹⣿⣿⣿⣷⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
                        ⢏⣾⣿⣿⢈⣩⡥⣿⣯⣭⣛⣿⣸⣿⣿⣿⣿⣿⣯⢸⣿
                        ⢸⣿⣿⣿⣿⡁⠀⠀⢹⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
                        ⣇⢻⣿⣛⢿⣷⣶⣶⣿⣿⣿⣿⣝⣛⣿⣿⣿⣿⡿⠨⡻
                        ⢟⡤⡻⣿⣮⡻⣿⣿⣿⣿⡿⣫⣾⣿⣿⣿⡿⢟⣣⢞⢡
                        ⣜⢷⣕⠜⣿⣿⣷⣶⣶⣶⣿⡿⠟⣛⣭⣴⣾⠿⣛⣭⣾
                        ⣿⣷⣶⣍⢒⣯⣝⡻⢟⣫⣵⣾⣿⡿⢟⣭⣶⣿⣿⣿⣿
                        ⣿⣿⣿⣿⣷⣝⡻⠷⠿⠿⣟⣋⣥⣾⣿⣿⣿⣿⣿⣿⣿
                    """.trimIndent(),
                    chat = message.chat,
                    disableNotification = true
                )
            }
            false -> {
                sendMessage(
                    text = """
                        ⠀⡠⠤⠤⠤⣀⠀⠀⢀⣀⣀⣀⣀⡀⠀⢠⠊⠉⠑⣄⠀
                        ⢰⠀⠀⠀⠀⡨⠗⠊⠉⠀⠀⠀⠀⠉⠑⣾⠀⠀⠀⢸⠀
                        ⠀⠓⠤⡤⠊⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⡀⠀⠀⡸⠀
                        ⠀⢀⠜⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⡀⠀
                        ⠀⢇⡔⠂⠀⠐⠄⠀⠀⠀⠀⠀⠉⠆⠀⠀⠀⠀⠀⠁⠀
                        ⠀⢈⠆⠀⠀⠀⠈⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                        ⡰⠁⠀⠀⡷⠖⢚⠀⠐⠒⠤⠀⠇⠀⠀⠀⠀⠀⠐⡇⠀
                        ⡇⠀⠀⠀⠀⢾⣿⣿⡆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                        ⠸⡄⠀⠤⡀⠈⠉⠉⠀⠀⠀⠀⠢⠤⠀⠀⠀⠀⢀⣗⢄
                        ⡠⢛⢄⠀⠑⢄⠀⠀⠀⠀⢀⠔⠁⠀⠀⠀⢀⡠⠜⡡⡞
                        ⠣⡈⠪⣣⠀⠀⠈⠉⠉⠉⠀⢀⣠⠤⠒⠋⠁⣀⠤⠒⠁
                        ⠀⠈⠉⠲⡭⠐⠢⢄⡠⠔⠊⠁⠀⢀⡠⠒⠉⠀⠀⠀⠀
                        ⠀⠀⠀⠀⠈⠢⢄⣈⣀⣀⠠⠴⠚⠁⠀⠀⠀⠀⠀⠀⠀
                    """.trimIndent(),
                    chat = message.chat,
                    disableNotification = true
                )
            }
        }
    }

    suspend fun BehaviourContext.sendHappyMerchantTextArt(message: Message) {
        when (Random.nextBoolean()) {
            true -> {
                sendMessage(
                    text = """
                        ⣿⣿⣿⣿⣿⣿⣿⣿⠟⠉⠅⣀⣄⡉⠈⠘⢿⣿⣿⣿
                        ⣿⣿⣿⣿⣿⣿⣿⠃⠀⠰⣿⣿⣿⣿⣿⣷⣶⢙⣿⣿
                        ⣿⣿⣿⡿⠿⠟⠉⠀⠀⠀⠉⣽⠟⠛⠻⣿⣿⣷⠹⣿
                        ⣿⣿⢋⣴⣦⡡⡄⣤⣤⠀⣸⣿⠐⠁⠀⢠⣤⠀⠀⣾
                        ⣿⡏⣾⣿⣿⣇⠃⢻⣿⠀⠿⠿⣿⣾⣿⡿⠿⣷⣌⢻
                        ⡟⢠⡍⣿⣿⣿⡄⠄⠁⠀⠀⠀⠉⠉⠁⠰⠷⣬⣿⣾
                        ⡇⢿⡇⣿⣿⣿⣷⢻⠀⠀⠀⠀⠀⠀⠒⠖⠒⣀⣤⣥
                        ⠇⣾⣇⣿⣿⣿⣿⡌⠀⠀⠀⠀⠀⠀⠢⠄⠀⠟⢿⣿
                        ⣴⣿⣿⠸⣿⣿⣿⣿⡄⠀⠀⠀⠀⠀⠀⢠⡖⠁⡙⢻
                        ⣿⣿⣿⣇⢽⣿⣿⣿⢟⡈⣂⣂⣠⣤⠲⣿⣿⡶⠇⠀
                        ⣿⣿⣿⣿⣏⢿⣿⣿⣿⣸⣿⣿⣿⡿⢀⡃⠀⠠⡘⣰
                        ⣿⣿⣿⣿⣿⣧⣙⠿⠿⠿⢿⣛⣥⣾⣿⣿⣿⣿⣿⣿
                    """.trimIndent(),
                    chat = message.chat,
                    disableNotification = true
                )
            }
            false -> {
                sendMessage(
                    text = """
                        ⠀⠀⠀⠀⠀⠀⠀⠀⣠⣶⣺⠿⠻⢶⣷⣧⡀⠀⠀⠀
                        ⠀⠀⠀⠀⠀⠀⠀⣼⣿⣏⠀⠀⠀⠀⠀⠈⠉⡦⠀⠀
                        ⠀⠀⠀⢀⣀⣠⣶⣿⣿⣿⣶⠂⣠⣤⣄⠀⠀⠈⣆⠀
                        ⠀⠀⡴⠋⠙⢞⢻⠛⠛⣿⠇⠀⣯⣾⣿⡟⠛⣿⣿⠁
                        ⠀⢰⠁⠀⠀⠸⣼⡄⠀⣿⣀⣀⠀⠁⠀⢀⣀⠈⠳⡄
                        ⢠⡟⢲⠀⠀⠀⢻⣻⣾⣿⣿⣿⣶⣶⣾⣏⣈⠓⠀⠁
                        ⢸⡀⢸⠀⠀⠀⠈⡄⣿⣿⣿⣿⣿⣿⣭⣩⣭⠿⠛⠚
                        ⣸⠁⠸⠀⠀⠀⠀⢳⣿⣿⣿⣿⣿⣿⣝⣻⣿⣠⡀⠀
                        ⠋⠀⠀⣇⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⡟⢩⣾⢦⡄
                        ⠀⠀⠀⠸⡂⠀⠀⠀⡠⢷⠽⠽⠟⠛⣍⠀⠀⢉⣸⣿
                        ⠀⠀⠀⠀⠰⡀⠀⠀⠀⠇⠀⠀⠀⢀⡿⢼⣿⣟⢧⠏
                        ⠀⠀⠀⠀⠀⠘⠦⣀⣀⣀⡀⠤⠚⠁⠀⠀⠀⠀⠀⠀
                    """.trimIndent(),
                    chat = message.chat,
                    disableNotification = true
                )
            }
        }
    }

    suspend fun BehaviourContext.sendNotGroupMessage(message: Message) {
        sendMessage(
            text = "\uD83D\uDE44The Social Credit System only works in groups.",
            chat = message.chat,
            disableNotification = true
        )
    }

    suspend fun BehaviourContext.showMyCredits(
        message: Message,
        ratingRepository: RatingRepository
    ) {
        message.from?.let { user ->
            val userRatingInfo = ratingRepository.getUserSocialCredits(
                groupId = message.chat.id.chatId.long,
                userId = user.id.chatId.long
            )
            val userSocialCredit = userRatingInfo?.socialCredits ?: 0L

            sendMessage(
                text = "The Party informs that Comrade *${user.firstName}* has $userSocialCredit social credits.",
                chat = message.chat,
                disableNotification = true,
                parseMode = MarkdownParseMode
            )
        }
    }

    suspend fun BehaviourContext.showOthersCredits(
        message: Message,
        ratingRepository: RatingRepository
    ) {
        val repliedUser = message.reply_to_message?.from
        if (repliedUser == null) {
            sendMessage(
                text = "⚠\uFE0FReply to someone with /$COMMAND_SHOW_OTHERS_CREDITS to find out their social credits!",
                chat = message.chat,
                replyParameters = ReplyParameters(message = message),
                disableNotification = true
            )
            return
        }

        val isReplyingToBot = when (repliedUser) {
            is ExtendedBot -> true
            is CommonBot -> true
            is CommonUser -> false
        }
        if (isReplyingToBot) {
            return
        }

        val isUserReplyingThemself = message.from?.id == repliedUser.id
        if (isUserReplyingThemself) {
            sendMessage(
                text = "⚠\uFE0FUse /$COMMAND_SHOW_MY_CREDITS command to find out your social credits!",
                chat = message.chat,
                replyParameters = ReplyParameters(message = message),
                disableNotification = true
            )
            return
        }

        val userRatingInfo = ratingRepository.getUserSocialCredits(
            groupId = message.chat.id.chatId.long,
            userId = repliedUser.id.chatId.long
        )
        val userSocialCredit = userRatingInfo?.socialCredits ?: 0L

        sendMessage(
            text = "The Party informs that Comrade *${repliedUser.firstName}* has $userSocialCredit social credits.",
            chat = message.chat,
            disableNotification = true,
            parseMode = MarkdownParseMode
        )
    }

    suspend fun BehaviourContext.showGroupSocialCreditsList(
        message: Message,
        ratingRepository: RatingRepository
    ) {
        val stringBuilder = StringBuilder()
        val groupSocialCreditsList = ratingRepository.getGroupSocialCreditsList(
            groupId = message.chat.id.chatId.long
        )

        if (groupSocialCreditsList.isEmpty()) {
            stringBuilder.append("Every comrade has 0 social credits. Be careful, *great Leader Xi* is watching over you!")
        } else {
            stringBuilder.apply {
                append("<i>\uD83D\uDCE3Comrades, listen carefully!</i>")
                append("\n")
                append("<i>\uD83D\uDCDCIn the name of our <b>great leader Xi</b>, the party has published top citizens list based on their social credits:</i>")
                append("\n\n")
                append("<tg-spoiler>") // Spoiler Markdown Tag
            }

            groupSocialCreditsList.forEachIndexed { index, userSocialCreditsInfo ->
                stringBuilder.apply {
                    userSocialCreditsInfo.apply {
                        val socialClass = SocialClass.getComradeSocialClass(socialCredits)
                        append("${index + 1}. $firstName ― $socialClass: $socialCredits credits")
                    }

                    if (index == groupSocialCreditsList.lastIndex) {
                        append("</tg-spoiler>") // Spoiler Markdown Tag
                    } else {
                        append("\n")
                    }
                }
            }
        }

        sendMessage(
            text = stringBuilder.toString(),
            chat = message.chat,
            disableNotification = true,
            parseMode = HTMLParseMode
        )
    }
}