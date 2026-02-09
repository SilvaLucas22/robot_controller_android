package com.robot_controller.utils.extensions

import com.robot_controller.mainView.joystick.JoystickCommandModel
import com.robot_controller.mainView.joystick.JoystickType
import com.robot_controller.data.RobotCommand
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

    val speed =
        if (isJoystickTypeRobot) {
            this.movementSpeed
        } else {
            null
        }

    return RobotCommand(
        module = module.value,
        action = action.value,
        direction = direction,
        speed = speed,
    )
}
