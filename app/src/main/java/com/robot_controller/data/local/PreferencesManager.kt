package com.robot_controller.data.local

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("ROBOT_PREFS", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IP_DOMAIN = "ROBOT_IP_DOMAIN"
        private const val KEY_TCP_PORT = "ROBOT_TCP_PORT"
    }

    var ipAddressOrDomain: String?
        set(value) {
            prefs.edit().putString(KEY_IP_DOMAIN, value).apply()
        }
        get() {
            return prefs.getString(KEY_IP_DOMAIN, null)
        }

    var tcpPort: String?
        set(value) {
            prefs.edit().putString(KEY_TCP_PORT, value).apply()
        }
        get() {
            return prefs.getString(KEY_TCP_PORT, null)
        }
}
