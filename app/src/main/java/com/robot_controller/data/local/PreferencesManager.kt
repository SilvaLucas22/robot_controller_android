package com.robot_controller.data.local

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("ROBOT_PREFS", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IP_DOMAIN = "ROBOT_IP_DOMAIN"
        private const val KEY_TCP_PORT_COMMANDS = "ROBOT_TCP_PORT_COMMANDS"
        private const val KEY_TCP_PORT_VIDEO = "ROBOT_TCP_PORT_VIDEO"
    }

    var ipAddressOrDomain: String?
        set(value) {
            prefs.edit().putString(KEY_IP_DOMAIN, value).apply()
        }
        get() {
            return prefs.getString(KEY_IP_DOMAIN, null)
        }

    var tcpPortCommands: String?
        set(value) {
            prefs.edit().putString(KEY_TCP_PORT_COMMANDS, value).apply()
        }
        get() {
            return prefs.getString(KEY_TCP_PORT_COMMANDS, null)
        }

    var tcpPortVideo: String?
        set(value) {
            prefs.edit().putString(KEY_TCP_PORT_VIDEO, value).apply()
        }
        get() {
            return prefs.getString(KEY_TCP_PORT_VIDEO, null)
        }
}
