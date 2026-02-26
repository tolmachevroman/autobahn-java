package io.crossbar.autobahn.wamp.requests

import io.crossbar.autobahn.wamp.types.Publication
import java8.util.concurrent.CompletableFuture

class PublishRequest(
    request: Long,
    @JvmField val onReply: CompletableFuture<Publication>
) : Request(request)
