package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

import com.google.android.material.switchmaterial.SwitchMaterial
import androidx.core.content.edit
import androidx.core.net.toUri

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: android.content.SharedPreferences
    private lateinit var switchDarkTheme: SwitchMaterial

    companion object {
        private const val PREFS_NAME = "app_settings"
        private const val KEY_DARK_THEME = "dark_theme_enabled"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)


        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        switchDarkTheme = findViewById(R.id.switch_dark_theme)
        val isDarkThemeEnabled = sharedPreferences.getBoolean(KEY_DARK_THEME, false)
        switchDarkTheme.isChecked = isDarkThemeEnabled

        switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit { putBoolean(KEY_DARK_THEME, isChecked) }
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            recreate()
        }


        findViewById<LinearLayout>(R.id.item_share_app).setOnClickListener { shareApp() }
        findViewById<LinearLayout>(R.id.item_write_to_support).setOnClickListener { writeToSupport() }
        findViewById<LinearLayout>(R.id.item_user_agreement).setOnClickListener { openUserAgreement() }


    }
    private fun shareApp() {
        val shareMessage = getString(R.string.share_message)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, shareMessage)
            type = "text/plain"
        }
        val chooserTitle = getString(R.string.share_app_label)
        startActivity(Intent.createChooser(shareIntent, chooserTitle))
    }

    private fun writeToSupport() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.support_body))
        }
        val chooserTitle = getString(R.string.support_label)
        startActivity(Intent.createChooser(emailIntent, chooserTitle))
    }

    private fun openUserAgreement() {
        val termsUrl = getString(R.string.terms_url)
        val browserIntent = Intent(Intent.ACTION_VIEW, termsUrl.toUri())
        val chooserTitle = getString(R.string.terms_label)
        startActivity(Intent.createChooser(browserIntent, chooserTitle))
    }
}