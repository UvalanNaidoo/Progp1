package com.example.moneymappart1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.moneymappart1.databinding.ActivitySetGoalsBinding
import kotlinx.coroutines.launch

class SetGoalsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetGoalsBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetGoalsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        binding.btnSaveGoals.setOnClickListener {
            val minGoalStr = binding.etMinGoal.text.toString().trim()
            val maxGoalStr = binding.etMaxGoal.text.toString().trim()

            if (minGoalStr.isEmpty() || maxGoalStr.isEmpty()) {
                Toast.makeText(this, "Please enter both minimum and maximum goals", Toast.LENGTH_SHORT).show()
            } else {
                val minGoal = minGoalStr.toDoubleOrNull()
                val maxGoal = maxGoalStr.toDoubleOrNull()

                if (minGoal == null || maxGoal == null) {
                    Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
                } else {
                    lifecycleScope.launch {
                        val goal = GoalEntity(minGoal = minGoal, maxGoal = maxGoal)
                        db.goalDao().insertGoal(goal)

                        runOnUiThread {
                            Toast.makeText(this@SetGoalsActivity, "Goals Saved Successfully!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
        }
    }
}
