package com.robot_controller.utils

fun String.isValidPrivateIp() = matches(Regex("""^(10\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})|172\.(1[6-9]|2\d|3[0-1])\.(\d{1,3})\.(\d{1,3})|192\.168\.(\d{1,3})\.(\d{1,3}))$"""))
fun String.isValidUdpPort() = matches(Regex("""^([1-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$"""))
