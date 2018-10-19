package uk.co.outchain.files.server

import io.vertx.core.Future
import io.vertx.grpc.VertxServer
import io.vertx.grpc.VertxServerBuilder
import io.vertx.reactivex.core.AbstractVerticle
import org.slf4j.LoggerFactory
import uk.co.outchain.files.Config
import uk.co.outchain.files.NodeService

class NodeServerVerticle : AbstractVerticle() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(NodeServerVerticle::class.java)
    }

    lateinit var rpcServer: VertxServer

    val node by lazy {
        Node(
            vertx,
            Config.nodeName(config()),
            Config.nodeAddress(config())
        )
    }

    override fun start(startFuture: Future<Void>) {
        val host = Config.httpServerAddress(config())
        val portNumber = Config.httpServerPort(config())

        // TODO: use SSL
        rpcServer = VertxServerBuilder
            .forAddress(vertx.delegate, host, portNumber)
            .addService(NodeService())
            .build()

        rpcServer.start {
            if (it.succeeded()) {
                LOGGER.info("Successfully started grpc server at $host:$portNumber")
                startFuture.complete()
            } else {
                startFuture.fail(it.cause())
            }
        }
    }
}
