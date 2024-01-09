package data.dto

import org.jetbrains.exposed.dao.id.LongIdTable

object UsersRatingsTable : LongIdTable("user_ratings") {
    val groupId = long("groupId").index()
    val groupTitle = varchar("groupTitle", length = 256).index()
    val userId = long("userId").index()
    val username = varchar("username", length = 256).index()
    val firstName = varchar("firstName", length = 256).index()
    val rating = long("rating").index()
    val createdAt = long("createdAt").index()
    val modifiedAt = long("modifiedAt").index()
}