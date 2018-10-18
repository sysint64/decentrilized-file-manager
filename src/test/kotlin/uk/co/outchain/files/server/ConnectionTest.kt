package uk.co.outchain.files.server

import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import uk.co.outchain.files.VertxTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@ExtendWith(VertxExtension::class)
class ConnectionTest : VertxTest() {
    private val nodesVerticles = ArrayList<ServerVerticle>()

    @BeforeEach
    fun deploy_verticle(vertx: Vertx, testContext: VertxTestContext) {
        fun addNode(name: String): Single<ServerVerticle> {
            return deployNode(vertx, testContext, name)
                .doOnSuccess { nodesVerticles.add(it) }
        }

        Single.merge(
            listOf(
                addNode("node1"),
                addNode("node2"),
                addNode("node3")
            )
        ).subscribeBy(
            onComplete = {
                testContext.junitVertxCatch {
                    assertEquals(3, nodesVerticles.size)
                    testContext.completeNow()
                }
            },
            onError = testContext::failNow
        )
    }

    @Test
    fun `ensure something`(vertx: Vertx, testContext: VertxTestContext) {
        testContext.completeNow()
    }
}
