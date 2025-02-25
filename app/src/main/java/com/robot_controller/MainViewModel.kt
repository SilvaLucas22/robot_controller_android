package com.robot_controller

import android.util.Log
import androidx.lifecycle.ViewModel
import com.robot_controller.data.local.PreferencesManager
import com.robot_controller.data.repository.RobotRepository
import com.robot_controller.joystick.JoystickCommandModel
import com.robot_controller.utils.isValidPrivateIp
import com.robot_controller.utils.isValidUdpPort
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainViewModel(private val preferencesManager: PreferencesManager): ViewModel() {

    private val robotRepository = RobotRepository()
    private var joystickCommandDisposable: Disposable? = null

    fun saveNetworkParams(ipAddress: String, udpPort: String) {
        preferencesManager.ipAddress = ipAddress
        preferencesManager.udpPort = udpPort
        Log.e("LOG TEST", "Network Params -> IP = $ipAddress, UDP Port = $udpPort")
    }

    fun getNetworkParams(): Pair<String, String>? {
        val ipAddress = preferencesManager.ipAddress ?: return null
        val udpPort = preferencesManager.udpPort ?: return null

        if (!ipAddress.isValidPrivateIp() || !udpPort.isValidUdpPort()) return null
        return Pair(ipAddress, udpPort)
    }

    private fun handleInvalidNetworkParams() {
        Log.e("LOG TEST", "Invalid Network Params")



    }

    fun sendJoystickCommand(joystickCommandModel: JoystickCommandModel) {
        val (ipAddress, udpPort) = getNetworkParams() ?: run {
            handleInvalidNetworkParams()
            return
        }

        Log.e("LOG TEST", "Joystick -> " +
                "Type = ${joystickCommandModel.joystickType}, " +
                "Command = ${joystickCommandModel.joystickCommand}, " +
                "Speed = ${joystickCommandModel.speed}")

        joystickCommandDisposable?.dispose()
        joystickCommandDisposable = robotRepository.moveRobot(
            joystickCommandModel,
            ipAddress,
            udpPort.toInt()
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                joystickCommandDisposable?.dispose()

            }, {
                joystickCommandDisposable?.dispose()

            })
    }

}