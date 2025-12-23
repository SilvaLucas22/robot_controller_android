package com.robot_controller.utils.enums

import com.robot_controller.R

enum class ErrorEnum {
    NETWORK_PARAMS,
    ON_SEND_COMMAND,
    FAIL_TO_CONNECT,
    ROBOT_OFFLINE;

    val message: Int
        get() = when (this) {
            NETWORK_PARAMS -> R.string.network_params_error
            ON_SEND_COMMAND -> R.string.on_send_command_error
            FAIL_TO_CONNECT -> R.string.fail_to_connect_to_robot
            ROBOT_OFFLINE -> R.string.robot_offline_error
        }
}
