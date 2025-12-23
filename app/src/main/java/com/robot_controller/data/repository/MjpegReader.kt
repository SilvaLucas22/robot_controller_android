package com.robot_controller.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.InputStream

class MjpegReader(inputStream: InputStream) {
    private val mIn = DataInputStream(inputStream)

    private val SOI_MARKER = 0xFFD8 // Start Of Image
    private val EOI_MARKER = 0xFFD9 // End Of Image

    fun readNextFrame(): Bitmap? {
        try {
            skipToMarker(SOI_MARKER)
            val frameData = readUntilMarker(EOI_MARKER)
            return if (frameData.isNotEmpty()) {
                BitmapFactory.decodeByteArray(frameData, 0, frameData.size)
            } else null
        } catch (e: Exception) {
            return null
        }
    }

    private fun skipToMarker(marker: Int) {
        var prev = 0
        while (true) {
            val current = mIn.readUnsignedByte()
            if ((prev shl 8 or current) == marker) break
            prev = current
        }
    }

    private fun readUntilMarker(marker: Int): ByteArray {
        val out = ByteArrayOutputStream()
        out.write(0xFF)
        out.write(0xD8)

        var prev = 0
        while (true) {
            val current = mIn.readUnsignedByte()
            out.write(current)
            if ((prev shl 8 or current) == marker) break
            prev = current
        }
        return out.toByteArray()
    }
}
