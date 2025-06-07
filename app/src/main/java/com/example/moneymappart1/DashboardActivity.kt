package com.example.moneymappart1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.example.moneymappart1.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddExpense.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }


        binding.btnSetGoals.setOnClickListener {
            val intent = Intent(this, SetGoalsActivity::class.java)
            startActivity(intent)
        }


        binding.btnViewExpenses.setOnClickListener {
            val intent = Intent(this, ViewExpensesActivity::class.java)
            startActivity(intent)
        }

        binding.btnAddCategory.setOnClickListener {
            val intent = Intent(this, AddCategoryActivity::class.java)
            startActivity(intent)
        }

        binding.btnCategoryGraph.setOnClickListener {
            val intent = Intent(this, CategoryGraphActivity::class.java)
            startActivity(intent)
        }

        binding.btnDailyTrends.setOnClickListener {
            val intent = Intent(this, DailyTrendGraphActivity::class.java)
            startActivity(intent)
        }

        binding.btnProgressReport.setOnClickListener {
            val intent = Intent(this, ProgressReportActivity::class.java)
            startActivity(intent)
        }


    }
}
