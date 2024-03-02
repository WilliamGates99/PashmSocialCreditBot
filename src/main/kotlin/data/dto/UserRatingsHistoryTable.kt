package data.dto

import org.jetbrains.exposed.dao.id.LongIdTable

object UserRatingsHistoryTable : LongIdTable("user_ratings_history") {
    val groupId = long("groupId").index()
    val raterUserId = long("raterUserId").index()
    val targetUserId = long("targetUserId").index()
    val createdAt = long("createdAt").index()
    val modifiedAt = long("modifiedAt").index()
    val modifiedAtDate = varchar("modifiedAtDate", length = 10).index()
}