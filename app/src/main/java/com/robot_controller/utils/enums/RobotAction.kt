package com.robot_controller.utils.enums

enum class RobotAction(val value: String) {
    GO("go"),
    STOP("stop"),
    CONTINUOUS("cont"),
    CENTER("center"),
    SET("set"),
    TELEMETRY("tele"),
    STOP_ALL("stop_all"),
    START("start"),
    STATUS("status"),
    READ("read"),
    LQI("lqi"),
    SCAN("scan"),
}
