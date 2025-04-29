package com.example.moneymappart1

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.moneymappart1.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        binding.btnRegisterSubmit.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            } else if (!isPasswordValid(password)) {
                Toast.makeText(this, "Password must be at least 8 characters long and include uppercase, lowercase, number and symbol", Toast.LENGTH_LONG).show()
            } else {
                lifecycleScope.launch {
                    val existingUser = db.userDao().findUserByUsernameOrEmail(username, email)
                    if (existingUser != null) {
                        runOnUiThread {
                            Toast.makeText(this@RegisterActivity, "Username or Email already exists", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val newUser = UserEntity(username = username, email = email, password = password)
                        db.userDao().insertUser(newUser)
                        runOnUiThread {
                            Toast.makeText(this@RegisterActivity, "Registration Successful!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        val passwordPattern = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=!]).{8,}\$")
        return passwordPattern.matches(password)
    }
}
