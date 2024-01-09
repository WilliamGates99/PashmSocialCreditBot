package data

import data.dto.UserRatingEntity
import data.dto.UserRatingsHistoryEntity
import data.dto.UserRatingsHistoryTable
import data.dto.UsersRatingsTable
import domain.model.UserRatingInfo
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import util.Constants

class RatingRepositoryImpl(dbPath: String) : RatingRepository {

    init {
        Database.connect("jdbc:sqlite:$dbPath")

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(UsersRatingsTable)
            SchemaUtils.create(UserRatingsHistoryTable)
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

    override fun getUserRating(
        groupId: Long,
        userId: Long
    ): UserRatingInfo? {
        return transaction {
            UserRatingEntity
                .find { (UsersRatingsTable.groupId eq groupId) and (UsersRatingsTable.userId eq userId) }
                .singleOrNull()?.toUserRatingInfo()
        }
    }

    override fun updateUserRating(
        messageSenderId: Long,
        groupId: Long,
        groupTitle: String,
        userId: Long,
        username: String,
        firstName: String,
        ratingChange: Long
    ): Result<UserRatingInfo> {
        return transaction {
            var ratingStatus: String? = null
            val currentTimeInMillis = System.currentTimeMillis()

            UserRatingsHistoryEntity.find {
                (UserRatingsHistoryTable.groupId eq groupId) and (UserRatingsHistoryTable.userId eq messageSenderId)
            }.singleOrNull()?.apply {
                val isCoolDownOver = currentTimeInMillis - this.modifiedAt > Constants.RATING_COOL_DOWN_IN_MILLIS
                if (isCoolDownOver) {
                    this.modifiedAt = currentTimeInMillis
                } else {
                    return@transaction Result.failure(Throwable("cool down is not over yet"))
                }
            } ?: UserRatingsHistoryEntity.new {
                this.groupId = groupId
                this.userId = messageSenderId
                this.createdAt = currentTimeInMillis
                this.modifiedAt = currentTimeInMillis
            }

            val userRating = UserRatingEntity
                .find { (UsersRatingsTable.groupId eq groupId) and (UsersRatingsTable.userId eq userId) }
                .singleOrNull()?.apply {
                    this.groupTitle = groupTitle
                    this.username = username
                    this.firstName = firstName
                    this.rating += ratingChange
                    this.modifiedAt = currentTimeInMillis
                    ratingStatus = "updated"
                } ?: UserRatingEntity.new {
                this.groupId = groupId
                this.groupTitle = groupTitle
                this.userId = userId
                this.username = username
                this.firstName = firstName
                this.rating = ratingChange
                this.createdAt = currentTimeInMillis
                this.modifiedAt = currentTimeInMillis
                ratingStatus = "created"
            }

            Result.success(userRating.toUserRatingInfo().also {
                println("User rating $ratingStatus: $it")
            })
        }
    }
}