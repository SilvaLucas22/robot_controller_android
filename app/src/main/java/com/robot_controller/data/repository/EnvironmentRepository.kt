package com.robot_controller.data.repository

import android.content.Context
import com.google.gson.Gson
import com.robot_controller.utils.enums.Angle
import io.reactivex.rxjava3.core.Single
import java.io.File

data class StoredFeatures(
    val env: String,
    val angle: String,
    val features: FloatArray,
)

class EnvironmentRepository(
    context: Context,
    private val gson: Gson = Gson(),
    private val rootDirName: String = "environments"
) {

    private val appContext = context.applicationContext

    private fun envDir(env: String): File =
        File(appContext.filesDir, "$rootDirName/$env").apply { mkdirs() }

    fun save(env: String, angle: Angle, features: FloatArray): Single<Unit> {
        return Single.fromCallable {
            val payload = StoredFeatures(env = env, angle = angle.name, features = features)
            val file = File(envDir(env), "${angle.name}.json")
            file.writeText(gson.toJson(payload))
            Unit
        }
    }

    fun load(env: String, angle: Angle): Single<FloatArray> {
        return Single.fromCallable {
            val file = File(envDir(env), "${angle.name}.json")
            val payload = gson.fromJson(file.readText(), StoredFeatures::class.java)
            payload.features
        }
    }

    fun listEnvironments(): Single<List<String>> {
        return Single.fromCallable {
            val root = File(appContext.filesDir, rootDirName)
            if (!root.exists()) emptyList()
            else root.listFiles()
                ?.filter { it.isDirectory }
                ?.map { it.name }
                ?: emptyList()
        }
    }
}
