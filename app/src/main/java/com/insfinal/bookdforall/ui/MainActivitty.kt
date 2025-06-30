package com.insfinal.bookdforall.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.insfinal.bookdforall.R
import com.insfinal.bookdforall.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, HomeFragment())
            .commit()

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.fragmentContainer.id, HomeFragment())
                        .commit()
                    true
                }
                R.id.nav_Collection -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.fragmentContainer.id, CollectionFragment())
                        .commit()
                    true
                }
                R.id.nav_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.fragmentContainer.id, ProfileFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}