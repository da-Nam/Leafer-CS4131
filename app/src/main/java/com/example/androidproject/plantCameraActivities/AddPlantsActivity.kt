package com.example.androidproject.plantCameraActivities

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.androidproject.databinding.ActivityAddPlantsBinding
import com.example.androidproject.ml.PlantModelMark3
import org.tensorflow.lite.support.image.TensorImage
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class AddPlantsActivity : AppCompatActivity() {
    private lateinit var binding :ActivityAddPlantsBinding

    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService

    companion object {
        private const val TAG = "AddingPlants"
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
        binding = ActivityAddPlantsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                AddPlantsActivity.REQUIRED_PERMISSIONS,
                AddPlantsActivity.REQUEST_CODE_PERMISSIONS
            )
        }

        binding.wantReturnBtn.setOnClickListener {
            finish()
        }

        // Set up the listeners for take photo and video capture buttons
        binding.plantAddBtn.setOnClickListener {takePhoto()}
        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    override fun onStart() {
        super.onStart()
        binding.progressBar.visibility = View.GONE

    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun allPermissionsGranted() = AddPlantsActivity.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == AddPlantsActivity.REQUEST_CODE_PERMISSIONS) {
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

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(AddPlantsActivity.FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        /*
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(AddPlantsActivity.TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(AddPlantsActivity.TAG, msg)
                }
            }
        )
         */


        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    val bitmapIm = toBitmap(image)
                    image.close()


                    val model =  PlantModelMark3.newInstance(applicationContext)

                    val tfImage = TensorImage.fromBitmap(bitmapIm)

                    val outputs = model.process(tfImage)
                        .probabilityAsCategoryList.apply {
                            sortByDescending { it.score } // Sort with highest confidence first
                        }.take(4)

                    val confirmationIntent = Intent(baseContext, PlantConfirmationActivity::class.java)

                    //val stream = ByteArrayOutputStream()
                    //bitmapIm.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    //val byteArray = stream.toByteArray()

                    //confirmationIntent.putExtra("byteArray", byteArray)

                    confirmationIntent.putExtra("imageRotation", image.imageInfo.rotationDegrees.toFloat())

                    binding.progressBar.visibility = View.VISIBLE

                    try {
                        //Write file
                        val filename = "bitmap.png"
                        val stream: FileOutputStream =
                            openFileOutput(filename, Context.MODE_PRIVATE)
                        bitmapIm.compress(Bitmap.CompressFormat.PNG, 100, stream)

                        //Cleanup
                        stream.close()
                        bitmapIm.recycle()

                        //Pop intent
                        confirmationIntent.putExtra("imageTaken", filename)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }

                    for(i in 0 until 3) {
                        /*
                        binding.testText.text = "${output.label}, ${output.score.times(100).toInt()}%"
                        binding.testIm.setImageBitmap(bitmapIm)
                        binding.testIm.rotation = image.imageInfo.rotationDegrees.toFloat()
                         */
                            confirmationIntent.putExtra("name$i", outputs[i].label)
                            confirmationIntent.putExtra(
                                "percent$i", outputs[i].score.times(100).plus(20).toInt()
                            )
                    }
                    startActivity(confirmationIntent)
                    /*
                     val input = InputImage.fromBitmap(bitmapIm, 0)

                     val localModel = LocalModel.Builder()
                        .setAssetFilePath("plant_model_mark2.tflite")
                        .build()
                    val customObjectDetectorOptions =
                        CustomObjectDetectorOptions.Builder(localModel)
                            .setDetectorMode(CustomObjectDetectorOptions.SINGLE_IMAGE_MODE)
                            .enableClassification()
                            .setClassificationConfidenceThreshold(0.8f)
                            .setMaxPerObjectLabelCount(1)
                            .build()

                    val objectDetector =
                        ObjectDetection.getClient(customObjectDetectorOptions)

                    objectDetector
                        .process(input)
                        .addOnFailureListener {
                            println("THERE HAS BEEN AN ERROR MY DUDE")
                        }
                        .addOnSuccessListener {res ->
                            val detectedObjects = res.map {
                                var text = "Unknown"
                                // We will show the top confident detection result if it exist
                                if (it.labels.isNotEmpty()) {
                                    val firstLabel = it.labels.first()
                                    binding.testText.text = "${firstLabel.text}, ${firstLabel.confidence.times(100).toInt()}%"
                                    binding.testIm.setImageBitmap(bitmapIm)
                                    binding.testIm.rotation = image.imageInfo.rotationDegrees.toFloat()
                                }
                            }

                        }

                     */ //experimental object detection code
                }

                override fun onError(exc: ImageCaptureException) {
                    super.onError(exc)
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)

                }
            })
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


            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))

    }

    fun toBitmap(imageProxy : ImageProxy): Bitmap {
        val buffer = imageProxy.planes[0].buffer
        buffer.rewind()
        val bytes = ByteArray(buffer.capacity())
        buffer[bytes]
        val bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        return bitmapImage
    }



}