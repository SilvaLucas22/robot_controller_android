package com.robot_controller.data

import android.util.Log
import com.google.gson.Gson
import com.robot_controller.data.RobotCommand
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.OutputStream
import java.net.Socket

object RobotSocketManager {
    private var socket: Socket? = null
    private val gson = Gson()
    private var outputStream: OutputStream? = null

    fun connect(ipOrDomain: String, tcpPortCommands: Int): Completable {
        return Completable.fromAction {
            closeConnection()

            socket = Socket(ipOrDomain, tcpPortCommands)
            outputStream = socket?.getOutputStream()
        }.subscribeOn(Schedulers.io())
    }

    fun disconnect(): Completable {
        return Completable.fromAction {
            closeConnection()
        }.subscribeOn(Schedulers.io())
    }

    private fun closeConnection() {
        try {
            outputStream?.close()
            socket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            socket = null
            outputStream = null
        }
    }

    fun isConnected(): Boolean = socket?.isConnected == true && socket?.isClosed == false

    fun sendCommand(command: RobotCommand): Completable {
        return Completable.fromAction {
            val json = gson.toJson(command) + "\n"
            Log.e("LOG TEST", "RobotController -> sendCommand = $json")
            outputStream?.write(json.toByteArray())
            outputStream?.flush()
        }
    }

    fun sendAndReceive(command: RobotCommand): Single<String> {
        return Single.create { emitter ->
            try {
                val json = gson.toJson(command) + "\n"
                val outputStream = socket?.getOutputStream()
                val inputStream = socket?.getInputStream()

                if (outputStream == null || inputStream == null) {
                    emitter.onError(Exception("Socket not found"))
                    return@create
                }

                Log.e("LOG TEST", "RobotController -> send command = $json")
                outputStream.write(json.toByteArray())
                outputStream.flush()

                val reader = inputStream.bufferedReader()
                val response = reader.readLine()

                if (response != null) {
                    Log.e("LOG TEST", "RobotController -> received response = $response")
                    emitter.onSuccess(response)
                } else {
                    emitter.onError(Exception("response is null"))
                }
            } catch (e: Exception) {
                if (!emitter.isDisposed) emitter.onError(e)
            }
        }
    }
}
