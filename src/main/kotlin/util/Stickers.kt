package util

import com.github.kotlintelegrambot.entities.Message

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

    fun Message.getSocialCreditChange(): Long? {
        println("sticker fileUniqueId: ${sticker?.fileUniqueId}")
        println("sticker fileId: ${sticker?.fileId}")

        return when {
            plus100SocialCreditStickers.contains(sticker?.fileUniqueId) -> Constants.PLUS_100_CREDIT
            plus50SocialCreditStickers.contains(sticker?.fileUniqueId) -> Constants.PLUS_50_CREDIT
            plus20SocialCreditStickers.contains(sticker?.fileUniqueId) -> Constants.PLUS_20_CREDIT
            zeroChangeSocialCreditStickers.contains(sticker?.fileUniqueId) -> Constants.ZERO_CHANGE_CREDIT
            minus20SocialCreditStickers.contains(sticker?.fileUniqueId) -> Constants.MINUS_20_CREDIT
            minus50SocialCreditStickers.contains(sticker?.fileUniqueId) -> Constants.MINUS_50_CREDIT
            minus100SocialCreditStickers.contains(sticker?.fileUniqueId) -> Constants.MINUS_100_CREDIT
            else -> null
        }
    }
}