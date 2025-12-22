package com.robot_controller.joystick

import org.json.JSONArray

data class JoystickCommandModel(
    val joystickType: JoystickType,
    val joystickCommand: JoystickCommand,
)
