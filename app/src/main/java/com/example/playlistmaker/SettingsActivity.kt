package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
           finish()
       }
        val switchDarkTheme = findViewById<SwitchMaterial>(R.id.switch_dark_theme)
        switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->

        }
        findViewById<LinearLayout>(R.id.item_share_app).setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "Ссылка на приложение: https://example.com")
        startActivity(Intent.createChooser(intent, "Поделиться через"))
        }
        findViewById<LinearLayout>(R.id.item_write_to_support).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
              data = Uri.parse("maito:support@example.com")
              putExtra(Intent.EXTRA_SUBJECT, "Вопрос по приложению")
            }
            if (intent.resolveActivity(packageManager) !=null) {
                startActivity(intent)
            }
                    }
    }
}