package message_observers

import dev.inmo.micro_utils.coroutines.subscribe
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.shortcuts.textMessages
import dev.inmo.tgbotapi.types.chat.ForumChatImpl
import dev.inmo.tgbotapi.types.chat.GroupChatImpl
import dev.inmo.tgbotapi.types.chat.PrivateChatImpl
import dev.inmo.tgbotapi.types.chat.SupergroupChatImpl
import dev.inmo.tgbotapi.utils.RiskFeature
import domain.model.StickerMessage
import kotlinx.coroutines.CoroutineScope
import utils.MessageHelper.sendLongLiveTheKingSticker
import utils.MessageHelper.sendStickerSet
import utils.MessageHelper.sendWomenGif
import utils.MessageHelper.shouldSendLongLiveTheKingSticker
import utils.MessageHelper.shouldSendWomenGif

@OptIn(RiskFeature::class)
fun BehaviourContext.observeTextMessages(
    scope: CoroutineScope
) {
    textMessages().subscribe(
        scope = scope
    ) { message ->
        val shouldSendStickerSet = message.content.text.equals(
            other = StickerMessage.STICKER_SET.messages.first(),
            ignoreCase = true
        )
        if (shouldSendStickerSet) {
            when (message.chat) {
                is PrivateChatImpl -> sendStickerSet(message = message)
                else -> Unit
            }
            return@subscribe
        }

        if (shouldSendLongLiveTheKingSticker(message)) {
            when (message.chat) {
                is GroupChatImpl -> sendLongLiveTheKingSticker(message = message)
                is SupergroupChatImpl -> sendLongLiveTheKingSticker(message = message)
                is ForumChatImpl -> sendLongLiveTheKingSticker(message = message)
                else -> Unit
            }
            return@subscribe
        }

        if (shouldSendWomenGif(message)) {
            when (message.chat) {
                is GroupChatImpl -> sendWomenGif(message = message)
                is SupergroupChatImpl -> sendWomenGif(message = message)
                is ForumChatImpl -> sendWomenGif(message = message)
                else -> Unit
            }
            return@subscribe
        }
    }
}