package com.example.moneymappart1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.moneymappart1.databinding.ActivityAddExpenseBinding
import kotlinx.coroutines.launch

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private lateinit var db: AppDatabase
    private var selectedImageUri: Uri? = null
    private var categoryList: List<CategoryEntity> = listOf() // 🆕 List to hold categories

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        // 🆕 Load categories into Spinner
        lifecycleScope.launch {
            categoryList = db.categoryDao().getAllCategories()
            val categoryNames = categoryList.map { it.name } // Just the names
            val adapter = ArrayAdapter(this@AddExpenseActivity, android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = adapter
        }

        // 🆕 Optional: Pick Image Setup
        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedImageUri = result.data?.data
                binding.ivSelectedImage.setImageURI(selectedImageUri)
                binding.ivSelectedImage.visibility = android.view.View.VISIBLE
            }
        }

        binding.btnPickImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        binding.btnSaveExpense.setOnClickListener {
            saveExpense()
        }
    }

    private fun saveExpense() {
        val date = binding.etDate.text.toString().trim()
        val startTime = binding.etStartTime.text.toString().trim()
        val endTime = binding.etEndTime.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val selectedCategory = binding.spinnerCategory.selectedItem?.toString() ?: ""
        val amountStr = binding.etAmount.text.toString().trim()

        if (date.isEmpty() || startTime.isEmpty() || endTime.isEmpty() || description.isEmpty() || selectedCategory.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        } else {
            val amount = amountStr.toDoubleOrNull()
            if (amount == null) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return
            }

            lifecycleScope.launch {
                val expense = ExpenseEntity(
                    date = date,
                    startTime = startTime,
                    endTime = endTime,
                    description = description,
                    category = selectedCategory,
                    amount = amount,
                    photoUri = selectedImageUri?.toString()
                )
                db.expenseDao().insertExpense(expense)

                runOnUiThread {
                    Toast.makeText(this@AddExpenseActivity, "Expense Saved Successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

}
