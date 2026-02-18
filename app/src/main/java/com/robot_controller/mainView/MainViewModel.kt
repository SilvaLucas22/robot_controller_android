package com.robot_controller.mainView

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.robot_controller.autonomy.AutonomyHelper
import com.robot_controller.data.local.PreferencesManager
import com.robot_controller.data.reponses.AntennaData
import com.robot_controller.data.repository.RobotRepository
import com.robot_controller.mainView.joystick.JoystickCommandModel
import com.robot_controller.utils.enums.Angle
import com.robot_controller.utils.enums.ErrorEnum
import com.robot_controller.utils.enums.RobotModule
import com.robot_controller.utils.extensions.isValidIpOrDomain
import com.robot_controller.utils.extensions.isValidPort
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MainViewModel(private val preferencesManager: PreferencesManager): ViewModel() {
    private val _onErrorLiveData = MutableLiveData<ErrorEnum>()
    private val _videoFrameLiveData = MutableLiveData<Bitmap>()
    private val _videoPlayingLiveData = MutableLiveData<Boolean>()
    private val _compassValueLiveData = MutableLiveData<Double>()
    private val _antennaInfoLiveData = MutableLiveData<AntennaData>()
    private val _autonomyFeedbackLiveData = MutableLiveData<String>()

    val onErrorLiveData: LiveData<ErrorEnum> = _onErrorLiveData
    val videoFrameLiveData: LiveData<Bitmap> = _videoFrameLiveData
    val videoPlayingLiveData: LiveData<Boolean> = _videoPlayingLiveData
    val compassValueLiveData: LiveData<Double> = _compassValueLiveData
    val antennaInfoLiveData: LiveData<AntennaData> = _antennaInfoLiveData
    val autonomyFeedbackLiveData: LiveData<String> = _autonomyFeedbackLiveData

    private val robotRepository = RobotRepository()
    private val disposables = CompositeDisposable()

    //region NETWORK and CONNECTION

    fun saveNetworkParams(ipAddressOrDomain: String, tcpPortCommands: String, httpPortVideo: String) {
        preferencesManager.ipAddressOrDomain = ipAddressOrDomain
        preferencesManager.tcpPortCommands = tcpPortCommands
        preferencesManager.httpPortVideo = httpPortVideo
        Log.e("LOG TEST", "Network Params -> IP = $ipAddressOrDomain, TCP Port Commands = $tcpPortCommands, HTTP Port Video = $httpPortVideo")

        connectSocket(ipAddressOrDomain, tcpPortCommands)
    }

    fun getNetworkParams(): Triple<String, String, String>? {
        val ipAddress = preferencesManager.ipAddressOrDomain ?: return null
        val tcpPortCommands = preferencesManager.tcpPortCommands ?: return null
        val httpPortVideo = preferencesManager.httpPortVideo ?: return null

        if (!ipAddress.isValidIpOrDomain() || !tcpPortCommands.isValidPort() || !httpPortVideo.isValidPort()) return null
        return Triple(ipAddress, tcpPortCommands, httpPortVideo)
    }

    private fun connectSocket(ipAddressOrDomain: String, tcpPortCommands: String) {
        robotRepository
            .connect(ipAddressOrDomain, tcpPortCommands.toInt())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
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

//        Log.e("LOG TEST", "Joystick -> " +
//                "Type = ${joystickCommandModel.joystickType}, " +
//                "Command = ${joystickCommandModel.joystickCommand}" +
//                "Speed = ${joystickCommandModel.movementSpeed}"
//        )

        robotRepository
            .moveRobotOrCamera(joystickCommandModel)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
//                Log.e("LOG TEST", "Joystick command sent")
            }, {
//                Log.e("LOG TEST", "Joystick command error")
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
//                Log.e("LOG TEST", "Joystick command sent")
            }, {
//                Log.e("LOG TEST", "Joystick command error")
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
//                Log.e("LOG TEST", "Centralize Camera command sent")
            }, {
//                Log.e("LOG TEST", "Centralize Camera command error")
                _onErrorLiveData.value = ErrorEnum.ON_SEND_COMMAND
            })
            .addTo(disposables)
    }

    fun goToCameraPanTilt(pan: Int? = null, tilt: Int? = null) {
        if (!robotRepository.isConnected()) {
            Log.e("LOG TEST", "Robot offline")
            _onErrorLiveData.value = ErrorEnum.ROBOT_OFFLINE
            return
        }

        val panValue = pan?.coerceIn(0, 180)
        val tiltValue = tilt?.coerceIn(45, 135)

        robotRepository
            .goToCameraPanTilt(pan = panValue, tilt = tiltValue)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
//                Log.e("LOG TEST", "Camera Pan/Tilt command sent")
            }, {
//                Log.e("LOG TEST", "Camera Pan/Tilt command error")
                _onErrorLiveData.value = ErrorEnum.ON_SEND_COMMAND
            })
            .addTo(disposables)
    }

    //endregion

    //region STREAM module

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
                _videoPlayingLiveData.value = false
