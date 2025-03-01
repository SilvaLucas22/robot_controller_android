package com.robot_controller.data

import android.util.Log
import io.reactivex.rxjava3.core.Completable
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

object RobotController {

    fun sendCommand(
        command: ByteArray,
        ipAddress: String,
        udpPort: Int
    ): Completable {
        return Completable.create { emitter ->
            try {
                val socket = DatagramSocket()
                val address = InetAddress.getByName(ipAddress)
                val packet = DatagramPacket(command, command.size, address, udpPort)

                Log.e("LOG TEST", "RobotController -> Try -> Command = ${command.toString(Charsets.UTF_8)}")

                socket.send(packet)
                socket.close()
                emitter.onComplete()
            } catch (e: Exception) {
                Log.e("LOG TEST", "RobotController -> Catch -> Error = ${e.message}")
                emitter.onError(e)
            }
        }
    }

}