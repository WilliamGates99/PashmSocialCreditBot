package util

import com.github.kotlintelegrambot.entities.Message

object Stickers {

    internal const val STICKER_SET_FILE_ID = "CAACAgEAAx0CRMHEJQABCEx7ZZ7DErK2TT7oM0591IB28aP9zHYAAgIAA39wRhwFzGTYNyIryDQE"
    internal const val HOLY_KING_FILE_ID = "CAACAgQAAxkBAANXZZ6cnGV-Hn7TzIJV4O9s8673hxYAAtoBAALeqGIKh9ilW8uGq9U0BA"

    private val plusSocialCreditStickers = arrayOf(
        "AgADAgADf3BGHA",
        "AgADPA0AAtKZWEg"
    )

    private val minusSocialCreditStickers = arrayOf(
        "AgADAwADf3BGHA",
        "AgADLBEAAtc2WEg"
    )

    private val plusRicePlateStickers = arrayOf(
        "AgADugwAAkaXMUo"
    )

    fun Message.getSocialCreditChange(): Long? {
        println("sticker fileUniqueId: ${sticker?.fileUniqueId}")
        println("sticker fileId: ${sticker?.fileId}")

        return when {
            plusSocialCreditStickers.contains(sticker?.fileUniqueId) -> Constants.DEFAULT_PLUS_CREDIT
            minusSocialCreditStickers.contains(sticker?.fileUniqueId) -> Constants.DEFAULT_MINUS_CREDIT
            plusRicePlateStickers.contains(sticker?.fileUniqueId) -> Constants.DEFAULT_PLUS_RICE_PLATE_CREDIT
            else -> null
        }
    }
}