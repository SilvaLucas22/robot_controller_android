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

    //region NETWORK and CONNECTION

    fun saveNetworkParams(ipAddressOrDomain: String, tcpPortCommands: String, tcpPortVideo: String) {
        preferencesManager.ipAddressOrDomain = ipAddressOrDomain
        preferencesManager.tcpPortCommands = tcpPortCommands
        preferencesManager.tcpPortVideo = tcpPortVideo
        Log.e("LOG TEST", "Network Params -> IP = $ipAddressOrDomain, TCP Port Commands = $tcpPortCommands, TCP Port Video = $tcpPortVideo")

        connectSocket(ipAddressOrDomain, tcpPortCommands)
    }

    fun getNetworkParams(): Triple<String, String, String>? {
        val ipAddress = preferencesManager.ipAddressOrDomain ?: return null
        val tcpPortCommands = preferencesManager.tcpPortCommands ?: return null
        val tcpPortVideo = preferencesManager.tcpPortVideo ?: return null

        if (!ipAddress.isValidIpOrDomain() || !tcpPortCommands.isValidPort() || !tcpPortVideo.isValidPort()) return null
        return Triple(ipAddress, tcpPortCommands, tcpPortVideo)
    }

    private fun connectSocket(ipAddressOrDomain: String, tcpPortCommands: String) {
        robotRepository
            .connect(ipAddressOrDomain, tcpPortCommands.toInt())
            .subscribe({
                Log.e("LOG TEST", "Connected successfully to robot")

            }, {
                Log.e("LOG TEST", "Fail to connect to robot")
                _onErrorLiveData.value = ErrorEnum.FAIL_TO_CONNECT
            })
            .addTo(disposables)
    }

    //endregion

    //region TRACTION and CAMERA modules

    fun sendJoystickCommand(joystickCommandModel: JoystickCommandModel) {
        if (!robotRepository.isConnected()) {
            Log.e("LOG TEST", "Robot offline")
            _onErrorLiveData.value = ErrorEnum.ROBOT_OFFLINE
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
            Log.e("LOG TEST", "Robot offline")
            _onErrorLiveData.value = ErrorEnum.ROBOT_OFFLINE
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

    fun centralizeCamera() {
        if (!robotRepository.isConnected()) {
            Log.e("LOG TEST", "Robot offline")
            _onErrorLiveData.value = ErrorEnum.ROBOT_OFFLINE
            return
        }

        robotRepository
            .centralizeCamera()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e("LOG TEST", "Centralize Camera command sent")
            }, {
                Log.e("LOG TEST", "Centralize Camera command error")
                _onErrorLiveData.value = ErrorEnum.ON_SEND_COMMAND
            })
            .addTo(disposables)
    }

    fun goToPanTilt(pan: Int? = null, tilt: Int? = null) {
        if (!robotRepository.isConnected()) {
            Log.e("LOG TEST", "Robot offline")
            _onErrorLiveData.value = ErrorEnum.ROBOT_OFFLINE
            return
        }

        val panValue = pan?.coerceIn(0, 180)
        val tiltValue = tilt?.coerceIn(45, 135)

        robotRepository
            .goToPanTilt(pan = panValue, tilt = tiltValue)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e("LOG TEST", "Pan/Tilt command sent")
            }, {
                Log.e("LOG TEST", "Pan/Tilt command error")
                _onErrorLiveData.value = ErrorEnum.ON_SEND_COMMAND
            })
            .addTo(disposables)
    }

    //endregion

    //region STREAM module

    fun startVideoStreaming() {
        if (!robotRepository.isConnected()) {
            Log.e("LOG TEST", "Robot offline")
            _onErrorLiveData.value = ErrorEnum.ROBOT_OFFLINE
            return
        }

        robotRepository
            .startVideoStreaming()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e("LOG TEST", "Start video streaming command sent")
            }, {
                Log.e("LOG TEST", "Start video streaming command error")
                _onErrorLiveData.value = ErrorEnum.ON_SEND_COMMAND
            })
            .addTo(disposables)
    }

    fun stopVideoStreaming() {
        if (!robotRepository.isConnected()) {
            Log.e("LOG TEST", "Robot offline")
            _onErrorLiveData.value = ErrorEnum.ROBOT_OFFLINE
            return
        }

        robotRepository
            .stopVideoStreaming()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e("LOG TEST", "Stop video streaming command sent")
            }, {
                Log.e("LOG TEST", "Stop video streaming command error")
                _onErrorLiveData.value = ErrorEnum.ON_SEND_COMMAND
            })
            .addTo(disposables)
    }

    //endregion

    //region SYSTEM module

    fun sendStopAllCommand() {
        if (!robotRepository.isConnected()) {
            Log.e("LOG TEST", "Robot offline")
            _onErrorLiveData.value = ErrorEnum.ROBOT_OFFLINE
            return
        }

        robotRepository
            .stopAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e("LOG TEST", "Stop all command sent")
            }, {
                Log.e("LOG TEST", "Stop all command error")
                _onErrorLiveData.value = ErrorEnum.ON_SEND_COMMAND
            })
            .addTo(disposables)
    }

    //endregion

    override fun onCleared() {
        robotRepository
            .disconnect()
            .subscribe()
            .addTo(disposables)
        disposables.clear()
        super.onCleared()
    }
}
