package com.dicoding.asclepius.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.utils.SettingPreferences
import com.dicoding.asclepius.utils.dataStore
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    private var destinationUri: Uri? = null
    private var resultCropUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val pref = SettingPreferences.getInstance(this.dataStore)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this, pref)
        val mainViewModel: MainViewModel by viewModels { factory }

        mainViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                mainViewModel.changeDarkmode(true)
                binding.btnTema.setIconResource(R.drawable.night_icon)
                binding.btnTema.text = "Dark Mode"
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                mainViewModel.changeDarkmode(false)
                binding.btnTema.setIconResource(R.drawable.light_icon)
                binding.btnTema.text = "Light Mode"
            }
        }

        binding.btnTema.setOnClickListener {
            mainViewModel.changeDarkmode(mainViewModel.isDarkModeActive.value!!)
            mainViewModel.saveThemeSetting(!mainViewModel.isDarkModeActive.value!!)
        }

        binding.btnHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)

            startActivity(intent)
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener {
            resultCropUri?.let {
                analyzeImage(mainViewModel)
            } ?: run {
                showToast(getString(R.string.empty_image_warning))
            }
            moveToResult(mainViewModel)
        }
    }

    private fun startGallery() {
        File(this.filesDir, "croppedImage.jpg").delete()
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            destinationUri = File(this.filesDir, "${uri.lastPathSegment}-croppedImage.jpg").toUri()
            UCrop.of(uri, destinationUri!!).withAspectRatio(2f, 2f).start(this)
        } else {
            Log.d("Photo Picker", "No media selected")
            showToast("No media selected")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
                resultCropUri = UCrop.getOutput(data!!)
                showImage()
            } else if (resultCode == UCrop.RESULT_ERROR) {
                val cropError = UCrop.getError(data!!)
                Log.d("Photo Picker", "No media selected")
                showToast("No media selected: ${cropError.toString()}")
            }
        } catch (e: Throwable) {
            Log.d("Photo Cropper", e.message.toString())
            showToast(e.message.toString())
        }
    }

    private fun showImage() {
        resultCropUri.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageResource(android.R.color.transparent)
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage(viewModel: MainViewModel) {
        binding.progressIndicator.visibility = View.VISIBLE
        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    runOnUiThread {
                        results?.let { it ->
                            if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                                println(it)
                                val sortedCategories =
                                    it[0].categories.sortedByDescending { it?.score }

                                sortedCategories[0].let {
                                    viewModel.changePercentage(
                                        NumberFormat.getPercentInstance().format(it.score).trim()
                                    )
                                    viewModel.changeResult(it.label)
                                }

                                binding.progressIndicator.visibility = View.GONE

                            }
                        }
                    }
                }
            })

        resultCropUri?.let {
            imageClassifierHelper.classifyStaticImage(it)
        }
    }

    private fun moveToResult(viewModel: MainViewModel) {
        if (viewModel.textResult.value != "") {
            Log.d("Photo Picker", "${resultCropUri?.lastPathSegment}")
            val historyData = HistoryEntity(
                predictionResult = viewModel.textResult.value.toString(),
                imageName = resultCropUri.toString(),
                confidenceScore = viewModel.percentageResult.value.toString()
            )
            viewModel.saveResultScan(historyData)
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, resultCropUri.toString())
            intent.putExtra(ResultActivity.EXTRA_RESULT, viewModel.textResult.value)
            intent.putExtra(ResultActivity.EXTRA_RESULT_PERCENT, viewModel.percentageResult.value)

            startActivity(intent)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}