package uk.co.outchain.files

import io.vertx.core.json.JsonObject
import uk.co.outchain.files.server.NodeAddress
import java.nio.file.Path
import java.nio.file.Paths

object Config {
    const val CONFIG_HTTP_SERVER_PORT = "http.server.port"
    const val HTTP_SERVER_ADDRESS = "http.server.address"
    const val NODE_NAME = "node.name"
    const val KEYS_DIRECTORY = "fs.keys"
    const val ROOT_DIRECTORY = "fs.root"

    fun httpServerPort(config: JsonObject): Int =
        config.getInteger(CONFIG_HTTP_SERVER_PORT) ?: 8080

    fun httpServerAddress(config: JsonObject): String =
        config.getString(HTTP_SERVER_ADDRESS) ?: "127.0.0.1"

    fun nodeAddress(config: JsonObject): NodeAddress =
        NodeAddress(
            httpServerAddress(config),
            httpServerPort(config)
        )

    fun nodeName(config: JsonObject): String =
        config.getString(NODE_NAME) ?: "127.0.0.1"

    fun keysDirectory(config: JsonObject): Path =
        Paths.get(config.getString(KEYS_DIRECTORY) ?: "keys")

    fun rootDirectory(config: JsonObject): Path =
        Paths.get(config.getString(ROOT_DIRECTORY) ?: "root")
}
