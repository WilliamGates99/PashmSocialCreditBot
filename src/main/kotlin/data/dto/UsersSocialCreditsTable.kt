package data.dto

import org.jetbrains.exposed.dao.id.LongIdTable

object UsersSocialCreditsTable : LongIdTable("users_social_credits") {
    val groupId = long("groupId").index()
    val groupTitle = varchar("groupTitle", length = 256).index()
    val userId = long("userId").index()
    val username = varchar("username", length = 256).index()
    val firstName = varchar("firstName", length = 256).index()
    val socialCredits = long("socialCredits").index()
    val createdAt = long("createdAt").index()
    val modifiedAt = long("modifiedAt").index()
}