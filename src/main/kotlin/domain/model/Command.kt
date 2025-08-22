package domain.model

enum class Command(
    val command: String
) {
    SHOW_COMRADES_RANK(
        command = "comradesrank"
    ),
    SHOW_MY_CREDITS(
        command = "mycredits"
    ),
    SHOW_OTHERS_CREDITS(
        command = "credits"
    )
}