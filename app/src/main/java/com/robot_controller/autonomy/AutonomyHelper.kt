package com.robot_controller.autonomy

import android.content.Context
import com.robot_controller.autonomy.interfaces.DescriptorExtractor
import com.robot_controller.data.repository.EnvironmentRepository

object AutonomyHelper {
    @Volatile private var initialized = false
    var knnK: Int = 1

    lateinit var repository: EnvironmentRepository
        private set
    lateinit var extractor: DescriptorExtractor
        private set
    lateinit var mapEnvironmentUseCase: MapEnvironmentUseCase
        private set
    lateinit var locateEnvironmentUseCase: LocateEnvironmentUseCase
        private set

    fun init(context: Context) {
        if (initialized) return
        synchronized(this) {
            if (initialized) return

            repository = EnvironmentRepository(context)
            extractor = LbpHistDescriptorExtractor()

            mapEnvironmentUseCase = MapEnvironmentUseCase(
                extractor = extractor,
                repository = repository
            )

            locateEnvironmentUseCase = LocateEnvironmentUseCase(
                repo = repository,
                extractor = extractor,
                kProvider = { knnK }
            )

            initialized = true
        }
    }
}
