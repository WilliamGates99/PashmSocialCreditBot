package util

object SocialClass {

    private const val LEADER_LI_RIGHT_HAND = "Leader Xi's Right Hand"
    private const val GOOD_CITIZEN = "Good Citizen"
    private const val COMRADE = "Comrade"
    private const val BAD_CITIZEN = "Bad Citizen"
    private const val TO_BE_EXECUTED = "To Be Executed"

    fun getComradeSocialClass(socialCredits: Long): String {
        return when {
            socialCredits >= Constants.SOCIAL_CLASS_CREDIT_POSITIVE_1000 -> LEADER_LI_RIGHT_HAND
            socialCredits >= Constants.SOCIAL_CLASS_CREDIT_POSITIVE250 -> GOOD_CITIZEN
            socialCredits <= Constants.SOCIAL_CLASS_CREDIT_NEGATIVE_1000 -> TO_BE_EXECUTED
            socialCredits <= Constants.SOCIAL_CLASS_CREDIT_NEGATIVE_250 -> BAD_CITIZEN
            else -> COMRADE
        }
    }
}