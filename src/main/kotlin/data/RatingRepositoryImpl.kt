package data

import data.dto.UserRatingsHistoryEntity
import data.dto.UserRatingsHistoryTable
import data.dto.UserSocialCreditsEntity
import data.dto.UsersSocialCreditsTable
import domain.model.UserSocialCreditsInfo
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import util.Constants
import util.Constants.THROWABLE_MESSAGE_COOL_DOWN
import java.time.LocalDate

class RatingRepositoryImpl(dbPath: String) : RatingRepository {

    init {
        Database.connect(url = "jdbc:sqlite:$dbPath")

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
                .orderBy(Pair(UsersSocialCreditsTable.firstName, SortOrder.ASC))
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
        messageSenderStatus: String,
        groupId: Long,
        groupTitle: String,
        userId: Long,
        username: String,
        firstName: String,
        socialCreditsChange: Long
    ): Result<UserSocialCreditsInfo> = try {
        transaction {
            var ratingStatus: String? = null
            val currentTimeInMillis = System.currentTimeMillis()
            val currentDate = LocalDate.now()
            val currentDateString = currentDate.toString() // yyyy-MM-dd

            UserRatingsHistoryEntity.find {
                (UserRatingsHistoryTable.groupId eq groupId) and
                        (UserRatingsHistoryTable.raterUserId eq messageSenderId) and
                        (UserRatingsHistoryTable.targetUserId eq userId)
            }.singleOrNull()?.apply {
                val isCoolDownOver = when (messageSenderStatus) {
                    Constants.CHAT_MEMBER_STATUS_CREATOR, Constants.CHAT_MEMBER_STATUS_ADMIN -> {
                        currentTimeInMillis - this.modifiedAt > Constants.RATING_COOL_DOWN_IN_MILLIS
                    }
                    Constants.CHAT_MEMBER_STATUS_MEMBER -> {
                        val modifiedAtDate = LocalDate.parse(this.modifiedAtDate)
                        val until = modifiedAtDate.until(currentDate)
                        val intervalDays = until.days

                        intervalDays > 0
                    }
                    else -> false
                }

                if (isCoolDownOver) {
                    this.modifiedAt = currentTimeInMillis
                    this.modifiedAtDate = currentDateString
                } else {
                    return@transaction Result.failure(Throwable(THROWABLE_MESSAGE_COOL_DOWN))
                }
            }.also {
                it?.let { println("User ratings history created: ${it.toUserRatingsHistory()}") }
            } ?: UserRatingsHistoryEntity.new {
                this.groupId = groupId
                this.raterUserId = messageSenderId
                this.targetUserId = userId
                this.createdAt = currentTimeInMillis
                this.modifiedAt = currentTimeInMillis
                this.modifiedAtDate = currentDateString
            }.also {
                println("User ratings history updated: ${it.toUserRatingsHistory()}")
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
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(Throwable("Update user social credits failed with an exception."))
    }
}