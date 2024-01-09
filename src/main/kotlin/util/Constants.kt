package util

object Constants {

    internal const val PROPERTY_BOT_TOKEN = "BOT_TOKEN"
    internal const val PROPERTY_RATING_DB_PATH = "RATING_DB_PATH"

    internal const val CHAT_MEMBER_STATUS_CREATOR = "creator"
    internal const val CHAT_MEMBER_STATUS_ADMIN = "administrator"
    internal const val CHAT_MEMBER_STATUS_MEMBER = "member"

    internal const val RATING_COOL_DOWN_IN_MILLIS = 5 * 60 * 1000L // 5 Minutes

    internal const val DEFAULT_PLUS_CREDIT = 20L
    internal const val DEFAULT_MINUS_CREDIT = -20L

    internal const val DEFAULT_PLUS_RICE_PLATE_CREDIT = 40L

    internal const val COMMAND_SHOW_CITIZENS_RANK = "citizensrank"
    internal const val COMMAND_SHOW_MY_CREDITS = "mycredits"
    internal const val COMMAND_SHOW_OTHER_CREDITS = "credits"

    internal const val RATING_REPO_RATINGS_LIST_SELECTION_LIMIT = 50
}