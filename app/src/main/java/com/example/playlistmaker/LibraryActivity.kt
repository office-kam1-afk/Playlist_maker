package com.example.playlistmaker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView

class LibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
    val tabSelected = findViewById<TextView>(R.id.tab_selected)
        val tabPlaylist = findViewById<TextView>(R.id.tab_playlist)
        tabSelected.setOnClickListener {

        }
    tabPlaylist.setOnClickListener {

    }
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.navigation_library
    bottomNavigation.setOnItemSelectedListener { item ->
        when (item.itemId){
            R.id.navigation_search -> {
               startActivity(Intent(this, SettingsActivity::class.java))
               overridePendingTransition(0,0)
               true
            }
            else -> false
        }
    }
    }
}