package com.robot_controller

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.robot_controller.data.local.PreferencesManager
import com.robot_controller.data.repository.RobotRepository
import com.robot_controller.joystick.JoystickCommandModel
import com.robot_controller.utils.enums.ErrorEnum
import com.robot_controller.utils.enums.RobotModule
import com.robot_controller.utils.extensions.isValidIpOrDomain
import com.robot_controller.utils.extensions.isValidPort
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers

class MainViewModel(private val preferencesManager: PreferencesManager): ViewModel() {

    private val _onErrorLiveData = MutableLiveData<ErrorEnum>()
    val onErrorLiveData: LiveData<ErrorEnum> = _onErrorLiveData

    private val robotRepository = RobotRepository()
    private val disposables = CompositeDisposable()

    fun saveNetworkParams(ipAddressOrDomain: String, tcpPort: String) {
        preferencesManager.ipAddressOrDomain = ipAddressOrDomain
        preferencesManager.tcpPort = tcpPort
        Log.e("LOG TEST", "Network Params -> IP = $ipAddressOrDomain, TCP Port = $tcpPort")

        connectSocket(ipAddressOrDomain, tcpPort)
    }

    fun getNetworkParams(): Pair<String, String>? {
        val ipAddress = preferencesManager.ipAddressOrDomain ?: return null
        val tcpPort = preferencesManager.tcpPort ?: return null

        if (!ipAddress.isValidIpOrDomain() || !tcpPort.isValidPort()) return null
        return Pair(ipAddress, tcpPort)
    }

    private fun connectSocket(ipAddressOrDomain: String, tcpPort: String) {
        robotRepository
            .connect(ipAddressOrDomain, tcpPort.toInt())
            .subscribe({
                Log.e("LOG TEST", "Connected successfully to robot")

            }, {
                Log.e("LOG TEST", "Fail to connect to robot")
                _onErrorLiveData.value = ErrorEnum.FAIL_TO_CONNECT
            })
            .addTo(disposables)
    }

    fun sendJoystickCommand(joystickCommandModel: JoystickCommandModel) {
        if (!robotRepository.isConnected()) {
            Log.e("LOG TEST", "Invalid Network Params")
            _onErrorLiveData.value = ErrorEnum.NETWORK_PARAMS
            return
        }

        Log.e("LOG TEST", "Joystick -> " +
                "Type = ${joystickCommandModel.joystickType}, " +
                "Command = ${joystickCommandModel.joystickCommand}"
//                "Speed = ${joystickCommandModel.speed}"
        )

        robotRepository
            .moveRobotOrCamera(joystickCommandModel)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e("LOG TEST", "Joystick command sent")
            }, {
                Log.e("LOG TEST", "Joystick command error")
                _onErrorLiveData.value = ErrorEnum.ON_SEND_COMMAND
            })
            .addTo(disposables)
    }

    fun sendStopCommand(module: RobotModule) {
        if (!robotRepository.isConnected()) {
            Log.e("LOG TEST", "Invalid Network Params")
            _onErrorLiveData.value = ErrorEnum.NETWORK_PARAMS
            return
        }

        robotRepository
            .stopRobotOrCamera(module)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e("LOG TEST", "Joystick command sent")
            }, {
                Log.e("LOG TEST", "Joystick command error")
                _onErrorLiveData.value = ErrorEnum.ON_SEND_COMMAND
            })
            .addTo(disposables)
    }

    override fun onCleared() {
        robotRepository
            .disconnect()
            .subscribe()
            .addTo(disposables)
        disposables.clear()
        super.onCleared()
    }
}
