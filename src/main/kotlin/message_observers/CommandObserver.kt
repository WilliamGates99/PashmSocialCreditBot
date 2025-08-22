package message_observers

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.types.chat.ForumChatImpl
import dev.inmo.tgbotapi.types.chat.GroupChatImpl
import dev.inmo.tgbotapi.types.chat.PrivateChatImpl
import dev.inmo.tgbotapi.types.chat.SupergroupChatImpl
import dev.inmo.tgbotapi.utils.RiskFeature
import domain.model.Command
import domain.model.TextArtMessage
import domain.repositories.RatingRepository
import utils.CommandHelper.sendHappyMerchantTextArt
import utils.CommandHelper.sendNotGroupMessage
import utils.CommandHelper.sendWinnieThePoohTextArt
import utils.CommandHelper.sendXiJinpingTextArt
import utils.CommandHelper.showGroupSocialCreditsList
import utils.CommandHelper.showMyCredits
import utils.CommandHelper.showOthersCredits

@OptIn(RiskFeature::class)
suspend fun BehaviourContext.observerCommands(
    ratingRepository: RatingRepository
) {
    onCommand(
        command = TextArtMessage.XI_JINPING.command
    ) { message ->
        sendXiJinpingTextArt(message = message)
    }

    onCommand(
        command = TextArtMessage.WINNIE_THE_POOH.command
    ) { message ->
        sendWinnieThePoohTextArt(message = message)
    }

    onCommand(
        command = TextArtMessage.HAPPY_MERCHANT.command
    ) { message ->
        sendHappyMerchantTextArt(message = message)
    }

    onCommand(
        command = Command.SHOW_MY_CREDITS.command
    ) { message ->
        when (message.chat) {
            is GroupChatImpl -> showMyCredits(
                message = message,
                ratingRepository = ratingRepository
            )
            is SupergroupChatImpl -> showMyCredits(
                message = message,
                ratingRepository = ratingRepository
            )
            is ForumChatImpl -> showMyCredits(
                message = message,
                ratingRepository = ratingRepository
            )
            is PrivateChatImpl -> sendNotGroupMessage(message = message)
            else -> Unit
        }
    }

    onCommand(
        command = Command.SHOW_OTHERS_CREDITS.command
    ) { message ->
        when (message.chat) {
            is GroupChatImpl -> showOthersCredits(
                message = message,
                ratingRepository = ratingRepository
            )
            is SupergroupChatImpl -> showOthersCredits(
                message = message,
                ratingRepository = ratingRepository
            )
            is ForumChatImpl -> showOthersCredits(
                message = message,
                ratingRepository = ratingRepository
            )
            is PrivateChatImpl -> sendNotGroupMessage(message = message)
            else -> Unit
        }
    }

    onCommand(
        command = Command.SHOW_COMRADES_RANK.command
    ) { message ->
        when (message.chat) {
            is GroupChatImpl -> showGroupSocialCreditsList(
                message = message,
                ratingRepository = ratingRepository
            )
            is SupergroupChatImpl -> showGroupSocialCreditsList(
                message = message,
                ratingRepository = ratingRepository
            )
            is ForumChatImpl -> showGroupSocialCreditsList(
                message = message,
                ratingRepository = ratingRepository
            )
            is PrivateChatImpl -> sendNotGroupMessage(message = message)
            else -> Unit
        }
    }
}