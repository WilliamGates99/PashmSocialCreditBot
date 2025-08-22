package message_observers

import dev.inmo.micro_utils.coroutines.subscribe
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.reply_to_message
import dev.inmo.tgbotapi.extensions.utils.shortcuts.stickerMessages
import dev.inmo.tgbotapi.types.chat.*
import dev.inmo.tgbotapi.utils.RiskFeature
import domain.repositories.RatingRepository
import domain.utils.getSocialCreditChange
import kotlinx.coroutines.CoroutineScope
import utils.MessageHelper.sendCreditingBotProhibitionMessage
import utils.MessageHelper.sendCreditingSocialCreditBotProhibitionMessage
import utils.MessageHelper.sendCreditingYourselfProhibitionMessage
import utils.MessageHelper.sendNotGroupMessage
import utils.MessageHelper.sendUpdateUserSocialCreditResultMessage
import utils.MessageHelper.sendZeroChangeTrollMessage

@OptIn(RiskFeature::class)
fun BehaviourContext.observeStickerMessages(
    telegramBot: TelegramBot,
    ratingRepository: RatingRepository,
    scope: CoroutineScope
) {
    stickerMessages().subscribe(
        scope = scope
    ) { sticker ->
        when (sticker.chat) {
            is PrivateChatImpl -> println("Sticker Content = ${sticker.content}")
            else -> Unit
        }

        val isStickerReplyingToMessage = sticker.reply_to_message?.from != null
        if (!isStickerReplyingToMessage) {
            return@subscribe
        }

        val socialCreditsChange = sticker.getSocialCreditChange() ?: return@subscribe

        val isUserReplyingThemself = sticker.from?.id == sticker.reply_to_message?.from?.id
        if (isUserReplyingThemself) {
            when (sticker.chat) {
                is GroupChatImpl -> sendCreditingYourselfProhibitionMessage(message = sticker)
                is SupergroupChatImpl -> sendCreditingYourselfProhibitionMessage(message = sticker)
                is ForumChatImpl -> sendCreditingYourselfProhibitionMessage(message = sticker)
                is PrivateChatImpl -> sendNotGroupMessage(message = sticker)
                else -> Unit
            }
            return@subscribe
        }

        val botUser = telegramBot.getMe()
        val isUserReplyingToSocialCreditBot = sticker.reply_to_message
            ?.from?.username == botUser.username
        if (isUserReplyingToSocialCreditBot) {
            when (sticker.chat) {
                is GroupChatImpl -> sendCreditingSocialCreditBotProhibitionMessage(message = sticker)
                is SupergroupChatImpl -> sendCreditingSocialCreditBotProhibitionMessage(message = sticker)
                is ForumChatImpl -> sendCreditingSocialCreditBotProhibitionMessage(message = sticker)
                is PrivateChatImpl -> sendNotGroupMessage(message = sticker)
                else -> Unit
            }
            return@subscribe
        }

        val isReplyingToBot = when (sticker.reply_to_message?.from) {
            is ExtendedBot -> true
            is CommonBot -> true
            else -> false
        }
        if (isReplyingToBot) {
            when (sticker.chat) {
                is GroupChatImpl -> sendCreditingBotProhibitionMessage(message = sticker)
                is SupergroupChatImpl -> sendCreditingBotProhibitionMessage(message = sticker)
                is ForumChatImpl -> sendCreditingBotProhibitionMessage(message = sticker)
                is PrivateChatImpl -> sendNotGroupMessage(message = sticker)
                else -> Unit
            }
            return@subscribe
        }

        if (socialCreditsChange == 0L) {
            when (sticker.chat) {
                is GroupChatImpl -> sendZeroChangeTrollMessage(message = sticker)
                is SupergroupChatImpl -> sendZeroChangeTrollMessage(message = sticker)
                is ForumChatImpl -> sendZeroChangeTrollMessage(message = sticker)
                is PrivateChatImpl -> sendNotGroupMessage(message = sticker)
                else -> Unit
            }
            return@subscribe
        }

        when (sticker.chat) {
            is GroupChatImpl -> sendUpdateUserSocialCreditResultMessage(
                message = sticker,
                socialCreditsChange = socialCreditsChange,
                ratingRepository = ratingRepository
            )
            is SupergroupChatImpl -> sendUpdateUserSocialCreditResultMessage(
                message = sticker,
                socialCreditsChange = socialCreditsChange,
                ratingRepository = ratingRepository
            )
            is ForumChatImpl -> sendUpdateUserSocialCreditResultMessage(
                message = sticker,
                socialCreditsChange = socialCreditsChange,
                ratingRepository = ratingRepository
            )
            is PrivateChatImpl -> sendNotGroupMessage(sticker)
            else -> Unit
        }
        return@subscribe
    }
}