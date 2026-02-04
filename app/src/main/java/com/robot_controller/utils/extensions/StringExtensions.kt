package com.robot_controller.utils.extensions

object NetworkValidators {
    val DOMAIN_REGEX = Regex("^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$")
    val IP_REGEX = Regex("^(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3}$")
    val PORT_REGEX = Regex("""^([1-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$""")
}

fun String.isValidDomain() = NetworkValidators.DOMAIN_REGEX.matches(this)
fun String.isValidIp() = NetworkValidators.IP_REGEX.matches(this)
fun String.isValidIpOrDomain() = isValidIp() || isValidDomain()
fun String.isValidPort() = NetworkValidators.PORT_REGEX.matches(this)
fun String.isValidPanOrTilt() = this.all { it.isDigit() }
