package util

import java.io.File
import java.io.FileInputStream
import java.util.*

class PropertiesHelper(private val propertiesFilePath: String) {

    private fun getLocalProperties(): Properties = Properties().apply {
        FileInputStream(File(propertiesFilePath)).use(::load)
    }

    fun getBotToken(): String = getLocalProperties()
        .getProperty(Constants.PROPERTY_BOT_TOKEN)
        ?: throw IllegalStateException("Property named \"${Constants.PROPERTY_BOT_TOKEN}\" was not found.")

    fun getDbPath(): String = getLocalProperties()
        .getProperty(Constants.PROPERTY_RATING_DB_PATH)
        ?: throw IllegalStateException("Property named \"${Constants.PROPERTY_BOT_TOKEN}\" was not found.")
}