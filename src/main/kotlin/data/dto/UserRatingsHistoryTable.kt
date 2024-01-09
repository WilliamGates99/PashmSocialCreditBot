package data.dto

import org.jetbrains.exposed.dao.id.LongIdTable

object UserRatingsHistoryTable : LongIdTable("user_ratings_history") {
    val groupId = long("groupId").index()
    val userId = long("userId").index()
    val createdAt = long("createdAt").index()
    val modifiedAt = long("modifiedAt").index()
}