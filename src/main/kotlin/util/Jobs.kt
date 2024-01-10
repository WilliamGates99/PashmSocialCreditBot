package util

import com.github.kotlintelegrambot.entities.User

object Jobs {

    private val uyghurCampJobs = arrayOf(
        "making Adidas sneakers",
        "sewing Nike socks",
        "sewing H&M underwear",
        "making Uniqlo T-shirt",
        "assembling Mercedes-Benz CLA",
        "lubricating DSG clutches for VAG",
        "assembling iPhone"
    )

    fun sendToUyghurCampIfNeeded(
        previousSocialCredits: Long,
        currentSocialCredits: Long,
        user: User
    ): String? {
        return when {
            previousSocialCredits >= 0L && currentSocialCredits < 0L -> {
                val job = uyghurCampJobs.random()
                "\uD83C\uDF34The party has decided to send Comrade ${user.firstName} to a Uyghur camp where he will be $job. The Party is taking care of the bad comrades.\uD83D\uDC6E\uD83C\uDFFB\u200D♂\uFE0F"
            }
            previousSocialCredits < 0L && currentSocialCredits >= 0L -> {
                "\uD83C\uDFE1The party has decided to return comrade ${user.firstName} from the Uyghur camp. Be careful from now on!\uD83D\uDC6E\uD83C\uDFFB\u200D♂\uFE0F"
            }
            else -> null
        }
    }
}