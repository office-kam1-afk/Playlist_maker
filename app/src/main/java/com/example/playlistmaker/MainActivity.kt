package com.example.playlistmaker

import android.content.Intent


import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<MaterialButton>(R.id.btn_search).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btn_library).setOnClickListener {
            startActivity(Intent(this, LibraryActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btn_settings).setOnClickListener {
        startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}