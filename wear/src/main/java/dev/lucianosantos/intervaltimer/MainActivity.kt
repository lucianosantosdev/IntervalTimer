package dev.lucianosantos.intervaltimer

import WearAppTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Picker
import androidx.wear.compose.material.PickerGroup
import androidx.wear.compose.material.PickerGroupItem
import androidx.wear.compose.material.PickerState
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberPickerGroupState
import androidx.wear.compose.material.rememberPickerState
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import com.google.android.horologist.composables.TimePicker
import dev.lucianosantos.intervaltimer.core.ui.PickerType
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setTheme(R.style.Theme_App)

        setContent {
                MainApp()
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberSwipeDismissableNavController()
    WearAppTheme {
        WearNavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@WearPreviewLargeRound
@Composable
fun MainAppPreview() {
    MainApp()
}
