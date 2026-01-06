package com.robot_controller.data.reponses

import com.google.gson.annotations.SerializedName

data class CompassResponse(
    @SerializedName("ok") val ok: Int,
    @SerializedName("deg") val degrees: Double,
)
