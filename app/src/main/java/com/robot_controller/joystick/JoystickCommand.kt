package com.robot_controller.joystick

enum class JoystickCommand(val robotDirection: String, val cameraDirection: String) {
    UP("w", "t+"),
    DOWN("s", "t-"),
    LEFT("a", "p-"),
    RIGHT("d", "p+");
}
