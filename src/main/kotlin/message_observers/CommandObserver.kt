package message_observers

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.types.chat.ForumChatImpl
import dev.inmo.tgbotapi.types.chat.GroupChatImpl
import dev.inmo.tgbotapi.types.chat.PrivateChatImpl
import dev.inmo.tgbotapi.types.chat.SupergroupChatImpl
import dev.inmo.tgbotapi.utils.RiskFeature
import domain.repositories.RatingRepository
import utils.CommandHelper.sendNotGroupMessage
import utils.CommandHelper.sendWinnieThePoohTextArt
import utils.CommandHelper.sendXiJinpingTextArt
import utils.CommandHelper.showGroupSocialCreditsList
import utils.CommandHelper.showMyCredits
import utils.CommandHelper.showOthersCredits
import utils.Constants

@OptIn(RiskFeature::class)
suspend fun BehaviourContext.observerCommands(
    ratingRepository: RatingRepository
) {
    onCommand(command = Constants.COMMAND_SEND_TEXT_ART_XI_JINPING) { message ->
        sendXiJinpingTextArt(message)
    }

    onCommand(command = Constants.COMMAND_SEND_TEXT_ART_WINNIE_THE_POOH) { message ->
        sendWinnieThePoohTextArt(message)
    }

    onCommand(command = Constants.COMMAND_SHOW_MY_CREDITS) { message ->
        when (message.chat) {
            is GroupChatImpl -> showMyCredits(message, ratingRepository)
            is SupergroupChatImpl -> showMyCredits(message, ratingRepository)
            is ForumChatImpl -> showMyCredits(message, ratingRepository)
            is PrivateChatImpl -> sendNotGroupMessage(message)
            else -> Unit
        }
    }

    onCommand(command = Constants.COMMAND_SHOW_OTHERS_CREDITS) { message ->
        when (message.chat) {
            is GroupChatImpl -> showOthersCredits(message, ratingRepository)
            is SupergroupChatImpl -> showOthersCredits(message, ratingRepository)
            is ForumChatImpl -> showOthersCredits(message, ratingRepository)
            is PrivateChatImpl -> sendNotGroupMessage(message)
            else -> Unit
        }
    }

    onCommand(command = Constants.COMMAND_SHOW_COMRADES_RANK) { message ->
        when (message.chat) {
            is GroupChatImpl -> showGroupSocialCreditsList(message, ratingRepository)
            is SupergroupChatImpl -> showGroupSocialCreditsList(message, ratingRepository)
            is ForumChatImpl -> showGroupSocialCreditsList(message, ratingRepository)
            is PrivateChatImpl -> sendNotGroupMessage(message)
            else -> Unit
        }
    }
}