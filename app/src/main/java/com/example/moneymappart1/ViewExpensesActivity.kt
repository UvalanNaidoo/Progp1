package com.example.moneymappart1

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymappart1.databinding.ActivityViewExpensesBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ViewExpensesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewExpensesBinding
    private lateinit var db: AppDatabase
    private lateinit var adapter: ExpenseAdapter
    private var expenseList: List<ExpenseEntity> = listOf()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        binding.rvExpenses.layoutManager = LinearLayoutManager(this)

        setupDatePickers()
        loadCategoriesIntoSpinner()

        lifecycleScope.launch {
            expenseList = db.expenseDao().getAllExpenses()
            displayExpenses(expenseList)
        }

        binding.btnFilterExpenses.setOnClickListener {
            filterExpenses()
        }
    }

    private fun setupDatePickers() {
        binding.etStartDate.setOnClickListener {
            pickDate { date -> binding.etStartDate.setText(date) }
        }
        binding.etEndDate.setOnClickListener {
            pickDate { date -> binding.etEndDate.setText(date) }
        }
    }

    private fun pickDate(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val pickedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            onDateSelected(pickedDate)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePicker.show()
    }

    private fun loadCategoriesIntoSpinner() {
        lifecycleScope.launch {
            val categories = db.categoryDao().getAllCategories().map { it.name }
            val spinnerAdapter = ArrayAdapter(this@ViewExpensesActivity, android.R.layout.simple_spinner_item, listOf("All Categories") + categories)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategoryFilter.adapter = spinnerAdapter
        }
    }

    private fun filterExpenses() {
        val startDateStr = binding.etStartDate.text.toString()
        val endDateStr = binding.etEndDate.text.toString()
        val selectedCategory = binding.spinnerCategoryFilter.selectedItem.toString()

        val filteredList = expenseList.filter { expense ->
            val matchesDate = try {
                val expenseDate = dateFormat.parse(expense.date)
                val startDate = if (startDateStr.isNotEmpty()) dateFormat.parse(startDateStr) else null
                val endDate = if (endDateStr.isNotEmpty()) dateFormat.parse(endDateStr) else null
                (startDate == null || !expenseDate!!.before(startDate)) &&
                        (endDate == null || !expenseDate!!.after(endDate))
            } catch (e: Exception) {
                true
            }

            val matchesCategory = if (selectedCategory == "All Categories") true else expense.category == selectedCategory

            matchesDate && matchesCategory
        }

        displayExpenses(filteredList)

        if (selectedCategory != "All Categories") {
            val totalAmount = filteredList.sumOf { it.amount }
            binding.tvTotalAmount.visibility = View.VISIBLE
            binding.tvTotalAmount.text = "Total Amount: R${String.format("%.2f", totalAmount)}"
        } else {
            binding.tvTotalAmount.visibility = View.GONE
        }
    }

    private fun displayExpenses(list: List<ExpenseEntity>) {
        adapter = ExpenseAdapter(list)
        binding.rvExpenses.adapter = adapter
    }
}
