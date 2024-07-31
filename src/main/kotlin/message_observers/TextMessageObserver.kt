package message_observers

import dev.inmo.micro_utils.coroutines.subscribe
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.shortcuts.textMessages
import dev.inmo.tgbotapi.types.chat.ForumChatImpl
import dev.inmo.tgbotapi.types.chat.GroupChatImpl
import dev.inmo.tgbotapi.types.chat.PrivateChatImpl
import dev.inmo.tgbotapi.types.chat.SupergroupChatImpl
import dev.inmo.tgbotapi.utils.RiskFeature
import kotlinx.coroutines.CoroutineScope
import utils.Constants.MESSAGE_BIG_MASOUD
import utils.Constants.MESSAGE_GET_STICKER_SET
import utils.Constants.MESSAGE_KING_MASOUD
import utils.Constants.MESSAGE_LONG_LIVE_THE_KING
import utils.Constants.MESSAGE_MASOUD
import utils.Constants.MESSAGE_WOMEN
import utils.Constants.MESSAGE_WOMEN_BRAIN
import utils.Constants.MESSAGE_WOMEN_COFFEE_1
import utils.Constants.MESSAGE_WOMEN_COFFEE_2
import utils.MessageHelper.sendLongLiveTheKingSticker
import utils.MessageHelper.sendStickerSet
import utils.MessageHelper.sendWomenGif
import java.util.*

@OptIn(RiskFeature::class)
fun BehaviourContext.observeTextMessages(
    scope: CoroutineScope
) {
    textMessages().subscribe(scope = scope) { message ->
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
}