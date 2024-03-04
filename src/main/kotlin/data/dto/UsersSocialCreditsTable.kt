package data.dto

import org.jetbrains.exposed.dao.id.LongIdTable

object UsersSocialCreditsTable : LongIdTable(name = "users_social_credits") {
    val groupId = long(name = "groupId").index()
    val groupTitle = varchar(name = "groupTitle", length = 256).index()
    val userId = long(name = "userId").index()
    val username = varchar(name = "username", length = 256).index()
    val firstName = varchar(name = "firstName", length = 256).index()
    val socialCredits = long(name = "socialCredits").index()
    val createdAt = long(name = "createdAt").index()
    val modifiedAt = long(name = "modifiedAt").index()
}