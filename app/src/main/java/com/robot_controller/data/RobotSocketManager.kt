package com.robot_controller.data

import android.util.Log
import com.google.gson.Gson
import com.robot_controller.model.RobotCommand
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.OutputStream
import java.net.Socket

object RobotSocketManager {
    private var socket: Socket? = null
    private val gson = Gson()
    private var outputStream: OutputStream? = null

    fun connect(ipOrDomain: String, tcpPort: Int): Completable {
        return Completable.fromAction {
            closeConnection()

            socket = Socket(ipOrDomain, tcpPort)
            outputStream = socket?.getOutputStream()
        }.subscribeOn(Schedulers.io())
    }

    fun sendCommand(command: RobotCommand): Completable {
        return Completable.fromAction {
            val json = gson.toJson(command) + "\n"
            Log.e("LOG TEST", "RobotController -> sendCommand = $json")
            outputStream?.write(json.toByteArray())
            outputStream?.flush()
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
}
