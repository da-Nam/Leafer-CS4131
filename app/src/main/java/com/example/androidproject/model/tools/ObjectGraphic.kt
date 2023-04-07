package com.example.androidproject.model.tools

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.google.mlkit.vision.objects.DetectedObject
import java.util.*
import kotlin.math.max
import kotlin.math.min


/*
NOTICE:
Object Graphic class sampled from Google Under Apache License
https://github.com/googlesamples/mlkit/tree/master/android/vision-quickstart
https://www.apache.org/licenses/LICENSE-2.0
 */class ObjectGraphic constructor(
    overlay: GraphicOverlay,
    private val detectedObject: DetectedObject
) : GraphicOverlay.Graphic(overlay) {


    private val boxPaint = Paint()
    private val textPaint =  Paint()
    private val labelPaint = Paint()

    init {
            textPaint.color = COLORS[0][0]
            textPaint.textSize = TEXT_SIZE
            boxPaint.color = COLORS[0][1]
            boxPaint.style = Paint.Style.STROKE
            boxPaint.strokeWidth = STROKE_WIDTH
            labelPaint.color = COLORS[0][1]
            labelPaint.style = Paint.Style.FILL

    }

    override fun draw(canvas: Canvas?) {
        // Decide color based on object tracking ID
        val colorID = 0
        var textWidth = textPaint.measureText("Tracking ID: " + detectedObject.trackingId)
        val lineHeight = TEXT_SIZE + STROKE_WIDTH
        var yLabelOffset = -lineHeight

        // Calculate width and height of label box
        for (label in detectedObject.labels) {
            textWidth =
                max(textWidth, textPaint.measureText(label.text))
            textWidth = max(
                textWidth,
                textPaint.measureText(
                    String.format(
                        Locale.US,
                        LABEL_FORMAT,
                        label.confidence * 100,
                        label.index
                    )
                )
            )
            yLabelOffset -= 2 * lineHeight
        }

        // Draws the bounding box.
        val rect = RectF(detectedObject.boundingBox)
        val x0 = translateX(rect.left)
        val x1 = translateX(rect.right)
        rect.left = min(x0, x1)
        rect.right = max(x0, x1)
        rect.top = translateY(rect.top)
        rect.bottom = translateY(rect.bottom)
        canvas?.drawRect(rect, boxPaint)

        // Draws other object info.
        canvas?.drawRect(
            rect.left - STROKE_WIDTH,
            rect.top + yLabelOffset,
            rect.left + textWidth + 2 * STROKE_WIDTH,
            rect.top,
            labelPaint
        )
        yLabelOffset += lineHeight
        for (label in detectedObject.labels) {
            canvas?.drawText(
                label.text + " (index: " + label.index + ")",
                rect.left,
                rect.top + yLabelOffset,
                textPaint
            )
            yLabelOffset += lineHeight
            canvas?.drawText(
                String.format(
                    Locale.US,
                    LABEL_FORMAT,
                    label.confidence * 100,
                    label.index
                ),
                rect.left,
                rect.top + yLabelOffset,
                textPaint
            )
            yLabelOffset += lineHeight
        }
    }

    companion object {
        private const val TEXT_SIZE = 54.0f
        private const val STROKE_WIDTH = 4.0f
        private val COLORS =
            arrayOf(
                intArrayOf(Color.BLACK, Color.WHITE)
            )
        private const val LABEL_FORMAT = "%.2f%% confidence (index: %d)"
    }
}