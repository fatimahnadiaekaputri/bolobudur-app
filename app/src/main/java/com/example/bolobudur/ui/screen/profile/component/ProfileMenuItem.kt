package com.example.bolobudur.ui.screen.profile.component

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    showDivider: Boolean = true,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    indication = LocalIndication.current, // tambahkan ini!
                    interactionSource = remember { MutableInteractionSource() }
                ) { onClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = textColor)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, style = MaterialTheme.typography.bodyLarge, color = textColor)
            Spacer(modifier = Modifier.weight(1f))
            Text(">", color = textColor) // tanda panah
        }
        if (showDivider) {
            Divider(modifier = Modifier.padding(start = 56.dp))
        }
    }
}
