package com.maksimgromov.a7minutesworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.maksimgromov.a7minutesworkout.databinding.ActivityHistoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private var binding: ActivityHistoryBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarHistoryActivity)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = getString(R.string.history).uppercase()
        }

        binding?.toolbarHistoryActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

        val dao = (application as WorkOutApp).db.historyDao()
        getAllCompletedDates(dao)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun getAllCompletedDates(historyDao: HistoryDao) {
            lifecycleScope.launch(Dispatchers.IO) {
                historyDao.fetchAllDates().collect { allCompletedDatesList ->
                    if (allCompletedDatesList.isNotEmpty()) {
                        binding?.tvHistory?.visibility = View.VISIBLE
                        binding?.rvHistory?.visibility = View.VISIBLE
                        binding?.tvNoDataAvailable?.visibility = View.INVISIBLE
                        binding?.rvHistory?.layoutManager = LinearLayoutManager(this@HistoryActivity)
                        val dates = ArrayList<String>()
                        for (date in allCompletedDatesList) {
                            dates.add(date.date)
                        }
                        val historyAdapter = HistoryAdapter(dates)
                        binding?.rvHistory?.adapter = historyAdapter
                    } else {
                        binding?.tvHistory?.visibility = View.GONE
                        binding?.rvHistory?.visibility = View.GONE
                        binding?.tvNoDataAvailable?.visibility = View.VISIBLE
                    }
                }
            }
    }
}