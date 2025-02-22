package com.robot_controller

import android.util.Log
import androidx.lifecycle.ViewModel
import com.robot_controller.joystick.JoystickCommandModel

class MainViewModel: ViewModel() {

    fun sendJoystickCommand(joystickCommandModel: JoystickCommandModel) {
        Log.e("LOG TEST", "Joystick -> " +
                "Type = ${joystickCommandModel.joystickType}, " +
                "Command = ${joystickCommandModel.joystickCommand}, " +
                "Speed = ${joystickCommandModel.speed}")


    }



}