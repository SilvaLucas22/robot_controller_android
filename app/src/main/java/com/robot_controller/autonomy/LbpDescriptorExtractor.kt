package com.robot_controller.autonomy

import android.graphics.Bitmap
import com.robot_controller.autonomy.interfaces.DescriptorExtractor
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class LbpHistDescriptorExtractor(
    private val resizeTo: Int = 256
) : DescriptorExtractor {

    override val name: String = "LBP-HIST(256)"
    override val featureSize: Int = 256

    override fun extract(bitmap: Bitmap): FloatArray {
        val rgba = Mat()
        Utils.bitmapToMat(bitmap, rgba)

        val gray = Mat()
        Imgproc.cvtColor(rgba, gray, Imgproc.COLOR_BGR2GRAY)

        if (resizeTo > 0) {
            Imgproc.resize(gray, gray, Size(resizeTo.toDouble(), resizeTo.toDouble()))
        }

        val rows = gray.rows()
        val cols = gray.cols()
        if (rows < 3 || cols < 3) return FloatArray(256)

        val pixels = ByteArray(rows * cols)
        gray.get(0, 0, pixels)

        fun at(r: Int, c: Int): Int = pixels[r * cols + c].toInt() and 0xFF

        val hist = FloatArray(256)
        var count = 0

        for (r in 1 until rows - 1) {
            for (c in 1 until cols - 1) {
                val center = at(r, c)
                var code = 0

                if (at(r - 1, c - 1) >= center) code = code or 1
                if (at(r - 1, c) >= center) code = code or 2
                if (at(r - 1, c + 1) >= center) code = code or 4
                if (at(r, c + 1) >= center) code = code or 8
                if (at(r + 1, c + 1) >= center) code = code or 16
                if (at(r + 1, c)     >= center) code = code or 32
                if (at(r + 1, c - 1) >= center) code = code or 64
                if (at(r,c - 1) >= center) code = code or 128

                hist[code] += 1f
                count++
            }
        }

        val denom = count.toFloat().coerceAtLeast(1f)
        for (i in hist.indices) hist[i] /= denom

        return hist
    }
}
