package com.robot_controller.data.reponses

import com.google.gson.annotations.SerializedName

data class AntennaInfoResponse(
    @SerializedName("ok") val ok: Int,
    @SerializedName("best") val antennaData: AntennaData = AntennaData(),
)
