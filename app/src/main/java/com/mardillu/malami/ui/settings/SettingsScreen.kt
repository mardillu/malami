package com.mardillu.malami.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mardillu.malami.R


/**
 * Created on 24/06/2024 at 8:16 pm
 * @author mardillu settings -> update learning styles, notification prefs, logout ...
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true,)
fun SettingsScreen() {
    val settingsItems = listOf(
        SettingItem(title = "General Settings", isHeader = true),
        SettingItem(R.drawable.ic_learning, "Learning Style", "Update your learning style"),
        SettingItem(R.drawable.ic_notifications, "Notifications", "Reminders and more"),
        SettingItem(
            R.drawable.outline_security_24,
            "Privacy policy",
            "Learn more about our privacy policy"
        ),
        SettingItem(
            R.drawable.outline_policy_24,
            "Terms of service",
            "Learn more about our terms of service"
        ),
        SettingItem(
            R.drawable.outline_help_outline_24,
            "Help",
            "Help centre, contact us, and more"
        ),
        SettingItem(R.drawable.baseline_share_24, "Invite a friend"),
    )

    val dangerousSettings = listOf(
        SettingItem(title = "Dangerous area", isHeader = true),
        SettingItem(R.drawable.baseline_logout_24, "Logout"),
        SettingItem(R.drawable.outline_delete_forever_24, "Delete account")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                item {
                    UserProfileSection()
                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(settingsItems) { item ->
                    SettingsItem(item, false)
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }

                items(dangerousSettings) { item ->
                    SettingsItem(item, true)
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    )
}

@Composable
fun UserProfileSection() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = "ezkl", style = MaterialTheme.typography.titleLarge)
            Text(text = "+233 59 941 1666", style = MaterialTheme.typography.bodyMedium)
            Text(text = "mardillu.com", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun SettingsItem(item: SettingItem, dangerous: Boolean, onClick: () -> Unit = {}) {
    Row(
        modifier = if (item.isHeader) {
            Modifier
                .fillMaxWidth()
        } else {
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item.icon != null) {
                Icon(
                    painter = painterResource(id = item.icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (dangerous) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (dangerous) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
                if (item.subtitle != null) {
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

data class SettingItem(
    val icon: Int? = null,
    val title: String,
    val subtitle: String? = null,
    val isHeader: Boolean = false,
)
