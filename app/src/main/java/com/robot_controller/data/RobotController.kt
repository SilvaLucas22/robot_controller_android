package com.robot_controller.data

import io.reactivex.rxjava3.core.Completable
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

object RobotController {

    fun sendCommand(
        commandJson: String,
        ipAddress: String,
        udpPort: Int
    ): Completable {
        return Completable.create { emitter ->
            try {
                val socket = DatagramSocket()
                val address = InetAddress.getByName(ipAddress)
                val commandAsByteArray = commandJson.toByteArray()
                val packet = DatagramPacket(commandAsByteArray, commandAsByteArray.size, address, udpPort)

                socket.send(packet)
                socket.close()

                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

}