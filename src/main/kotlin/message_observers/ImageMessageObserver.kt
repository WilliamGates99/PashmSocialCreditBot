package message_observers

import dev.inmo.micro_utils.coroutines.subscribe
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.shortcuts.imageMessages
import dev.inmo.tgbotapi.types.chat.ForumChatImpl
import dev.inmo.tgbotapi.types.chat.GroupChatImpl
import dev.inmo.tgbotapi.types.chat.SupergroupChatImpl
import dev.inmo.tgbotapi.utils.RiskFeature
import kotlinx.coroutines.CoroutineScope
import utils.MessageHelper.sendLongLiveTheKingSticker
import utils.MessageHelper.sendWomenGif
import utils.MessageHelper.shouldSendLongLiveTheKingSticker
import utils.MessageHelper.shouldSendWomenGif

@OptIn(RiskFeature::class)
fun BehaviourContext.observeImageMessages(
    scope: CoroutineScope
) {
    imageMessages().subscribe(scope = scope) { message ->
        if (shouldSendLongLiveTheKingSticker(message)) {
            when (message.chat) {
                is GroupChatImpl -> sendLongLiveTheKingSticker(message)
                is SupergroupChatImpl -> sendLongLiveTheKingSticker(message)
                is ForumChatImpl -> sendLongLiveTheKingSticker(message)
                else -> Unit
            }
            return@subscribe
        }

        if (shouldSendWomenGif(message)) {
            when (message.chat) {
                is GroupChatImpl -> sendWomenGif(message)
                is SupergroupChatImpl -> sendWomenGif(message)
                is ForumChatImpl -> sendWomenGif(message)
                else -> Unit
            }
            return@subscribe
        }
    }
}