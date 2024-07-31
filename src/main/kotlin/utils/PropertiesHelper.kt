package utils

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

    fun getProxyHost(): String = getLocalProperties()
        .getProperty(Constants.PROXY_HOST)
        ?: throw IllegalStateException("Property named \"${Constants.PROXY_HOST}\" was not found.")

    fun getProxyPort(): String = getLocalProperties()
        .getProperty(Constants.PROXY_PORT)
        ?: throw IllegalStateException("Property named \"${Constants.PROXY_PORT}\" was not found.")

    fun getProxyUsername(): String = getLocalProperties()
        .getProperty(Constants.PROXY_USERNAME)
        ?: throw IllegalStateException("Property named \"${Constants.PROXY_USERNAME}\" was not found.")

    fun getProxyPassword(): String = getLocalProperties()
        .getProperty(Constants.PROXY_PASSWORD)
        ?: throw IllegalStateException("Property named \"${Constants.PROXY_PASSWORD}\" was not found.")
}