package com.example.androidproject.plantCameraActivities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.widget.Toast
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import android.util.Log
import android.view.View
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import com.example.androidproject.databinding.ActivityScanPlantsBinding
import com.example.androidproject.model.tools.GraphicOverlay
import com.example.androidproject.model.tools.ObjectGraphic
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions


class ScanPlantsActivity : AppCompatActivity() {

    private lateinit var binding :ActivityScanPlantsBinding


    private var imageCapture: ImageCapture? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var previewView: PreviewView? = null



    private lateinit var cameraExecutor: ExecutorService

    companion object {
        private const val TAG = "ScanningPlants"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanPlantsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Request camera permissions

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        binding.wantReturnBtn.setOnClickListener {
            finish()
        }
        previewView = binding.viewFinder
        graphicOverlay = binding.graphicOverlayy



        cameraExecutor = Executors.newSingleThreadExecutor()


    }

    override fun onStart() {
        super.onStart()
        binding.hintText.visibility = View.VISIBLE
    }
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder().build()


            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, ImageAnalyzer(this, graphicOverlay!!))
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))

    }
    private inner class ImageAnalyzer(ctx : Context, private val graphicsOverlay : GraphicOverlay) : ImageAnalysis.Analyzer {

        private val localModel = LocalModel.Builder()
            .setAssetFilePath("lite-model_aiy_vision_classifier_plants_V1_3.tflite")
            .build()

        private val customObjectDetectorOptions = CustomObjectDetectorOptions.Builder(localModel)
            .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
            .enableClassification()
            .setClassificationConfidenceThreshold(0.7f)
            .setMaxPerObjectLabelCount(1)
            .build()

        private val objectDetector = ObjectDetection.getClient(customObjectDetectorOptions)

        var needUpdateGraphOSrcInfo = true

        @SuppressLint("UnsafeOptInUsageError")
        override fun analyze(imageP: ImageProxy) {
            if(needUpdateGraphOSrcInfo) {
                val rotationDegrees = imageP.imageInfo.rotationDegrees
                if(rotationDegrees == 0 || rotationDegrees == 180) {
                    graphicsOverlay.setImageSourceInfo(
                        imageP.width, imageP.height, false
                    )
                } else {
                    graphicsOverlay.setImageSourceInfo(
                        imageP.height, imageP.width, false
                    )
                }
                needUpdateGraphOSrcInfo = false
            }

            val mediaImage  = imageP.image
            if(mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageP.imageInfo.rotationDegrees)

                objectDetector.process(image)
                    .addOnFailureListener { Log.d("Scan.Plant", it.printStackTrace().toString()) }
                    .addOnSuccessListener {
                        binding.hintText.visibility = View.GONE
                        graphicsOverlay.clear()
                        for(dO in it) {
                            graphicsOverlay.add(ObjectGraphic(graphicsOverlay, dO))
                        }
                        graphicsOverlay.postInvalidate()
                    }.addOnCompleteListener {
                        imageP.close()
                    }

            }

        }


    }
}