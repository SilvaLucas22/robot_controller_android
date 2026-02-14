package com.robot_controller.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.google.gson.Gson
import com.robot_controller.data.RobotSocketManager
import com.robot_controller.data.reponses.CompassResponse
import com.robot_controller.data.reponses.TelemetryResponse
import com.robot_controller.mainView.joystick.JoystickCommandModel
import com.robot_controller.data.RobotCommand
import com.robot_controller.data.reponses.AntennaInfoResponse
import com.robot_controller.data.reponses.AntennaLqiResponse
import com.robot_controller.data.reponses.StatusVideoStreamingResponse
import com.robot_controller.utils.enums.RobotAction
import com.robot_controller.utils.enums.RobotModule
import com.robot_controller.utils.extensions.toRobotCommand
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import okhttp3.OkHttpClient
import okhttp3.Request

class RobotRepository {
    fun connect(ipOrDomain: String, tcpPortCommands: Int) = RobotSocketManager.connect(ipOrDomain, tcpPortCommands)

    fun disconnect() = RobotSocketManager.disconnect()

    fun isConnected() = RobotSocketManager.isConnected()

    fun moveRobotOrCamera(joystickCommandModel: JoystickCommandModel): Completable =
        RobotSocketManager
            .sendCommand(joystickCommandModel.toRobotCommand())
            .ignoreElement()

    fun stopRobotOrCamera(module: RobotModule): Completable {
        val cmd = RobotCommand(
            module = module.value,
            action = RobotAction.STOP.value,
        )
        return RobotSocketManager
            .sendCommand(cmd)
            .ignoreElement()
    }

    fun centralizeCamera(): Completable {
        val cmd = RobotCommand(
            module = RobotModule.CAMERA.value,
            action = RobotAction.CENTER.value,
        )
        return RobotSocketManager
            .sendCommand(cmd)
            .ignoreElement()
    }

    fun goToCameraPanTilt(pan: Int? = null, tilt: Int? = null): Completable {
        val cmd = RobotCommand(
            module = RobotModule.CAMERA.value,
            action = RobotAction.SET.value,
            pan = pan,
            tilt = tilt,
        )
        return RobotSocketManager
            .sendCommand(cmd)
            .ignoreElement()
    }

    fun startVideoStreaming(): Completable {
        val cmd = RobotCommand(
            module = RobotModule.STREAM.value,
            action = RobotAction.START.value,
        )
        return RobotSocketManager
            .sendCommand(cmd)
            .ignoreElement()
    }

    fun stopVideoStreaming(): Completable {
        val cmd = RobotCommand(
            module = RobotModule.STREAM.value,
            action = RobotAction.STOP.value,
        )
        return RobotSocketManager
            .sendCommand(cmd)
            .ignoreElement()
    }

    fun getStatusVideoStreaming(): Single<StatusVideoStreamingResponse> {
        val cmd = RobotCommand(
            module = RobotModule.STREAM.value,
            action = RobotAction.STATUS.value,
        )

        return RobotSocketManager
            .sendCommand(cmd)
            .map { response ->
                Gson().fromJson(response, StatusVideoStreamingResponse::class.java)
            }
    }

    fun getVideoStream(url: String): Observable<Bitmap> {
        return Observable.create { emitter ->
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()

            try {
                val response = client.newCall(request).execute()
                val inputStream = response.body?.byteStream()

                Log.e("LOG TEST", "Http connection -> $response")

                if (inputStream != null) {
                    val reader = MjpegReader(inputStream)
                    while (!emitter.isDisposed) {
                        val bitmap = reader.readNextFrame()
                        if (bitmap != null) {
                            emitter.onNext(bitmap)
                        }
                    }
                }
            } catch (e: Exception) {
                if (!emitter.isDisposed) emitter.onError(e)
            }
        }
    }

    fun getTelemetry(): Single<TelemetryResponse> {
        val cmd = RobotCommand(
            module = RobotModule.SYSTEM.value,
            action = RobotAction.TELEMETRY.value,
        )
        return RobotSocketManager
            .sendCommand(cmd)
            .map { response ->
                Gson().fromJson(response, TelemetryResponse::class.java)
            }
    }

    fun readCompass(): Single<CompassResponse> {
        val cmd = RobotCommand(
            module = RobotModule.COMPASS.value,
            action = RobotAction.READ.value,
        )

        return RobotSocketManager
            .sendCommand(cmd)
            .map { response ->
                Gson().fromJson(response, CompassResponse::class.java)
            }
    }

    fun stopAll(): Completable {
        val cmd = RobotCommand(
            module = RobotModule.SYSTEM.value,
            action = RobotAction.STOP_ALL.value,
        )
        return RobotSocketManager
            .sendCommand(cmd)
            .ignoreElement()
    }

    fun goToAntennaPanTilt(pan: Int? = null, tilt: Int? = null): Completable {
        val cmd = RobotCommand(
            module = RobotModule.ANTENNA.value,
            action = RobotAction.SET.value,
            pan = pan,
            tilt = tilt,
        )
        return RobotSocketManager
            .sendCommand(cmd)
            .ignoreElement()
    }

    fun readAntennaLqi(): Single<AntennaLqiResponse> {
        val cmd = RobotCommand(
            module = RobotModule.ANTENNA.value,
            action = RobotAction.LQI.value,
        )

        return RobotSocketManager
            .sendCommand(cmd)
            .map { response ->
                Gson().fromJson(response, AntennaLqiResponse::class.java)
            }
    }

    fun getAllAntennaInfo(): Completable {
        val cmd = RobotCommand(
            module = RobotModule.ANTENNA.value,
            action = RobotAction.SCAN.value,
        )

        return RobotSocketManager
            .sendCommand(cmd)
            .ignoreElement()
    }
}
