package domain.model

enum class StickerMessage(
    val fileId: String,
    val messages: List<String> = emptyList()
) {
    STICKER_SET(
        fileId = "CAACAgQAAxkBAAPgZaa81LNEybPGWsV-NOYaW6PpptkAAqsUAALQVzFREyEyhklyhPk0BA",
        messages = listOf("?")
    ),
    HOLY_KING(
        fileId = "CAACAgQAAxkBAANXZZ6cnGV-Hn7TzIJV4O9s8673hxYAAtoBAALeqGIKh9ilW8uGq9U0BA",
        messages = listOf(
            "long live the king",
            "king masoud",
            "big masoud",
            "Masoud Roshan Pajooh",
            "Masoud Roshan Pazhouh",
            "masoud",
            "کینگ مسعود",
            "بیگ مسعود",
            "مسعود روشن\u200Cپژوه",
            "مسعود روشن پژوه",
            "مسعود",
            "روشن\u200Cپژوه",
            "روشن پژوه",
        )
    )
}