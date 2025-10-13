package com.example.playlistmaker.ui.settings.compose

import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.compose.components.CommonToolbar
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsComposeScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val isThemeDarkState by viewModel.getThemeLiveData().observeAsState()

    Scaffold(
        topBar = { CommonToolbar(stringResource(R.string.btn_settings)) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .padding(innerPadding)
                .padding(start = 8.dp, end = 6.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            SettingsSwitchItem(
                title = stringResource(R.string.btn_dark_theme),
                isChecked = isThemeDarkState ?: false,
                onClick = { isDarkThemeOn -> viewModel.switchTheme(isDarkThemeOn) }
            )

            val shareLinq = stringResource(R.string.curs_uri)
            SettingsRowItem(
                title = stringResource(R.string.btn_share),
                iconId = R.drawable.share
            ) {
                viewModel.shareLink(shareLinq)
            }

            val subject = stringResource(R.string.support_subject)
            val message = stringResource(R.string.support_message)
            val mails = arrayOf(stringResource(R.string.support_email))
            SettingsRowItem(
                title = stringResource(R.string.btn_support),
                iconId = R.drawable.support
            ) {
                viewModel.sendEmail(subject, message, mails)
            }

            val userAgreementLink = stringResource(R.string.offer_uri)
            SettingsRowItem(
                title = stringResource(R.string.btn_user_agreement),
                iconId = R.drawable.arrow_forward
            ) {
                viewModel.openLink(userAgreementLink)
            }
        }
    }
}

@Composable
fun SettingsSwitchItem(title: String, isChecked: Boolean, onClick: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 6.dp)
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Switch(
            checked = isChecked,
            onCheckedChange = onClick,
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = colorResource(R.color.switch_thumb_off),
                uncheckedTrackColor = colorResource(R.color.switch_track_off),
                checkedThumbColor = colorResource(R.color.switch_thumb_on),
                checkedTrackColor = colorResource(R.color.switch_track_on)
            )
        )
    }
}

@Composable
fun SettingsRowItem(title: String, iconId: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 6.dp)
            .height(60.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Icon(
            painter = painterResource(iconId),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onTertiary
        )
    }
}