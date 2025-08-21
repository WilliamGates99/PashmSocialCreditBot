import data.repositories.RatingRepositoryImpl
import dev.inmo.tgbotapi.extensions.api.telegramBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import domain.repositories.RatingRepository
import io.ktor.client.engine.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import message_observers.*
import utils.EnvironmentVariables
import java.net.Authenticator
import java.net.PasswordAuthentication
import java.util.*

fun main() {
    val ratingRepository: RatingRepository = RatingRepositoryImpl(
        dbUrl = EnvironmentVariables.getDbUrl(),
        dbUser = EnvironmentVariables.getDbUsername(),
        dbPassword = EnvironmentVariables.getDbPassword()
    )

    val telegramBot = telegramBot(
        token = EnvironmentVariables.getBotToken()
    ) {
        Authenticator.setDefault(
            object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication? {
                    val isRequestSentToProxy = requestingHost.lowercase(
                        Locale.US
                    ) == EnvironmentVariables.getProxyHost().lowercase(Locale.US)

                    return if (isRequestSentToProxy) PasswordAuthentication(
                        /* userName = */ EnvironmentVariables.getProxyUsername(),
                        /* password = */ EnvironmentVariables.getProxyPassword().toCharArray()
                    ) else null
                }
            }
        )

        engine {
            proxy = ProxyBuilder.socks(
                host = EnvironmentVariables.getProxyHost(),
                port = EnvironmentVariables.getProxyPort().toInt()
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
                observeAudioMessages(scope = this@launch)
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