package com.example.moneymappart1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.moneymappart1.databinding.ActivityAddCategoryBinding
import kotlinx.coroutines.launch

class AddCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCategoryBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        binding.btnSaveCategory.setOnClickListener {
            val categoryName = binding.etCategoryName.text.toString().trim()

            if (categoryName.isEmpty()) {
                Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch {
                    val category = CategoryEntity(name = categoryName)
                    db.categoryDao().insertCategory(category)
                    runOnUiThread {
                        Toast.makeText(this@AddCategoryActivity, "Category Saved!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }
}
