package domain.utils

import domain.model.SocialClass

fun getComradeSocialClass(
    socialCredits: Long
): String = when {
    socialCredits >= SocialClass.LEADER_LI_RIGHT_HAND.credit -> SocialClass.LEADER_LI_RIGHT_HAND
    socialCredits >= SocialClass.GOOD_CITIZEN.credit -> SocialClass.GOOD_CITIZEN
    socialCredits <= SocialClass.EXECUTED.credit -> SocialClass.EXECUTED
    socialCredits <= SocialClass.BAD_CITIZEN.credit -> SocialClass.BAD_CITIZEN
    else -> SocialClass.COMRADE
}.title