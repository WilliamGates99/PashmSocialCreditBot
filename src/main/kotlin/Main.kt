import data.repositories.RatingRepositoryImpl
import dev.inmo.tgbotapi.extensions.api.telegramBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import domain.repositories.RatingRepository
import io.ktor.client.engine.*
import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import message_observers.*
import utils.EnvironmentVariables
import java.net.Authenticator
import java.net.PasswordAuthentication

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
                    val isRequestSentToProxy = requestingHost.equals(
                        other = EnvironmentVariables.getProxyHost(),
                        ignoreCase = true
                    )

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
                port = EnvironmentVariables.getProxyPort()
            )
        }
    }

    // Start a minimal Ktor server for Render web service
    embeddedServer(
        factory = Netty,
        port = EnvironmentVariables.getRenderPort(),
        host = "0.0.0."
    ) {
        routing {
            get(path = "/health") {
                call.respondText(text = "OK", status = HttpStatusCode.OK)
            }
        }
    }.start(wait = false) // Run the server non-blocking, allowing telegram bot’s polling logic to continue

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