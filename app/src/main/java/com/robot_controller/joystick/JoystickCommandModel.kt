package com.robot_controller.joystick

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi

@JsonClass(generateAdapter = true)
data class JoystickCommandModel(
    val joystickType: JoystickType,
    val joystickCommand: JoystickCommand,
    var speed: Int = 0
) {

    fun toJson(): String {
        return ""
        //nao vai funcionar assim, falta montar o vetor certinho
//        val moshi = Moshi.Builder().build()
//        val adapter = moshi.adapter(JoystickCommandModel::class.java)
//        return adapter.toJson(this)
    }

}
