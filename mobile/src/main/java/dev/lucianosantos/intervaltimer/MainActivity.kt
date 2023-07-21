package dev.lucianosantos.intervaltimer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.lucianosantos.intervaltimer.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        setContent {
//            MainApp()
//        }
    }
}

@Composable
fun MainApp() {
    Text(text ="Hello World!")
}