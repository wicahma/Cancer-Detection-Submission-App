package com.dicoding.asclepius.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.utils.SettingPreferences
import com.dicoding.asclepius.utils.dataStore
import java.io.File

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_history)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val pref = SettingPreferences.getInstance(this.dataStore)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this, pref)

        val historyViewModel: HistoryViewModel by viewModels { factory }

        historyViewModel.getAllHistory().observe(this) {
            setHistoryListData(it, historyViewModel)
        }

        binding.btnDelete.setOnClickListener {
            historyViewModel.deleteAllHistory()
            val resultDelete = File(this.filesDir, "").deleteRecursively()
            if (resultDelete) showToast("Hapus data berhasil") else showToast("Hapus data gagal!")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setHistoryListData(
        history: List<HistoryEntity>, viewModel: HistoryViewModel
    ) {
        val adapter = HistoryScanListAdapter()
        adapter.submitList(history)
        binding.rvHistoryScanCancer.adapter = adapter

        adapter.setOnItemClickCallback(object : HistoryScanListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: HistoryEntity, view: View) {
                val intent = Intent(applicationContext, ResultActivity::class.java)
                intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, data.imageName)
                intent.putExtra(ResultActivity.EXTRA_RESULT, data.predictionResult)
                intent.putExtra(ResultActivity.EXTRA_RESULT_PERCENT, data.confidenceScore)
            }
        })
    }
}