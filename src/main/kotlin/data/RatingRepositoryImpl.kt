package data

import data.dto.UserRatingsHistoryEntity
import data.dto.UserRatingsHistoryTable
import data.dto.UserSocialCreditsEntity
import data.dto.UsersSocialCreditsTable
import domain.model.UserSocialCreditsInfo
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

    override fun getGroupSocialCreditsList(groupId: Long): List<UserSocialCreditsInfo> {
        return transaction {
            UserSocialCreditsEntity
                .find { UsersSocialCreditsTable.groupId eq groupId }
                .orderBy(Pair(UsersSocialCreditsTable.socialCredits, SortOrder.DESC))
                .limit(Constants.GROUP_SOCIAL_CREDITS_LIST_SELECTION_LIMIT)
                .map { it.toUserSocialCreditsInfo() }
        }
    }

    override fun getUserSocialCredits(
        groupId: Long,
        userId: Long
    ): UserSocialCreditsInfo? {
        return transaction {
            UserSocialCreditsEntity
                .find { (UsersSocialCreditsTable.groupId eq groupId) and (UsersSocialCreditsTable.userId eq userId) }
                .singleOrNull()?.toUserSocialCreditsInfo()
        }
    }

    override fun updateUserSocialCredits(
        messageSenderId: Long,
        groupId: Long,
        groupTitle: String,
        userId: Long,
        username: String,
        firstName: String,
        socialCreditsChange: Long
    ): Result<UserSocialCreditsInfo> {
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

            val userRating = UserSocialCreditsEntity
                .find { (UsersSocialCreditsTable.groupId eq groupId) and (UsersSocialCreditsTable.userId eq userId) }
                .singleOrNull()?.apply {
                    this.groupTitle = groupTitle
                    this.username = username
                    this.firstName = firstName
                    this.socialCredits += socialCreditsChange
                    this.modifiedAt = currentTimeInMillis
                    ratingStatus = "updated"
                } ?: UserSocialCreditsEntity.new {
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

            Result.success(userRating.toUserSocialCreditsInfo().also {
                println("User social credits $ratingStatus: $it")
            })
        }
    }
}