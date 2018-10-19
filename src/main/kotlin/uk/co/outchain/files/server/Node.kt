package uk.co.outchain.files.server

import io.reactivex.Completable
import io.vertx.grpc.VertxChannelBuilder
import io.vertx.reactivex.core.Vertx
import uk.co.outchain.files.proto.ConnectRequest
import uk.co.outchain.files.proto.NodeGrpc

data class NodeAddress(
    val host: String,
    val port: Int
) {
    constructor(address: String) : this(
        address.split(":")[0],
        address.split(":")[1].toInt()
    )

    override fun toString(): String =
        "$host:$port"
}

class Node(val vertx: Vertx, val name: String, val address: NodeAddress) {
    fun connectByAddress(address: NodeAddress): Completable =
        connectByAddress(address.toString())

    fun connectByAddress(address: String): Completable {
        val nodeAddress = NodeAddress(address)
        val channel = VertxChannelBuilder
            .forAddress(vertx.delegate, nodeAddress.host, nodeAddress.port)
            .usePlaintext(true)
            .build()

        val stub = NodeGrpc.newVertxStub(channel)
        val request = ConnectRequest.newBuilder().build()

        return Completable.create { emitter ->
            stub.connect(request) {
                if (it.succeeded()) {
                    emitter.onComplete()
                } else {
                    emitter.onError(it.cause())
                }
            }
        }
    }

    fun connectByName(name: String): Completable {
        TODO()
    }
}
