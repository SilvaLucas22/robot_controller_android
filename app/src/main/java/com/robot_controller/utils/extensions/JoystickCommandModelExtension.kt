package com.robot_controller.utils.extensions

import com.robot_controller.joystick.JoystickCommandModel
import com.robot_controller.joystick.JoystickType
import com.robot_controller.model.RobotCommand
import com.robot_controller.utils.enums.RobotAction
import com.robot_controller.utils.enums.RobotModule

fun JoystickCommandModel.toRobotCommand(): RobotCommand {
    val isJoystickTypeRobot = this.joystickType == JoystickType.ROBOT

    val (module, action) =
        if (isJoystickTypeRobot) {
            Pair(RobotModule.TRACTION, RobotAction.GO)
        } else {
            Pair(RobotModule.CAMERA, RobotAction.CONTINUOUS)
        }

    val direction =
        if (isJoystickTypeRobot) {
            this.joystickCommand.robotDirection
        } else {
            this.joystickCommand.cameraDirection
        }

    return RobotCommand(module.value, action.value, direction)
}
