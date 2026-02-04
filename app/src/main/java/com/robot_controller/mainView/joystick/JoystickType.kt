package com.robot_controller.mainView.joystick

import com.robot_controller.R

enum class JoystickType {
    ROBOT,
    CAMERA;

    val icon: Int
        get() = when (this) {
            ROBOT -> R.drawable.ic_car
            CAMERA -> R.drawable.ic_camera
        }
}