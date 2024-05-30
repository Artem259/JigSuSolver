package com.jigsusolver.sudoku.imageproc.classification

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

class ImageClassifier(
    private val context: Context,
    private var labelAllowList: List<String>,
    private var maxResults: Int = 1,
    private var threshold: Float = 0.0f,
    private var numThreads: Int = 1
) {
    private val classifier: ImageClassifier

    init {
        classifier = setupClassifier()
    }

    fun classify(image: Bitmap): List<Classifications> {
        val tensorImage = TensorImage.fromBitmap(image)
        return classifier.classify(tensorImage)
    }

    private fun setupClassifier(): ImageClassifier {
        val baseOptionsBuilder = BaseOptions.builder()
        baseOptionsBuilder.setNumThreads(numThreads)

        if (CompatibilityList().isDelegateSupportedOnThisDevice) {
            baseOptionsBuilder.useGpu()
            Log.i("ImageClassifier", "Using GPU delegate.")
        }

        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
        optionsBuilder.setLabelAllowList(labelAllowList)
        optionsBuilder.setMaxResults(maxResults)
        optionsBuilder.setScoreThreshold(threshold)

        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        return ImageClassifier.createFromFileAndOptions(
            context, "model.tflite", optionsBuilder.build()
        ) // TODO const
    }
}
