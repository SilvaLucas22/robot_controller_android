package com.robot_controller.data.local

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("ROBOT_PREFS", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IP_ADDRESS = "ROBOT_IP_ADDRESS"
        private const val KEY_UDP_PORT = "ROBOT_UDP_PORT"
    }

    var ipAddress: String?
        set(value) {
            prefs.edit().putString(KEY_IP_ADDRESS, value).apply()
        }
        get() {
            return prefs.getString(KEY_IP_ADDRESS, null)
        }

    var udpPort: String?
        set(value) {
            prefs.edit().putString(KEY_UDP_PORT, value).apply()
        }
        get() {
            return prefs.getString(KEY_UDP_PORT, null)
        }

}