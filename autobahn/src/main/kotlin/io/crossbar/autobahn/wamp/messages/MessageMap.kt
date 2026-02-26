package io.crossbar.autobahn.wamp.messages

import io.crossbar.autobahn.wamp.interfaces.IMessage

object MessageMap {
    @JvmField
    val MESSAGE_TYPE_MAP: MutableMap<Int, Class<out IMessage>> = HashMap()

    init {
        // Note: Hello is still in Java due to raw type usage
        MESSAGE_TYPE_MAP[Hello.MESSAGE_TYPE] = Hello::class.java
        MESSAGE_TYPE_MAP[Challenge.MESSAGE_TYPE] = Challenge::class.java
        MESSAGE_TYPE_MAP[Welcome.MESSAGE_TYPE] = Welcome::class.java
        MESSAGE_TYPE_MAP[Abort.MESSAGE_TYPE] = Abort::class.java
        MESSAGE_TYPE_MAP[Goodbye.MESSAGE_TYPE] = Goodbye::class.java
        MESSAGE_TYPE_MAP[Error.MESSAGE_TYPE] = Error::class.java
        MESSAGE_TYPE_MAP[Publish.MESSAGE_TYPE] = Publish::class.java
        MESSAGE_TYPE_MAP[Published.MESSAGE_TYPE] = Published::class.java
        MESSAGE_TYPE_MAP[Subscribe.MESSAGE_TYPE] = Subscribe::class.java
        MESSAGE_TYPE_MAP[Subscribed.MESSAGE_TYPE] = Subscribed::class.java
        MESSAGE_TYPE_MAP[Unsubscribe.MESSAGE_TYPE] = Unsubscribe::class.java
        MESSAGE_TYPE_MAP[Unsubscribed.MESSAGE_TYPE] = Unsubscribed::class.java
        MESSAGE_TYPE_MAP[Event.MESSAGE_TYPE] = Event::class.java
        MESSAGE_TYPE_MAP[Call.MESSAGE_TYPE] = Call::class.java
        MESSAGE_TYPE_MAP[Result.MESSAGE_TYPE] = Result::class.java
        MESSAGE_TYPE_MAP[Register.MESSAGE_TYPE] = Register::class.java
        MESSAGE_TYPE_MAP[Registered.MESSAGE_TYPE] = Registered::class.java
        MESSAGE_TYPE_MAP[Unregister.MESSAGE_TYPE] = Unregister::class.java
        MESSAGE_TYPE_MAP[Unregistered.MESSAGE_TYPE] = Unregistered::class.java
        MESSAGE_TYPE_MAP[Invocation.MESSAGE_TYPE] = Invocation::class.java
        MESSAGE_TYPE_MAP[Yield.MESSAGE_TYPE] = Yield::class.java
        MESSAGE_TYPE_MAP[Interrupt.MESSAGE_TYPE] = Interrupt::class.java
    }
}
