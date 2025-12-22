package com.robot_controller.data.repository

import com.robot_controller.data.RobotSocketManager
import com.robot_controller.joystick.JoystickCommandModel
import com.robot_controller.model.RobotCommand
import com.robot_controller.utils.enums.RobotAction
import com.robot_controller.utils.enums.RobotModule
import com.robot_controller.utils.extensions.toRobotCommand
import io.reactivex.rxjava3.core.Completable

class RobotRepository {
    fun connect(ip: String, port: Int) = RobotSocketManager.connect(ip, port)

    fun disconnect() = RobotSocketManager.disconnect()

    fun isConnected() = RobotSocketManager.isConnected()

    fun moveRobotOrCamera(joystickCommandModel: JoystickCommandModel): Completable =
        RobotSocketManager.sendCommand(joystickCommandModel.toRobotCommand())

    fun stopRobotOrCamera(module: RobotModule): Completable {
        val cmd = RobotCommand(
            module = module.value,
            action = RobotAction.STOP.value,
        )
        return RobotSocketManager.sendCommand(cmd)
    }
}
