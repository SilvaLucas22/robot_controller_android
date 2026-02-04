package com.robot_controller.autonomy.interfaces

import android.graphics.Bitmap

interface DescriptorExtractor {
    val name: String
    val featureSize: Int
    fun extract(bitmap: Bitmap): FloatArray
}
