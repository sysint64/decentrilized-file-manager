package uk.co.outchain.files

import io.vertx.core.Future
import uk.co.outchain.files.proto.ConnectReply
import uk.co.outchain.files.proto.ConnectRequest
import uk.co.outchain.files.proto.NodeGrpc

class NodeService : NodeGrpc.NodeVertxImplBase() {
    override fun connect(request: ConnectRequest, response: Future<ConnectReply>) {
        val reply = ConnectReply
            .newBuilder()
            .setSuccess(true)
            .build()

        response.complete(reply)
    }
}
