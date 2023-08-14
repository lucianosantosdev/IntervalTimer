package dev.lucianosantos.intervaltimer.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.lucianosantos.intervaltimer.R
import dev.lucianosantos.intervaltimer.theme.IntervalTimerTheme

@Composable
fun ActionButton(
    icon : Int,
    contentDescription : Int,
    onClick : () -> Unit,
    modifier : Modifier = Modifier
) {
    FloatingActionButton(
        containerColor = MaterialTheme.colorScheme.primary,
        modifier = modifier,
        onClick = onClick,
        shape = CircleShape
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = stringResource(id = contentDescription)
        )
    }
}

@Preview(showBackground = true, heightDp = 200, widthDp = 200)
@Composable
fun ActionButtonPreview() {
    IntervalTimerTheme {
        Surface {
            Box {
                ActionButton(
                    modifier = Modifier.align(Alignment.Center),
                    icon = R.drawable.ic_baseline_play_arrow_24,
                    contentDescription = R.string.pause_content_description,
                    onClick = {

                    }
                )
            }
        }
    }
}
