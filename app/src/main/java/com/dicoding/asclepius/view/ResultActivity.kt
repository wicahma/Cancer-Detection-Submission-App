package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.data.remote.response.ArticleResponse
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.utils.SettingPreferences
import com.dicoding.asclepius.utils.dataStore

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
        const val EXTRA_RESULT_PERCENT = "extra_result_percent"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = SettingPreferences.getInstance(this.dataStore)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this, pref)
        val resultViewModel: ResultViewModel by viewModels { factory }

        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        imageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.resultImage.setImageURI(it)
        }

        val detectedText = intent.getStringExtra(EXTRA_RESULT)
        val detectedTextResult = intent.getStringExtra(EXTRA_RESULT_PERCENT)
        binding.resultText.text = " - $detectedText"
        binding.resultDesc.text = detectedText.let {
            if (it!!.contains("Non")) "Hasil prediksi dari AI menunjukkan bahwa tidak ada tanda-tanda kanker, " + "dan AI memberikan nilai kepercayaan sebesar $detectedTextResult. Ini bisa diartikan bahwa AI telah menganalisis data dengan cermat " + "dan menemukan indikasi kuat bahwa kondisi tersebut tidak berkaitan dengan kanker. Namun, perlu diingat bahwa AI juga " + "memiliki keterbatasan, dan hasilnya tidak selalu 100% akurat. Oleh karena itu, sangat penting untuk tetap mengikuti " + "saran dari profesional medis dan melakukan pemeriksaan rutin untuk memastikan kesehatan yang optimal. " + "Jangan ragu untuk bertanya kepada dokter tentang hasil tersebut dan apakah ada langkah tambahan yang perlu " + "diambil untuk menjaga kesehatan Anda."
            else "Hasil prediksi dari AI menunjukkan bahwa ada kemungkinan adanya kanker, dengan nilai kepercayaan sebesar $detectedTextResult. " + "Ini bisa sangat mengkhawatirkan dan membuat cemas. Namun, penting untuk diingat bahwa ini hanyalah hasil dari analisis data oleh AI, " + "dan bukan diagnosis pasti. Hal pertama yang harus dilakukan adalah berkonsultasi dengan dokter untuk evaluasi lebih lanjut. " + "Dokter akan melakukan pemeriksaan lebih lanjut dan mungkin merujuk Anda untuk tes tambahan atau konsultasi spesialis " + "untuk memastikan diagnosis yang akurat dan merencanakan rencana perawatan yang tepat. Meskipun berita ini mungkin menakutkan, " + "tetapi ingatlah bahwa ada banyak kemajuan dalam pengobatan kanker dan dukungan yang tersedia untuk membantu Anda melalui proses ini. " + "Tetaplah bersabar, jangan ragu untuk mencari dukungan dari keluarga, teman, dan profesional medis selama perjalanan ini."
        }

        resultViewModel.getTopNews().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is com.dicoding.asclepius.data.Result.Loading -> {
                        binding.progressIndicator.visibility = View.VISIBLE
                    }

                    is com.dicoding.asclepius.data.Result.Success -> {
                        binding.progressIndicator.visibility = View.GONE
                        val articles = result.data
                        Log.d("Recycler View Article:", articles.toString())
                        setArticleListData(articles)
                    }

                    is com.dicoding.asclepius.data.Result.Error -> {
                        binding.progressIndicator.visibility = View.GONE
                        showToast(result.error)
                    }
                }
            }
        }
    }

    private fun setArticleListData(
        history: List<ArticleResponse>
    ) {
        val adapter = ArticleListAdapter()
        adapter.submitList(history)
        binding.rvArticle.adapter = adapter


        adapter.setOnItemClickCallback(object : ArticleListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ArticleResponse, view: View) {
                val shareIntent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                startActivity(shareIntent)
            }
        })

    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}