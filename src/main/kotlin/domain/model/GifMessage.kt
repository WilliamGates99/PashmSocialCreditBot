package domain.model

enum class GifMessage(
    val fileId: String,
    val messages: List<String> = emptyList()
) {
    COTTON_FARM(
        fileId = "CgACAgQAAxkBAANfZZ6fI3peqNrZvmxeP_OXbV1DVT0AAl8GAAK1GSBRIB_fe0osYoY0BA"
    ),
    POOH_AND_CJ(
        fileId = "CgACAgQAAxkBAANZZZ6c5ydjcfJI9VHCZb9V3RNol3UAAqAEAAJpHGQHyeUWCiOJcY00BA"
    ),
    JOHN_XINA(
        fileId = "CgACAgQAAxkBAAN5ZZ7Phj_VUxKpx6ICHiqN1OTTg_YAAnEDAALQ7CxQpYm4bk4Vags0BA"
    ),
    EXECUTION(
        fileId = "CgACAgQAAxkBAAICsmXlkfBM6AzYxSOCNGHhRJsIM0pKAAKkAAM5_vFSKnqFipz6Zoc0BA"
    ),
    REVIVAL(
        fileId = "CgACAgQAAxkBAAICtWXltRyKPIc3Cwvcw9Bm0yXjX7eyAAJmBgACa7iAUgEWmywkxJ5mNAQ"
    ),
    HMPH_WOMEN(
        fileId = "CgACAgQAAxkBAANdZZ6dwiCXGI4zHtRqHheMmWhlPt0AAloQAAIxFUFQ58yBz5cnU9A0BA",
        messages = listOf(
            "women☕\uFE0F",
            "women ☕\uFE0F",
            "women",
            "aqle zan",
            "aghle zan",
            "عقل زن",
            "زن☕\uFE0F",
            "زن ☕\uFE0F"
        )
    )
}