package com.example.go4lunch24kotlin.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.go4lunch24kotlin.R
import com.example.go4lunch24kotlin.databinding.SettingLayoutBinding
import com.example.go4lunch24kotlin.factory.Go4LunchFactory
import com.example.go4lunch24kotlin.util.PermissionsAction
import com.example.go4lunch24kotlin.viewModel.SettingViewModel


class SettingActivity : AppCompatActivity() {

    private lateinit var binding: SettingLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        manageViewModel()

      //  binding.settingsToolbar.setOnClickListener { view -> onBackPressed() }
    }

    private fun manageViewModel() {
        val viewModelFactory = Go4LunchFactory.instance
        val viewModel = ViewModelProvider(this, viewModelFactory!!)[SettingViewModel::class.java]

        viewModel.getSwitchPosition.observe(this) { switchPosition ->

            when (switchPosition) {
                1 -> binding.notificationSwitch.isChecked = false
                2 -> binding.notificationSwitch.isChecked = true
            }
        }

        viewModel.actionSingleLiveEvent.observe(this) { permission ->
            when(permission){
                PermissionsAction.NOTIFICATION_DISABLED -> Toast.makeText(this, getString(R.string.notification_disabled),
                    Toast.LENGTH_SHORT)
                    .show()
                PermissionsAction.NOTIFICATION_ENABLED -> Toast.makeText(this, getString(R.string.notifications_enabled),
                    Toast.LENGTH_SHORT)
                    .show()
                else -> {}
            }
        }

        binding.notificationSwitch.setOnClickListener { viewModel.notificationChange() }
    }
}