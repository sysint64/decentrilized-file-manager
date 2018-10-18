package uk.co.outchain.files

import io.reactivex.rxkotlin.subscribeBy
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.config.ConfigRetriever
import org.slf4j.LoggerFactory

class MainVerticle : AbstractVerticle() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(MainVerticle::class.java)
    }

    override fun start(startFuture: Future<Void>) {
        val retriever = ConfigRetriever.create(vertx)

        retriever.rxGetConfig()
            .flatMap { config ->
                vertx
                    .rxDeployVerticle(
                        "uk.co.outchain.files.client.ClientVerticle",
                        DeploymentOptions()
                            .setConfig(config)
                            .setInstances(1)
                    )
                    .flatMap {
                        vertx.rxDeployVerticle(
                            "uk.co.outchain.files.server.ServerVerticle",
                            DeploymentOptions()
                                .setConfig(config)
                                .setInstances(1)
                        )
                    }
            }
            .subscribeBy(
                onSuccess = {
                    startFuture.complete()
                    LOGGER.info("Successfully started")
                },
                onError = startFuture::fail
            )
    }
}
