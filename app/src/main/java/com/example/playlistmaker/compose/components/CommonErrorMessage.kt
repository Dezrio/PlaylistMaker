package com.example.playlistmaker.compose.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CommonErrorMessage(
    message: String,
    iconId: Int,
    paddingTop: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = paddingTop.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = painterResource(iconId),
            contentDescription = message,
            modifier = Modifier.size(120.dp)
        )

        Text(
            text = message,
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 24.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}