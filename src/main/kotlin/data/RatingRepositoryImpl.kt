package data

import data.dto.UserRatingsHistoryEntity
import data.dto.UserRatingsHistoryTable
import data.dto.UserSocialCreditEntity
import data.dto.UsersSocialCreditsTable
import domain.model.UserRatingInfo
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import util.Constants

class RatingRepositoryImpl(dbPath: String) : RatingRepository {

    init {
        Database.connect("jdbc:sqlite:$dbPath")

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(UsersSocialCreditsTable)
            SchemaUtils.create(UserRatingsHistoryTable)
        }
    }

    override fun getRatingsList(): List<UserRatingInfo> {
        return transaction {
            UserSocialCreditEntity
                .all()
                .limit(Constants.RATING_REPO_RATINGS_LIST_SELECTION_LIMIT)
                .map { it.toUserRatingInfo() }
                .sortedByDescending { it.socialCredits }
        }
    }

    override fun getUserRating(
        groupId: Long,
        userId: Long
    ): UserRatingInfo? {
        return transaction {
            UserSocialCreditEntity
                .find { (UsersSocialCreditsTable.groupId eq groupId) and (UsersSocialCreditsTable.userId eq userId) }
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
        socialCreditsChange: Long
    ): Result<UserRatingInfo> {
        return transaction {
            var ratingStatus: String? = null
            val currentTimeInMillis = System.currentTimeMillis()

            UserRatingsHistoryEntity.find {
                (UserRatingsHistoryTable.groupId eq groupId) and
                        (UserRatingsHistoryTable.raterUserId eq messageSenderId) and
                        (UserRatingsHistoryTable.targetUserId eq userId)
            }.singleOrNull()?.apply {
                val isCoolDownOver = currentTimeInMillis - this.modifiedAt > Constants.RATING_COOL_DOWN_IN_MILLIS
                if (isCoolDownOver) {
                    this.modifiedAt = currentTimeInMillis
                } else {
                    return@transaction Result.failure(Throwable("Cool-Down isn't over yet!"))
                }
            } ?: UserRatingsHistoryEntity.new {
                this.groupId = groupId
                this.raterUserId = messageSenderId
                this.targetUserId = userId
                this.createdAt = currentTimeInMillis
                this.modifiedAt = currentTimeInMillis
            }

            val userRating = UserSocialCreditEntity
                .find { (UsersSocialCreditsTable.groupId eq groupId) and (UsersSocialCreditsTable.userId eq userId) }
                .singleOrNull()?.apply {
                    this.groupTitle = groupTitle
                    this.username = username
                    this.firstName = firstName
                    this.socialCredits += socialCreditsChange
                    this.modifiedAt = currentTimeInMillis
                    ratingStatus = "updated"
                } ?: UserSocialCreditEntity.new {
                this.groupId = groupId
                this.groupTitle = groupTitle
                this.userId = userId
                this.username = username
                this.firstName = firstName
                this.socialCredits = socialCreditsChange
                this.createdAt = currentTimeInMillis
                this.modifiedAt = currentTimeInMillis
                ratingStatus = "created"
            }

            Result.success(userRating.toUserRatingInfo().also {
                println("User social credits $ratingStatus: $it")
            })
        }
    }
}