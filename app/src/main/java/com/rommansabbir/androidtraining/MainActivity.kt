package com.rommansabbir.androidtraining

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.rommansabbir.androidtraining.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainVM
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainVM::class.java)

        viewModel.isLoading.observe(this, {
            it?.let {
                binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
            }
        })

        viewModel.hasError.observe(this, {
            it?.let {
                showToast(it.message.toString())
            }
        })

        viewModel.checkEmail("mark@mark.com") {
            viewModel.checkPassword("123456789") {
                showToast("You are good. Very good. Good Good Good.")
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}