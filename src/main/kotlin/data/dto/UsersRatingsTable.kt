package data.dto

import org.jetbrains.exposed.dao.id.LongIdTable

object UsersRatingsTable : LongIdTable("user_ratings") {
    val userId = long("userId").uniqueIndex()
    val username = varchar("username", length = 256).index()
    val rating = long("rating").index()
}