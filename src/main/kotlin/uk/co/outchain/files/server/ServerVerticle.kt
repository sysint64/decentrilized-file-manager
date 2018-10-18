package uk.co.outchain.files.server

import io.vertx.core.Future
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.web.Router
import org.slf4j.LoggerFactory
import uk.co.outchain.files.Config

class ServerVerticle : AbstractVerticle() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ServerVerticle::class.java)
    }

    override fun start(startFuture: Future<Void>) {
        val router = Router.router(vertx)
        val portNumber = Config.httpServerPort(config())

        vertx.createHttpServer()
            .requestHandler(router::accept)
            .listen(portNumber) {
                LOGGER.info("Successfully started http server at 127.0.0.1:$portNumber")
                startFuture.complete()
            }
    }
}
