package com.robot_controller.data

import android.util.Log
import com.google.gson.Gson
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.EOFException
import java.net.Socket

object RobotSocketManager {
    private var socket: Socket? = null
    private val gson = Gson()

    private var reader: BufferedReader? = null
    private var writer: BufferedWriter? = null

    private val ioLock = Any()

    fun connect(ipOrDomain: String, tcpPortCommands: Int): Completable {
        return Completable.fromAction {
            closeConnection()

            socket = Socket(ipOrDomain, tcpPortCommands).apply {
                tcpNoDelay = true
                soTimeout = 10_000
            }

            reader = socket?.getInputStream()?.bufferedReader(Charsets.UTF_8)
            writer = socket?.getOutputStream()?.bufferedWriter(Charsets.UTF_8)

            val connectionMsg = reader?.readLine() ?: "Falha ao ler msg de conexão"
            Log.e("LOG TEST", "Connection -> $connectionMsg")
        }
    }

    fun disconnect(): Completable {
        return Completable.fromAction {
            closeConnection()
        }
    }

    private fun closeConnection() {
        try {
            reader?.close()
            writer?.close()
            socket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            reader = null
            writer = null
            socket = null
        }
    }

    fun isConnected(): Boolean = socket?.isConnected == true && socket?.isClosed == false

    fun sendCommand(command: RobotCommand): Single<String> {
        return Single.fromCallable {
            val w = writer ?: throw IllegalStateException("Socket TCP sem conexão")
            val r = reader ?: throw IllegalStateException("Socket TCP sem conexão")

            val json = gson.toJson(command) + "\n"

            synchronized(ioLock) {
                Log.e("LOG TEST", "RobotController -> send command = $json")
                w.write(json)
                w.flush()

                val response = r.readLine() ?: throw EOFException("Conexão TCP perdida")
                Log.e("LOG TEST", "RobotController -> received response = $response")
                response
            }
        }
    }
}
