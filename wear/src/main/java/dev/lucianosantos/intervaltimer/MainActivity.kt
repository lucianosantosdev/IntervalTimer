package dev.lucianosantos.intervaltimer

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import dev.lucianosantos.intervaltimer.databinding.ActivityMainBinding
import dev.lucianosantos.intervaltimer.R

class MainActivity : FragmentActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_App)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}