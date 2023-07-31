package dev.lucianosantos.intervaltimer.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

@Composable
fun ActionButton(
    icon : Int,
    contentDescription : Int,
    onClick : () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = stringResource(id = contentDescription)
        )
    }
}
