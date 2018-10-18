package uk.co.outchain.files

import io.reactivex.Single
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxTestContext
import uk.co.outchain.files.server.ServerVerticle
import java.io.File
import java.nio.file.Paths

abstract class VertxTest {
    protected val classloader by lazy {
        Thread.currentThread().contextClassLoader
    }

    protected fun deployNode(vertx: Vertx, testContext: VertxTestContext, name: String): Single<ServerVerticle> {
        return Single.create { emitter ->
            val configPath = Paths.get("envs", name, "config.json")
            val stream = classloader.getResourceAsStream(configPath.toString())
            val configSource = String(stream.readBytes())
            val config = JsonObject(configSource)

            val keysPath = Paths.get("envs", name, "keys").toString()
            val rootPath = Paths.get("envs", name, "root").toString()

            val keysUrl = classloader.getResource(keysPath)
            val rootUrl = classloader.getResource(rootPath)

            assert(keysUrl != null) { "keys path not found \"$keysPath\"" }
            assert(rootUrl != null) { "root path not found \"$rootPath\"" }

            config.put(Config.KEYS_DIRECTORY, keysUrl.file)
            config.put(Config.ROOT_DIRECTORY, rootUrl.file)

            val verticle = ServerVerticle()
            val options = DeploymentOptions()
                .setConfig(config)
                .setInstances(1)

            vertx.deployVerticle(verticle, options, testContext.succeeding { _ ->
                emitter.onSuccess(verticle)
            })
        }
    }

    fun rxVertx(vertx: io.vertx.core.Vertx): io.vertx.reactivex.core.Vertx {
        return io.vertx.reactivex.core.Vertx(vertx)
    }

    inline fun VertxTestContext.junitVertxCatch(crossinline job: () -> Unit) {
        try {
            job()
        } catch (e: Throwable) {
            failNow(e)
        }
    }
}
