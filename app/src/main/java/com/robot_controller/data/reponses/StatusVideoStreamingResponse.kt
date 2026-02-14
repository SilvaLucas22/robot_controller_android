package com.robot_controller.data.reponses

import com.google.gson.annotations.SerializedName

data class StatusVideoStreamingResponse(
    @SerializedName("ok") val ok: Int,
    @SerializedName("on") val online: Int,
    @SerializedName("cfg") val videoData: VideoData = VideoData(),
)

data class VideoData(
    @SerializedName("res") val resolution: String = "",
    @SerializedName("fps") val fps: Int = -1,
    @SerializedName("port") val httpPort: Int = -1,
    @SerializedName("dev") val dev: String = "",
)
