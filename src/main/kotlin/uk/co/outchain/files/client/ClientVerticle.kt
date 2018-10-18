package uk.co.outchain.files.client

import io.vertx.reactivex.core.AbstractVerticle
import org.slf4j.LoggerFactory

class ClientVerticle : AbstractVerticle() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ClientVerticle::class.java)
    }
}
