package util

import com.github.kotlintelegrambot.entities.Message

object Stickers {

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
            Stickers.plusSocialCreditStickers.contains(sticker?.fileUniqueId) -> Constants.DEFAULT_PLUS_CREDIT
            Stickers.minusSocialCreditStickers.contains(sticker?.fileUniqueId) -> Constants.DEFAULT_MINUS_CREDIT
            Stickers.plusRicePlateStickers.contains(sticker?.fileUniqueId) -> Constants.DEFAULT_PLUS_RICE_PLATE_CREDIT
            else -> null
        }
    }
}