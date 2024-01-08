package data

import data.dto.UserRatingEntity
import data.dto.UsersRatingsTable
import domain.model.UserRatingInfo
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import util.Constants

class RatingRepositoryImpl(dbPath: String) : RatingRepository {

    init {
        Database.connect("jdbc:sqlite:$dbPath")

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(UsersRatingsTable)
        }
    }

    override fun updateUserRating(
        userId: Long,
        username: String,
        ratingChange: Long
    ): UserRatingInfo {
        return transaction {
            val userRating = UserRatingEntity
                .find { UsersRatingsTable.userId eq userId }
                .singleOrNull()?.apply {
                    this.username = username
                    this.rating += ratingChange
                } ?: UserRatingEntity.new {
                this.userId = userId
                this.username = username
                this.rating = ratingChange
            }

            userRating.toUserRatingInfo().also {
                println("User rating updated: $it")
            }
        }
    }

    override fun getUserRating(userId: Long): UserRatingInfo? {
        return transaction {
            UserRatingEntity
                .find { UsersRatingsTable.userId eq userId }
                .singleOrNull()?.toUserRatingInfo()
        }
    }

    override fun getRatingsList(): List<UserRatingInfo> {
        return transaction {
            UserRatingEntity
                .all()
                .limit(Constants.RATING_REPO_RATINGS_LIST_SELECTION_LIMIT)
                .map { it.toUserRatingInfo() }
                .sortedByDescending { it.rating }
        }
    }
}