package data.repositories

import data.dto.UserRatingsHistoryEntity
import data.dto.UserRatingsHistoryTable
import data.dto.UserSocialCreditsEntity
import data.dto.UsersSocialCreditsTable
import dev.inmo.tgbotapi.types.chat.member.ChatMember
import domain.model.SocialClass
import domain.model.UserSocialCreditsInfo
import domain.repositories.RatingRepository
import domain.utils.Constants
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

class RatingRepositoryImpl(
    private val dbUrl: String,
    private val dbUser: String,
    private val dbPassword: String
) : RatingRepository {

    init {
        initDatabase()
        createDbSchemas()
    }

    override fun initDatabase() {
        Database.connect(
            url = dbUrl,
            driver = "org.postgresql.Driver",
            user = dbUser,
            password = dbPassword
        )
    }

    override fun createDbSchemas() {
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(UsersSocialCreditsTable)
            SchemaUtils.create(UserRatingsHistoryTable)
        }
    }

    override fun getGroupSocialCreditsList(
        groupId: Long,
        maxRows: Int
    ): List<UserSocialCreditsInfo> {
        return transaction {
            UserSocialCreditsEntity
                .find { UsersSocialCreditsTable.groupId eq groupId }
                .orderBy(Pair(UsersSocialCreditsTable.socialCredits, SortOrder.DESC))
                .orderBy(Pair(UsersSocialCreditsTable.firstName, SortOrder.ASC))
                .limit(n = maxRows)
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
        messageSenderStatus: ChatMember.Status,
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
                    ChatMember.Status.Creator, ChatMember.Status.Administrator -> {
                        currentTimeInMillis - this.modifiedAt > Constants.RATING_COOL_DOWN_IN_MILLIS
                    }
                    ChatMember.Status.Member -> {
                        val modifiedAtDate = LocalDate.parse(this.modifiedAtDate)
                        val until = modifiedAtDate.until(currentDate)
                        val intervalDays = until.days

                        intervalDays >= Constants.RATING_COOL_DOWN_FOR_MEMBERS_IN_DAYS
                    }
                    else -> false
                }

                if (isCoolDownOver) {
                    this.modifiedAt = currentTimeInMillis
                    this.modifiedAtDate = currentDateString
                } else {
                    return@transaction Result.failure(Throwable(Constants.THROWABLE_MESSAGE_COOL_DOWN))
                }
            }.also {
                it?.let { println("User ratings history updated: ${it.toUserRatingsHistory()}") }
            } ?: UserRatingsHistoryEntity.new {
                this.groupId = groupId
                this.raterUserId = messageSenderId
                this.targetUserId = userId
                this.createdAt = currentTimeInMillis
                this.modifiedAt = currentTimeInMillis
                this.modifiedAtDate = currentDateString
            }.also {
                println("User ratings history created: ${it.toUserRatingsHistory()}")
            }

            val userRating = UserSocialCreditsEntity
                .find { (UsersSocialCreditsTable.groupId eq groupId) and (UsersSocialCreditsTable.userId eq userId) }
                .singleOrNull()?.apply {
                    this.groupTitle = groupTitle
                    this.username = username
                    this.firstName = firstName
                    this.socialCredits = (this.socialCredits + socialCreditsChange).coerceAtLeast(
                        minimumValue = SocialClass.EXECUTED.credit
                    )
                    this.modifiedAt = currentTimeInMillis
                    ratingStatus = "updated"
                } ?: UserSocialCreditsEntity.new {
                this.groupId = groupId
                this.groupTitle = groupTitle
                this.userId = userId
                this.username = username
                this.firstName = firstName
                this.socialCredits = socialCreditsChange.coerceAtLeast(
                    minimumValue = SocialClass.EXECUTED.credit
                )
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