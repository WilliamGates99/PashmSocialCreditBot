package data.dto

import org.jetbrains.exposed.dao.id.LongIdTable

object UserRatingsHistoryTable : LongIdTable(name = "user_ratings_history") {
    val groupId = long(name = "groupId").index()
    val raterUserId = long(name = "raterUserId").index()
    val targetUserId = long(name = "targetUserId").index()
    val createdAt = long(name = "createdAt").index()
    val modifiedAt = long(name = "modifiedAt").index()
    val modifiedAtDate = varchar(name = "modifiedAtDate", length = 10).index()
}