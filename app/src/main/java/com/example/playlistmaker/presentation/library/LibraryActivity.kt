package com.example.playlistmaker.presentation.library

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.search.SearchActivity
import com.example.playlistmaker.presentation.settings.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class LibraryActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        initViews()
        setupBackButton()
        setupViewPagerAndTabs()
        //setupBottomNavigation()
    }

    private fun initViews() {
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
    }

    private fun setupBackButton() {
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }
    }

    private fun setupViewPagerAndTabs() {

        val adapter = LibraryPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout,viewPager) { tab, position ->
            tab.text = when (position) {
                1 -> "Плейлисты"
                0 -> "Избранные треки"
                else -> ""
            }
        }.attach()
    }

    //private fun setupBottomNavigation() {
     //   val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
       // bottomNavigation.selectedItemId = R.id.navigation_library

        //bottomNavigation.setOnItemSelectedListener { item ->
         //   when (item.itemId) {
           //     R.id.navigation_search -> {
             //       startActivity(Intent(this, SearchActivity::class.java))
               //     true
     //           }
       ///         R.id.navigation_settings -> {
          //          startActivity(Intent(this, SettingsActivity::class.java))
            ///        true
              ///  }
                //R.id.navigation_library -> true
            //    else -> false
           // }
      //  }
   // }
}