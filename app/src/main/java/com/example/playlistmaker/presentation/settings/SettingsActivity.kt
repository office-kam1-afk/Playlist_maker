package com.example.playlistmaker.presentation.settings

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val factory = (application as App).viewModelFactory
        viewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]

        val switchTheme = findViewById<SwitchMaterial>(R.id.switch_dark_theme)
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        val itemShareApp = findViewById<LinearLayout>(R.id.item_share_app)
        val itemWriteToSupport = findViewById<LinearLayout>(R.id.item_write_to_support)
        val itemUserAgreement = findViewById<LinearLayout>(R.id.item_user_agreement)

        viewModel.screenState.observe(this) { state ->
            switchTheme.isChecked = state.isDarkTheme
        }

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeSwitched(isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        btnBack.setOnClickListener { finish() }

        itemShareApp.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=${packageName}")
            }
            startActivity(Intent.createChooser(shareIntent, "Поделиться приложением"))
        }

        itemWriteToSupport.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:".toUri()
                putExtra(Intent.EXTRA_EMAIL, arrayOf("support@playlistmaker.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Вопрос по приложению Playlist Maker")
            }
            startActivity(Intent.createChooser(emailIntent, "Написать в поддержку"))
        }

        itemUserAgreement.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, getString(R.string.terms_url).toUri()))
        }
    }
}