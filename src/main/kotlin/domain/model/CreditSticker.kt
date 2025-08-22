package domain.model

enum class CreditSticker(
    val credit: Long,
    val fileUniqueIds: Array<String>
) {
    PLUS_100_CREDIT(
        credit = 100,
        fileUniqueIds = arrayOf(
            "AgADqxQAAtBXMVE"
        )
    ),
    PLUS_50_CREDIT(
        credit = 50,
        fileUniqueIds = arrayOf(
            "AgADchEAAmiiMVE"
        )
    ),
    PLUS_20_CREDIT(
        credit = 20,
        fileUniqueIds = arrayOf(
            "AgADzxIAAth9OFE",
            "AgADAgADf3BGHA",
            "AgADPA0AAtKZWEg"
        )
    ),
    ZERO_CHANGE_CREDIT(
        credit = 0,
        fileUniqueIds = arrayOf(
            "AgADEQ8AAup9iVI",
            "AgAD7xQAAlxpOVE"
        )
    ),
    MINUS_20_CREDIT(
        credit = -20,
        fileUniqueIds = arrayOf(
            "AgADrxkAAjdXOFE",
            "AgADAwADf3BGHA",
            "AgADLBEAAtc2WEg"
        )
    ),
    MINUS_50_CREDIT(
        credit = -50,
        fileUniqueIds = arrayOf(
            "AgADHhkAAj42MFE"
        )
    ),
    MINUS_100_CREDIT(
        credit = -100,
        fileUniqueIds = arrayOf(
            "AgADNxMAAmUfMFE"
        )
    )
}