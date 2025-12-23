package com.robot_controller.data.repository

import android.graphics.Bitmap
import com.robot_controller.data.RobotSocketManager
import com.robot_controller.joystick.JoystickCommandModel
import com.robot_controller.model.RobotCommand
import com.robot_controller.utils.enums.RobotAction
import com.robot_controller.utils.enums.RobotModule
import com.robot_controller.utils.extensions.toRobotCommand
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request

class RobotRepository {
    fun connect(ipOrDomain: String, tcpPortCommands: Int) = RobotSocketManager.connect(ipOrDomain, tcpPortCommands)

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

    fun centralizeCamera(): Completable {
        val cmd = RobotCommand(
            module = RobotModule.CAMERA.value,
            action = RobotAction.CENTER.value,
        )
        return RobotSocketManager.sendCommand(cmd)
    }

    fun goToPanTilt(pan: Int? = null, tilt: Int? = null): Completable {
        val cmd = RobotCommand(
            module = RobotModule.CAMERA.value,
            action = RobotAction.SET.value,
            pan = pan,
            tilt = tilt,
        )
        return RobotSocketManager.sendCommand(cmd)
    }

    fun startVideoStreaming(): Completable {
        val cmd = RobotCommand(
            module = RobotModule.STREAM.value,
            action = RobotAction.START.value,
        )
        return RobotSocketManager.sendCommand(cmd)
    }

    fun stopVideoStreaming(): Completable {
        val cmd = RobotCommand(
            module = RobotModule.STREAM.value,
            action = RobotAction.STOP.value,
        )
        return RobotSocketManager.sendCommand(cmd)
    }

    // TODO Dar um double check nesse método para ver como vem o status
    fun getStatusVideoStreaming(): Completable {
        val cmd = RobotCommand(
            module = RobotModule.STREAM.value,
            action = RobotAction.STATUS.value,
        )
        return RobotSocketManager.sendCommand(cmd)
    }

    fun getVideoStream(url: String): Observable<Bitmap> {
        return Observable.create { emitter ->
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()

            try {
                val response = client.newCall(request).execute()
                val inputStream = response.body?.byteStream()

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

    // TODO Dar um double check nesse método para ver como vem as informacoes
    fun getTelemetry(): Completable {
        val cmd = RobotCommand(
            module = RobotModule.SYSTEM.value,
            action = RobotAction.TELEMETRY.value,
        )
        return RobotSocketManager.sendCommand(cmd)
    }

    fun stopAll(): Completable {
        val cmd = RobotCommand(
            module = RobotModule.SYSTEM.value,
            action = RobotAction.STOP_ALL.value,
        )
        return RobotSocketManager.sendCommand(cmd)
    }
}
