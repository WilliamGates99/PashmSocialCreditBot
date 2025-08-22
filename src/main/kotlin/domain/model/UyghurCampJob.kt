package domain.model

enum class UyghurCampJob(
    val description: String
) {
    MAKING_SNEAKERS(description = "making Adidas sneakers"),
    SEWING_SOCKS(description = "sewing Nike socks"),
    SEWING_UNDERWEAR(description = "sewing H&M underwear"),
    MAKING_T_SHIRT(description = "making Uniqlo T-shirt"),
    ASSEMBLING_MERCEDES_BENZ(description = "assembling Mercedes-Benz CLA"),
    LUBRICATING_CLUTCH(description = "lubricating DSG clutches for VAG"),
    ASSEMBLING_IPHONE(description = "assembling iPhones"),
    ASSEMBLING_IPAD(description = "assembling iPads"),
    ASSEMBLING_MACBOOK(description = "assembling MacBooks"),
    WASHING_FEET(description = "washing party members' feet"),
    CLEANING_TOILET(description = "cleaning toilets")
}