package com.example.moneymappart1

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.moneymappart1.databinding.ActivityDailyTrendGraphBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DailyTrendGraphActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDailyTrendGraphBinding
    private lateinit var db: AppDatabase
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyTrendGraphBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        // Set up date pickers
        binding.etStartDate.setOnClickListener {
            pickDate { binding.etStartDate.setText(it) }
        }
        binding.etEndDate.setOnClickListener {
            pickDate { binding.etEndDate.setText(it) }
        }

        binding.btnFilter.setOnClickListener {
            loadAndShowTrend()
        }

        // Load full data initially
        loadAndShowTrend()
    }

    private fun pickDate(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            val pickedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
            onDateSelected(pickedDate)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun loadAndShowTrend() {
        val startStr = binding.etStartDate.text.toString()
        val endStr = binding.etEndDate.text.toString()

        lifecycleScope.launch {
            val allExpenses = db.expenseDao().getAllExpenses()

            val filtered = allExpenses.filter {
                try {
                    val expenseDate = dateFormat.parse(it.date)
                    val startDate = if (startStr.isNotEmpty()) dateFormat.parse(startStr) else null
                    val endDate = if (endStr.isNotEmpty()) dateFormat.parse(endStr) else null
                    (startDate == null || !expenseDate!!.before(startDate)) &&
                            (endDate == null || !expenseDate!!.after(endDate))
                } catch (e: Exception) {
                    true
                }
            }

            val groupedByDate = filtered.groupBy { it.date }
                .toSortedMap() // ensure chronological order

            val entries = ArrayList<Entry>()
            val labels = ArrayList<String>()
            var index = 0f

            for ((date, list) in groupedByDate) {
                val total = list.sumOf { it.amount }
                entries.add(Entry(index, total.toFloat()))
                labels.add(date)
                index++
            }

            val dataSet = LineDataSet(entries, "Daily Spending")
            dataSet.setDrawCircles(true)
            dataSet.setDrawFilled(true)
            dataSet.color = getColor(R.color.white)
            dataSet.fillAlpha = 180

            val lineData = LineData(dataSet)

            with(binding.lineChart) {
                data = lineData
                xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                xAxis.labelRotationAngle = -45f
                axisRight.isEnabled = false
                description.isEnabled = false
                invalidate()
            }
        }
    }
}
