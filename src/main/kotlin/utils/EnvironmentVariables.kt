package utils

object EnvironmentVariables {

    fun getRenderPort(): Int = System.getenv("PORT")?.toIntOrNull() ?: 10000

    fun getBotToken(): String = System.getenv("BOT_TOKEN")
        ?: throw IllegalStateException("Variable named BOT_TOKEN was not found.")

    fun getDbUrl(): String = System.getenv("POSTGRESQL_URL")
        ?: throw IllegalStateException("Variable named POSTGRESQL_URL was not found.")

    fun getDbUsername(): String = System.getenv("POSTGRESQL_USERNAME")
        ?: throw IllegalStateException("Variable named POSTGRESQL_USERNAME was not found.")

    fun getDbPassword(): String = System.getenv("POSTGRESQL_PASSWORD")
        ?: throw IllegalStateException("Variable named POSTGRESQL_PASSWORD was not found.")

    fun getProxyHost(): String = System.getenv("PROXY_HOST")
        ?: throw IllegalStateException("Variable named PROXY_HOST was not found.")

    fun getProxyPort(): Int = System.getenv("PROXY_PORT")?.toIntOrNull()
        ?: throw IllegalStateException("Variable named PROXY_PORT was not found.")

    fun getProxyUsername(): String = System.getenv("PROXY_USERNAME")
        ?: throw IllegalStateException("Variable named PROXY_USERNAME was not found.")

    fun getProxyPassword(): String = System.getenv("PROXY_PASSWORD")
        ?: throw IllegalStateException("Variable named PROXY_PASSWORD was not found.")
}