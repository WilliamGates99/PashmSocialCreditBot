import data.repositories.RatingRepositoryImpl
import dev.inmo.tgbotapi.extensions.api.telegramBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import domain.repositories.RatingRepository
import io.ktor.client.engine.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import message_observers.*
import utils.PropertiesHelper
import java.net.Authenticator
import java.net.PasswordAuthentication
import java.util.*

fun main(args: Array<String>) {
    val propertiesHelper = PropertiesHelper(propertiesFilePath = args[0])
    val ratingRepository: RatingRepository = RatingRepositoryImpl(dbPath = propertiesHelper.getDbPath())

    val telegramBot = telegramBot(
        token = propertiesHelper.getBotToken()
    ) {
        Authenticator.setDefault(object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication? {
                val isRequestSentToProxy = requestingHost.lowercase(
                    Locale.US
                ) == propertiesHelper.getProxyHost().lowercase(Locale.US)

                return if (isRequestSentToProxy) PasswordAuthentication(
                    /* userName = */ propertiesHelper.getProxyUsername(),
                    /* password = */ propertiesHelper.getProxyPassword().toCharArray()
                ) else null
            }
        })

        engine {
            proxy = ProxyBuilder.socks(
                host = propertiesHelper.getProxyHost(),
                port = propertiesHelper.getProxyPort().toInt()
            )
        }
    }

    runBlocking {
        launch(context = Dispatchers.IO) {
            telegramBot.buildBehaviourWithLongPolling {
                observerCommands(ratingRepository = ratingRepository)

                observeTextMessages(scope = this@launch)
                observeGifMessages(scope = this@launch)
                observeImageMessages(scope = this@launch)
                observeVideoMessages(scope = this@launch)
                observeVoiceMessages(scope = this@launch)

                observeStickerMessages(
                    telegramBot = telegramBot,
                    ratingRepository = ratingRepository,
                    scope = this@launch
                )
            }
        }
    }
}