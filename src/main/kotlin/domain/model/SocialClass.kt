package domain.model

enum class SocialClass(
    val title: String,
    val credit: Long
) {
    LEADER_LI_RIGHT_HAND(
        title = "Leader Xi's Right Hand",
        credit = 1500
    ),
    GOOD_CITIZEN(
        title = "Good Citizen",
        credit = 400
    ),
    COMRADE(
        title = "Comrade",
        credit = 0
    ),
    BAD_CITIZEN(
        title = "Bad Citizen",
        credit = -400
    ),
    EXECUTED(
        title = "Executed☠\uFE0F",
        credit = -2000
    )
}