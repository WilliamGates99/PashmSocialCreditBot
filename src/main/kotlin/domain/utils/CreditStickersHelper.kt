package domain.utils

import dev.inmo.tgbotapi.types.chat.PrivateChatImpl
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.StickerContent
import dev.inmo.tgbotapi.utils.RiskFeature
import domain.model.CreditSticker

@OptIn(RiskFeature::class)
fun ContentMessage<StickerContent>.getSocialCreditChange(): Long? {
    when (this.chat) {
        is PrivateChatImpl -> {
            println("sticker fileUniqueId: ${content.media.fileUniqueId}")
            println("sticker fileId: ${content.media.fileId}")
        }
        else -> Unit
    }

    return when {
        CreditSticker.PLUS_100_CREDIT.fileUniqueIds.contains(content.media.fileUniqueId.string) -> CreditSticker.PLUS_100_CREDIT
        CreditSticker.PLUS_50_CREDIT.fileUniqueIds.contains(content.media.fileUniqueId.string) -> CreditSticker.PLUS_50_CREDIT
        CreditSticker.PLUS_20_CREDIT.fileUniqueIds.contains(content.media.fileUniqueId.string) -> CreditSticker.PLUS_20_CREDIT
        CreditSticker.ZERO_CHANGE_CREDIT.fileUniqueIds.contains(content.media.fileUniqueId.string) -> CreditSticker.ZERO_CHANGE_CREDIT
        CreditSticker.MINUS_20_CREDIT.fileUniqueIds.contains(content.media.fileUniqueId.string) -> CreditSticker.MINUS_20_CREDIT
        CreditSticker.MINUS_50_CREDIT.fileUniqueIds.contains(content.media.fileUniqueId.string) -> CreditSticker.MINUS_50_CREDIT
        CreditSticker.MINUS_100_CREDIT.fileUniqueIds.contains(content.media.fileUniqueId.string) -> CreditSticker.MINUS_100_CREDIT
        else -> null
    }?.credit
}