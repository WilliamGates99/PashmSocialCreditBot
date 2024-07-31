package utils

import dev.inmo.tgbotapi.types.chat.PrivateChatImpl
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.StickerContent
import dev.inmo.tgbotapi.utils.RiskFeature

object Stickers {

    internal const val STICKER_SET_FILE_ID = "CAACAgQAAxkBAAPgZaa81LNEybPGWsV-NOYaW6PpptkAAqsUAALQVzFREyEyhklyhPk0BA"
    internal const val HOLY_KING_FILE_ID = "CAACAgQAAxkBAANXZZ6cnGV-Hn7TzIJV4O9s8673hxYAAtoBAALeqGIKh9ilW8uGq9U0BA"

    private val plus100SocialCreditStickers = arrayOf(
        "AgADqxQAAtBXMVE"
    )

    private val plus50SocialCreditStickers = arrayOf(
        "AgADchEAAmiiMVE"
    )

    private val plus20SocialCreditStickers = arrayOf(
        "AgADzxIAAth9OFE",
        "AgADAgADf3BGHA",
        "AgADPA0AAtKZWEg"
    )

    private val zeroChangeSocialCreditStickers = arrayOf(
        "AgADEQ8AAup9iVI",
        "AgAD7xQAAlxpOVE"
    )

    private val minus20SocialCreditStickers = arrayOf(
        "AgADrxkAAjdXOFE",
        "AgADAwADf3BGHA",
        "AgADLBEAAtc2WEg"
    )

    private val minus50SocialCreditStickers = arrayOf(
        "AgADHhkAAj42MFE"
    )

    private val minus100SocialCreditStickers = arrayOf(
        "AgADNxMAAmUfMFE"
    )

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
            plus100SocialCreditStickers.contains(content.media.fileUniqueId.string) -> Constants.PLUS_100_CREDIT
            plus50SocialCreditStickers.contains(content.media.fileUniqueId.string) -> Constants.PLUS_50_CREDIT
            plus20SocialCreditStickers.contains(content.media.fileUniqueId.string) -> Constants.PLUS_20_CREDIT
            zeroChangeSocialCreditStickers.contains(content.media.fileUniqueId.string) -> Constants.ZERO_CHANGE_CREDIT
            minus20SocialCreditStickers.contains(content.media.fileUniqueId.string) -> Constants.MINUS_20_CREDIT
            minus50SocialCreditStickers.contains(content.media.fileUniqueId.string) -> Constants.MINUS_50_CREDIT
            minus100SocialCreditStickers.contains(content.media.fileUniqueId.string) -> Constants.MINUS_100_CREDIT
            else -> null
        }
    }
}