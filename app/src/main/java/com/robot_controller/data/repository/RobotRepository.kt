package com.robot_controller.data.repository

import com.robot_controller.data.RobotController
import com.robot_controller.joystick.JoystickCommandModel
import io.reactivex.rxjava3.core.Completable

class RobotRepository {

    fun moveRobot(
        joystickCommandModel: JoystickCommandModel,
        ipAddress: String,
        udpPort: Int
    ): Completable {
        return RobotController.sendCommand(joystickCommandModel.toJson(), ipAddress, udpPort)
    }

}