package com.robot_controller.data

import com.google.gson.annotations.SerializedName

data class RobotCommand(
    @SerializedName("m") val module: String,
    @SerializedName("a") val action: String,
    @SerializedName("d") val direction: String? = null,
    @SerializedName("pan") val pan: Int? = null,
    @SerializedName("tilt") val tilt: Int? = null,
)