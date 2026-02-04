package com.robot_controller.data.reponses

import com.google.gson.annotations.SerializedName

data class AntennaLqiResponse(
    @SerializedName("ok") val ok: Int,
    @SerializedName("lqi") val lqi: Double,
)