//                Log.e("LOG TEST", "Stop video streaming command sent")
            }, {
//                Log.e("LOG TEST", "Stop video streaming command error")
                _onErrorLiveData.value = ErrorEnum.ON_SEND_COMMAND
            })
            .addTo(disposables)
    }

    fun playVideo() {
        if (!robotRepository.isConnected()) {
            Log.e("LOG TEST", "Robot offline")
            _onErrorLiveData.value = ErrorEnum.ROBOT_OFFLINE
            return
        }

        val (currentIpOrDomain, _, currentHttpPortVideo) = getNetworkParams() ?: return
        val url = "http://$currentIpOrDomain:$currentHttpPortVideo/?action=stream"

        _videoPlayingLiveData.value = true

        robotRepository
            .startVideoStreaming()
            .delay(2000, TimeUnit.MILLISECONDS)
            .andThen(robotRepository.getVideoStream(url))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { frameAsBitmap ->
                    _videoFrameLiveData.value = frameAsBitmap
                },
                onError = {
//                    Log.e("LOG TEST", "Error on receiving video stream")
                    _onErrorLiveData.value = ErrorEnum.VIDEO_STREAMING_ERROR
                    _videoPlayingLiveData.value = false
                }
            )
            .addTo(disposables)
    }

    fun getStatusVideoStreaming() {
        if (!robotRepository.isConnected()) {
            Log.e("LOG TEST", "Robot offline")
            _onErrorLiveData.value = ErrorEnum.ROBOT_OFFLINE
            return
        }

        if (_videoPlayingLiveData.value != true) {
            Log.e("LOG TEST", "Não está reproduzindo vídeo do robô agora.")
            _onErrorLiveData.value = ErrorEnum.ON_SEND_COMMAND
            return
        }

        robotRepository
            .getStatusVideoStreaming()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            }, {
//                Log.e("LOG TEST", "Status video streaming command error")
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
                _videoPlayingLiveData.value = false
//                Log.e("LOG TEST", "Stop all command sent")
            }, {
//                Log.e("LOG TEST", "Stop all command error")
                _onErrorLiveData.value = ErrorEnum.ON_SEND_COMMAND
            })
            .addTo(disposables)
    }

    fun getTelemetry() {
        if (!robotRepository.isConnected()) {
            Log.e("LOG TEST", "Robot offline")
            _onErrorLiveData.value = ErrorEnum.ROBOT_OFFLINE
            return
        }

        robotRepository
            .getTelemetry()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ telemetryResponse ->
//                Log.e("LOG TEST", "Get telemetry value = $telemetryResponse")
            }, {
//                Log.e("LOG TEST", "Get telemetry command error")
                _onErrorLiveData.value = ErrorEnum.ON_SEND_COMMAND
            })
            .addTo(disposables)
    }

    //endregion

    //region COMPASS module

    fun getCompassValue() {
        if (!robotRepository.isConnected()) {
            Log.e("LOG TEST", "Robot offline")
            _onErrorLiveData.value = ErrorEnum.ROBOT_OFFLINE
            return
        }

        robotRepository
            .readCompass()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ compassResponse ->
//                Log.e("LOG TEST", "Read compass value = $compassResponse")
                if (compassResponse.ok == 1) {
                    _compassValueLiveData.value = compassResponse.degrees
                }
            }, {
//                Log.e("LOG TEST", "Read compass command error")
                _onErrorLiveData.value = ErrorEnum.ON_SEND_COMMAND
            })
            .addTo(disposables)
    }

    //endregion

    //region ANTENNA module

    fun goToAntennaPanTilt(pan: Int? = null, tilt: Int? = null) {
        if (!robotRepository.isConnected()) {
            Log.e("LOG TEST", "Robot offline")
            _onErrorLiveData.value = ErrorEnum.ROBOT_OFFLINE
            return
        }

        val panValue = pan?.coerceIn(0, 180)
        val tiltValue = tilt?.coerceIn(0, 180)

        robotRepository
            .goToAntennaPanTilt(pan = panValue, tilt = tiltValue)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
//                Log.e("LOG TEST", "Antenna Pan/Tilt command sent")
            }, {
//                Log.e("LOG TEST", "Antenna Pan/Tilt command error")
                _onErrorLiveData.value = ErrorEnum.ON_SEND_COMMAND
            })
            .addTo(disposables)
    }

    fun getAntennaLqiValue() {
        if (!robotRepository.isConnected()) {
            Log.e("LOG TEST", "Robot offline")
            _onErrorLiveData.value = ErrorEnum.ROBOT_OFFLINE
            return
        }

        robotRepository
            .readAntennaLqi()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ antennaLqiResponse ->
//                Log.e("LOG TEST", "Read antenna lqi value = $antennaLqiResponse")
            }, {
//                Log.e("LOG TEST", "Read antenna lqi command error")
                _onErrorLiveData.value = ErrorEnum.ON_SEND_COMMAND
            })
            .addTo(disposables)
    }

    fun getAllAntennaInfo() {
        if (!robotRepository.isConnected()) {
            Log.e("LOG TEST", "Robot offline")
            _onErrorLiveData.value = ErrorEnum.ROBOT_OFFLINE
            return
        }

        robotRepository
            .getAllAntennaInfo()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
//                Log.e("LOG TEST", "Read antenna all info value = $antennaInfoResponse")
//                if (antennaInfoResponse.ok == 1) {
//                    _antennaInfoLiveData.value = antennaInfoResponse.antennaData
//                }
            }, {
//                Log.e("LOG TEST", "Read antenna all info command error")
                _onErrorLiveData.value = ErrorEnum.ON_SEND_COMMAND
            })
            .addTo(disposables)
    }

    //endregion

    //region Autonomy region

    fun mapEnvironmentFromGallery(
        envName: String,
        north: Bitmap,
        east: Bitmap,
        south: Bitmap,
        west: Bitmap
    ) {
        val useCase = AutonomyHelper.mapEnvironmentUseCase

        val items = listOf(
            Angle.NORTH to north,
            Angle.EAST to east,
            Angle.SOUTH to south,
            Angle.WEST to west
        )

        Completable
            .concat(items.map { (angle, bmp) -> useCase.execute(envName, angle, bmp).ignoreElement() })
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _autonomyFeedbackLiveData.value = "Ambiente \"$envName\" mapeado!"
                listOf(north, east, south, west).forEach { it.recycle() }
            }, { err ->
                _autonomyFeedbackLiveData.value = "Erro ao mapear: ${err.message}"
                listOf(north, east, south, west).forEach { it.recycle() }
            })
            .addTo(disposables)
    }

    fun locateFromGallery(north: Bitmap, east: Bitmap, south: Bitmap, west: Bitmap) {
        AutonomyHelper
            .locateEnvironmentUseCase
            .execute(north, east, south, west)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                val environmentFeedback = "Provável ambiente: ${result.environmentName}"
                Log.e("LOG TEST", environmentFeedback)
                _autonomyFeedbackLiveData.value = environmentFeedback
            }, { err ->
                _autonomyFeedbackLiveData.value = "Erro ao localizar: ${err.message}"
            })
            .addTo(disposables)
    }


    //endregion

    override fun onCleared() {
        robotRepository
            .disconnect()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
            .addTo(disposables)
        disposables.clear()
        super.onCleared()
    }
}
