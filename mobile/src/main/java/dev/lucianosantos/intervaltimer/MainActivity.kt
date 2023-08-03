package dev.lucianosantos.intervaltimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import dev.lucianosantos.intervaltimer.R
import dev.lucianosantos.intervaltimer.theme.IntervalTimerTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        setContent {
            IntervalTimerTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()

    IntervalTimerNavHost(
        navController = navController,
        modifier = Modifier.fillMaxSize()
    )
}

@Preview(widthDp = 320, heightDp = 640)
@Composable
fun MainAppPreview() {
    MainApp()
}