package com.robot_controller.data.reponses

import com.google.gson.annotations.SerializedName

data class TelemetryResponse(
    @SerializedName("ok") val ok: Int,
    @SerializedName("bat") val battery: Double,
    @SerializedName("lqi") val signalQuality: Int,
    @SerializedName("tra") val traction: TractionData,
    @SerializedName("cam") val camera: CameraData,
    @SerializedName("ant") val antenna: AntennaData,
    @SerializedName("imu") val imu: ImuData,
    @SerializedName("stream") val stream: StreamData,
)

data class TractionData(
    @SerializedName("dir") val direction: String,
    @SerializedName("v") val velocity: Int,
)

data class CameraData(
    @SerializedName("pan") val pan: Int,
    @SerializedName("tilt") val tilt: Int,
)

data class AntennaData(
    @SerializedName("pan") val pan: Int,
    @SerializedName("tilt") val tilt: Int,
    @SerializedName("lqi") val lqi: Double? = null,
)

data class ImuData(
    @SerializedName("compass") val compass: Double,
)

data class StreamData(
    @SerializedName("on") val isOn: Int,
)
