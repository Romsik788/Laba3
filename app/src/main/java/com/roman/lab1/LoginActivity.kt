package com.roman.lab1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.roman.lab1.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var intentPlayActivity: Intent

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intentPlayActivity = Intent(this, PlayActivity::class.java)

        binding.loginButton.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                binding.errorMessage.text = getString(R.string.fill_in_fields)
                return@setOnClickListener
            }
            attemptLogin(username, password)
        }
    }
    private fun attemptLogin(username: String, password: String) {
        lifecycleScope.launch {
            if (viewModel.isUserExist(username)) {
                if (viewModel.isPasswordCorrect(username, password)) {
                    createToast(getString(R.string.welcome_back, username))
                    intentPlayActivity.putExtra("USERNAME", username)
                    startActivity(intentPlayActivity)

                } else {
                    binding.errorMessage.text = getString(R.string.wrong_password)
                }
            } else {
                viewModel.addUser(username, password)
                createToast(getString(R.string.sign_up))
                intentPlayActivity.putExtra("USERNAME", username)
                startActivity(intentPlayActivity)
            }
        }
    }

    private fun createToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}