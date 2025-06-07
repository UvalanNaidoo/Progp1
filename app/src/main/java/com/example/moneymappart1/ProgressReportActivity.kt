package com.example.moneymappart1

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins
import androidx.lifecycle.lifecycleScope
import com.example.moneymappart1.databinding.ActivityProgressReportBinding
import kotlinx.coroutines.launch

class ProgressReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgressReportBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgressReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        loadProgressReport()
    }

    private fun loadProgressReport() {
        lifecycleScope.launch {
            val goals = db.goalDao().getAllGoals()
            val expenses = db.expenseDao().getAllExpenses()

            val dashboard = binding.dashboardContainer
            dashboard.removeAllViews()

            goals.forEach { goal ->
                val categoryExpenses = expenses.filter { it.category == goal.category }
                val totalSpent = categoryExpenses.sumOf { it.amount }

                // Title
                val titleView = TextView(this@ProgressReportActivity).apply {
                    text = "${goal.category} - Spent: R${"%.2f".format(totalSpent)}"
                    textSize = 18f
                    setTextColor(Color.DKGRAY)
                }

                // Progress bar
                val progressBar = ProgressBar(this@ProgressReportActivity, null, android.R.attr.progressBarStyleHorizontal).apply {
                    max = goal.maxAmount.toInt()
                    progress = totalSpent.toInt()
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 8, 0, 8)
                    }

                    progressDrawable.setTint(
                        when {
                            totalSpent < goal.minAmount -> Color.BLUE
                            totalSpent > goal.maxAmount -> Color.RED
                            else -> Color.GREEN
                        }
                    )
                }

                // Percentage text
                val percent = ((totalSpent / goal.maxAmount) * 100).coerceAtMost(100.0)
                val percentView = TextView(this@ProgressReportActivity).apply {
                    text = "Progress: ${"%.0f".format(percent)}%"
                    textSize = 14f
                    setTextColor(Color.DKGRAY)
                }

                dashboard.addView(titleView)
                dashboard.addView(progressBar)
                dashboard.addView(percentView)
            }

            // --------------- Badges ------------------
            val badgeContainer = binding.badgeContainer
            badgeContainer.removeAllViews()

            var withinAllGoals = true
            var totalBudget = 0.0
            var totalSpent = 0.0
            val daysLogged = expenses.map { it.date }.distinct().size

            goals.forEach { goal ->
                val spent = expenses.filter { it.category == goal.category }.sumOf { it.amount }
                if (spent > goal.maxAmount || spent < goal.minAmount) {
                    withinAllGoals = false
                }
                totalBudget += goal.maxAmount
                totalSpent += spent
            }

            if (withinAllGoals) {
                addBadge("🎯 Goal Smasher", "You stayed within all your category budgets!", badgeContainer)
            }

            if (daysLogged >= 5) {
                addBadge("🔁 Consistent Logger", "You've logged expenses for 5+ days!", badgeContainer)
            }

            if (totalSpent < totalBudget * 0.5) {
                addBadge("💰 Big Saver", "You've spent less than 50% of your total budget!", badgeContainer)
            }
        }
    }

    private fun addBadge(title: String, desc: String, container: LinearLayout) {
        val badgeText = TextView(this).apply {
            text = "$title - $desc"
            textSize = 16f
            setTextColor(Color.parseColor("#444444"))
            setPadding(8, 8, 8, 16)
        }
        container.addView(badgeText)
    }
}
