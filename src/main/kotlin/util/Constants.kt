package util

object Constants {

    internal const val PROPERTY_BOT_TOKEN = "BOT_TOKEN"
    internal const val PROPERTY_RATING_DB_PATH = "RATING_DB_PATH"

    internal const val CHAT_MEMBER_STATUS_CREATOR = "creator"
    internal const val CHAT_MEMBER_STATUS_ADMIN = "administrator"
    internal const val CHAT_MEMBER_STATUS_MEMBER = "member"

    internal const val COMMAND_SHOW_COMRADES_RANK = "comradesrank"
    internal const val COMMAND_SHOW_MY_CREDITS = "mycredits"
    internal const val COMMAND_SHOW_OTHERS_CREDITS = "credits"

    internal const val MESSAGE_GET_STICKER_SET = "?"
    internal const val MESSAGE_LONG_LIVE_THE_KING = "long live the king"
    internal const val MESSAGE_WOMEN = "women☕\uFE0F"

    internal const val RATING_COOL_DOWN_IN_MINUTES = 5
    internal const val RATING_COOL_DOWN_IN_MILLIS = 5 * 60 * 1000L // 5 Minutes
    internal const val GROUP_SOCIAL_CREDITS_LIST_SELECTION_LIMIT = 10
    internal const val MIN_SOCIAL_CREDITS_FOR_PROUD_PARTY_MESSAGE = 100L
    internal const val MIN_SOCIAL_CREDITS_FOR_PROUD_PARTY_GIF = 1000L

    internal const val DEFAULT_PLUS_CREDIT = 20L
    internal const val DEFAULT_MINUS_CREDIT = -20L
    internal const val DEFAULT_PLUS_RICE_PLATE_CREDIT = 40L
}