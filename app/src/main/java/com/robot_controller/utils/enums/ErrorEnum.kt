package com.robot_controller.utils.enums

import com.robot_controller.R

enum class ErrorEnum {
    NETWORK_PARAMS,
    ON_SEND_COMMAND;

    val message: Int
        get() = when (this) {
            NETWORK_PARAMS -> R.string.network_params_error
            ON_SEND_COMMAND -> R.string.on_send_command_error
        }

}