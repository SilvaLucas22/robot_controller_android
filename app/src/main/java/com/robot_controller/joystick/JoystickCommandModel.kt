package com.robot_controller.joystick

import org.json.JSONArray

data class JoystickCommandModel(
    val joystickType: JoystickType,
    val joystickCommand: JoystickCommand,
    var speed: Int = 0
) {

    fun getCommand(): ByteArray {
        return if (joystickType == JoystickType.ROBOT)
            JSONArray(listOf(joystickCommand.moveRobot, 0, speed)).toString().toByteArray()
        else
            JSONArray(listOf(0, joystickCommand.moveCamera, speed)).toString().toByteArray()
    }

}
