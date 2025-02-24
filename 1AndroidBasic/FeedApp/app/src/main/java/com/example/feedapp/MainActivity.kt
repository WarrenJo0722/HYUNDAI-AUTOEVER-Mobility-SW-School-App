package com.example.feedapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textViewTitle = findViewById<TextView>(R.id.textViewTitle)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // 초기 화면 설정
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, FeedFragment())
            .commit()

        // 네비게이션 아이템 클릭 리스너 설정
        bottomNavigation.setOnItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                R.id.nav_feed -> {
                    textViewTitle.text = "피드"
                    FeedFragment()
                }
                R.id.nav_profile -> {
                    textViewTitle.text = "프로필"
                    ProfileFragment()
                }
                else -> null
            }

            selectedFragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, it)
                    .commit()
            }

            true
        }
    }
}