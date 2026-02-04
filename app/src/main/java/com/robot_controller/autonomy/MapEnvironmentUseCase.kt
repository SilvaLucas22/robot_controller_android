package com.robot_controller.autonomy

import android.graphics.Bitmap
import com.robot_controller.autonomy.interfaces.DescriptorExtractor
import com.robot_controller.data.repository.EnvironmentRepository
import com.robot_controller.utils.enums.Angle
import io.reactivex.rxjava3.core.Single

class MapEnvironmentUseCase(
    private val extractor: DescriptorExtractor,
    private val repository: EnvironmentRepository
) {
    fun execute(env: String, angle: Angle, bitmap: Bitmap): Single<Unit> {
        return Single.fromCallable { extractor.extract(bitmap) }
            .flatMap { features ->
                if (features.size != extractor.featureSize) {
                    Single.error(IllegalStateException("Feature size inválido: ${features.size} != ${extractor.featureSize}"))
                } else {
                    repository.save(env, angle, features)
                }
            }
    }
}
