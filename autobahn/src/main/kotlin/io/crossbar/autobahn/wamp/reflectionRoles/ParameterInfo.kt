package io.crossbar.autobahn.wamp.reflectionRoles

class ParameterInfo(
    private val position: Int,
    private val name: String,
    private val type: Class<*>
) {
    fun getPosition(): Int = position
    fun getName(): String = name
    fun getType(): Class<*> = type
}
