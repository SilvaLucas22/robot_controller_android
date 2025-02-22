package com.robot_controller.joystick

data class JoystickCommandModel(
    val joystickType: JoystickType,
    val joystickCommand: JoystickCommand,
    var speed: Int = 0
)