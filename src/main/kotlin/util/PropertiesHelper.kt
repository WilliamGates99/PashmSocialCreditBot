package util

import java.io.File
import java.io.FileInputStream
import java.util.*

object PropertiesHelper {

    private val rootDir = System.getProperty("user.dir")

    private fun getLocalProperties(): Properties {
        return Properties().apply {
            FileInputStream(File("$rootDir/local.properties")).use(::load)
        }
    }

    fun getBotToken(): String {
        return getLocalProperties()
            .getProperty(Constants.PROPERTY_BOT_TOKEN)
            ?: throw IllegalStateException("Property named \"${Constants.PROPERTY_BOT_TOKEN}\" was not found.")
    }

    fun getDbPath(): String {
        return getLocalProperties()
            .getProperty(Constants.PROPERTY_RATING_DB_PATH)
            ?: throw IllegalStateException("Property named \"${Constants.PROPERTY_BOT_TOKEN}\" was not found.")
    }
}