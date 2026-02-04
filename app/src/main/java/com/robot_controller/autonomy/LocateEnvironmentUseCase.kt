package com.robot_controller.autonomy

import android.graphics.Bitmap
import com.robot_controller.autonomy.interfaces.DescriptorExtractor
import com.robot_controller.data.repository.EnvironmentRepository
import com.robot_controller.utils.enums.Angle
import io.reactivex.rxjava3.core.Single
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.ml.KNearest
import org.opencv.ml.Ml
import kotlin.math.min

data class LocateResult(
    val environmentName: String,
    val predictedLabel: Int
)

class LocateEnvironmentUseCase(
    private val repo: EnvironmentRepository,
    private val extractor: DescriptorExtractor,
    private val kProvider: () -> Int
) {

    fun execute(north: Bitmap, east: Bitmap, south: Bitmap, west: Bitmap): Single<LocateResult> {
        return repo.listEnvironments()
            .flatMap { envs ->
                if (envs.isEmpty()) {
                    return@flatMap Single.error(IllegalStateException("Nenhum ambiente mapeado ainda."))
                }

                // Carrega vetor de treino (1 por ambiente)
                Single.zip(
                    envs.mapIndexed { label, envName ->
                        loadEnvVector(envName).map { vec1024 ->
                            Triple(label, envName, vec1024)
                        }
                    }
                ) { arr ->
                    @Suppress("UNCHECKED_CAST")
                    arr.toList() as List<Triple<Int, String, FloatArray>>
                }.map { triples ->

                    val labelToName = triples.associate { it.first to it.second }

                    val trainVectors = triples.map { it.third }
                    val trainLabels = triples.map { it.first }

                    val featureSize = trainVectors.first().size
                    val trainData = Mat(trainVectors.size, featureSize, CvType.CV_32F)
                    val labelsMat = Mat(trainVectors.size, 1, CvType.CV_32S)

                    trainVectors.forEachIndexed { i, vec ->
                        trainData.put(i, 0, vec)
                        labelsMat.put(i, 0, intArrayOf(trainLabels[i]))
                    }

                    val knn = KNearest.create()

                    val k = min(kProvider().coerceAtLeast(1), trainVectors.size)
                    knn.defaultK = k
                    knn.train(trainData, Ml.ROW_SAMPLE, labelsMat)

                    val query = concat4(
                        extractor.extract(north),
                        extractor.extract(east),
                        extractor.extract(south),
                        extractor.extract(west)
                    )

                    val queryMat = Mat(1, featureSize, CvType.CV_32F)
                    queryMat.put(0, 0, query)

                    val results = Mat()
                    val response = knn.findNearest(queryMat, k, results)
                    val predictedLabel = response.toInt()

                    LocateResult(
                        environmentName = labelToName[predictedLabel] ?: "Desconhecido",
                        predictedLabel = predictedLabel
                    )
                }
            }
    }

    private fun loadEnvVector(envName: String): Single<FloatArray> {
        return Single.zip(
            repo.load(envName, Angle.NORTH),
            repo.load(envName, Angle.EAST),
            repo.load(envName, Angle.SOUTH),
            repo.load(envName, Angle.WEST)
        ) { n, e, s, w ->
            concat4(n, e, s, w)
        }
    }

    private fun concat4(a: FloatArray, b: FloatArray, c: FloatArray, d: FloatArray): FloatArray {
        val out = FloatArray(a.size + b.size + c.size + d.size)
        var p = 0
        a.copyInto(out, p); p += a.size
        b.copyInto(out, p); p += b.size
        c.copyInto(out, p); p += c.size
        d.copyInto(out, p)
        return out
    }
}
