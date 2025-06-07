package com.example.moneymappart1

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.moneymappart1.databinding.ActivityCategoryGraphBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CategoryGraphActivity : AppCompatActivity() {

    // ✅ Final Step: Declare view binding
    private lateinit var binding: ActivityCategoryGraphBinding
    private lateinit var db: AppDatabase
    private lateinit var barChart: BarChart
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryGraphBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        barChart = binding.barChart

        // Step 1: Setup date pickers
        binding.etStartDate.setOnClickListener {
            pickDate { binding.etStartDate.setText(it) }
        }
        binding.etEndDate.setOnClickListener {
            pickDate { binding.etEndDate.setText(it) }
        }

        // Step 2: Filter button logic
        binding.btnFilter.setOnClickListener {
            loadFilteredData()
        }

        // Load default
        loadFilteredData()
    }

    private fun pickDate(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            val date = String.format("%04d-%02d-%02d", year, month + 1, day)
            onDateSelected(date)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    // Step 3: Load and group filtered expenses
    private fun loadFilteredData() {
        val startDateStr = binding.etStartDate.text.toString()
        val endDateStr = binding.etEndDate.text.toString()

        lifecycleScope.launch {
            val expenses = db.expenseDao().getAllExpenses()

            val filtered = expenses.filter {
                try {
                    val expenseDate = dateFormat.parse(it.date)
                    val startDate = if (startDateStr.isNotEmpty()) dateFormat.parse(startDateStr) else null
                    val endDate = if (endDateStr.isNotEmpty()) dateFormat.parse(endDateStr) else null
                    (startDate == null || !expenseDate!!.before(startDate)) &&
                            (endDate == null || !expenseDate!!.after(endDate))
                } catch (e: Exception) {
                    true
                }
            }

            showGraph(filtered)
        }
    }

    private fun showGraph(expenses: List<ExpenseEntity>) {
        val grouped = expenses.groupBy { it.category }
        val barEntries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        var index = 0f

        grouped.forEach { (category, items) ->
            val total = items.sumOf { it.amount }
            barEntries.add(BarEntry(index, total.toFloat()))
            labels.add(category)
            index++
        }

        val barDataSet = BarDataSet(barEntries, "Spending per Category")
        barDataSet.color = resources.getColor(R.color.white)
        val barData = BarData(barDataSet)
        barChart.data = barData

        // Axis formatting
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawLabels(true)

        barChart.axisRight.isEnabled = false
        barChart.description.isEnabled = false

        val leftAxis: YAxis = barChart.axisLeft
        leftAxis.removeAllLimitLines() // Clear old lines

        val minGoal = LimitLine(100f, "Min Goal")
        minGoal.lineColor = Color.GREEN
        minGoal.lineWidth = 2f
        minGoal.textColor = Color.GREEN

        val maxGoal = LimitLine(500f, "Max Goal")
        maxGoal.lineColor = Color.RED
        maxGoal.lineWidth = 2f
        maxGoal.textColor = Color.RED

        leftAxis.addLimitLine(minGoal)
        leftAxis.addLimitLine(maxGoal)

        // Refresh chart
        barChart.invalidate()
    }
}
