package com.robot_controller.joystick

enum class JoystickCommand {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    val moveRobot: Int
        get() = when (this) {
            UP -> 0
            DOWN -> 1
            LEFT -> 3
            RIGHT -> 2
        }

    val moveCamera: Int
        get() = when (this) {
            UP -> 0
            DOWN -> 1
            LEFT -> 2
            RIGHT -> 3
        }
}