package uk.co.outchain.files.server

import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import uk.co.outchain.files.VertxTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@ExtendWith(VertxExtension::class)
class ConnectionTest : VertxTest() {
    private val nodesVerticles = ArrayList<NodeServerVerticle>()

    @BeforeEach
    fun deployVerticle(vertx: Vertx, testContext: VertxTestContext) {
        fun addNode(name: String): Single<NodeServerVerticle> {
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

    @AfterEach
    fun tearDown(vertx: Vertx, testContext: VertxTestContext) {
        val releaseLatch = CountDownLatch(nodesVerticles.size)

        for (verticle in nodesVerticles) {
            verticle.rpcServer.shutdown { releaseLatch.countDown() }
        }

        releaseLatch.await(10, TimeUnit.SECONDS)
        nodesVerticles.clear()
        testContext.completeNow()
    }

    @Test
    fun `successfully connect test`(vertx: Vertx, testContext: VertxTestContext) {
        val node1Verticle = nodesVerticles[0]
        val node2Verticle = nodesVerticles[1]

        node1Verticle.node.connectByAddress(node2Verticle.node.address)
            .subscribeBy(
                onComplete = testContext::completeNow,
                onError = testContext::failNow
            )
    }

    @Test
    fun `failed connect due to incorrect address test`(vertx: Vertx, testContext: VertxTestContext) {
        val node1Verticle = nodesVerticles[0]

        node1Verticle.node.connectByAddress("127.0.0.1:55555")
            .subscribeBy(
                onComplete = {
                    testContext.failNow(AssertionError("Expected error but complete got"))
                },
                onError = {
                    testContext.completeNow()
                }
            )
    }

    @Test
    @Disabled
    fun `getFiles test`(vertx: Vertx, testContext: VertxTestContext) {
        testContext.completeNow()
    }
}
