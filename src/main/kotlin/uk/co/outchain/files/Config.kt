package uk.co.outchain.files

import io.vertx.core.json.JsonObject
import java.nio.file.Path
import java.nio.file.Paths

object Config {
    const val CONFIG_HTTP_SERVER_PORT = "http.server.port"
    const val KEYS_DIRECTORY = "fs.keys"
    const val ROOT_DIRECTORY = "fs.root"

    fun httpServerPort(config: JsonObject): Int =
        config.getInteger(CONFIG_HTTP_SERVER_PORT) ?: 8080

    fun keysDirectory(config: JsonObject): Path =
        Paths.get(config.getString(KEYS_DIRECTORY) ?: "keys")

    fun rootDirectory(config: JsonObject): Path =
        Paths.get(config.getString(ROOT_DIRECTORY) ?: "root")
}
