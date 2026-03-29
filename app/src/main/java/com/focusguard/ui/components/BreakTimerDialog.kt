package com.focusguard.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.focusguard.ui.theme.*

/**
 * Dialog presenting break time options: 5, 10, 15, 30 minutes.
 * Selecting a value calls onTakeBreak and dims blocking for that duration.
 */
@Composable
fun BreakTimerDialog(
    onDismissRequest: () -> Unit,
    onTakeBreak: (Int) -> Unit
) {
    val options = listOf(5, 10, 15, 30)

    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                "Take a Break ☕",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "How long would you like to pause blocking?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                options.forEach { minutes ->
                    OutlinedButton(
                        onClick = { onTakeBreak(minutes) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryPurple)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("$minutes minutes", fontWeight = FontWeight.Medium)
                            Text(
                                when (minutes) {
                                    5  -> "Quick break"
                                    10 -> "Short break"
                                    15 -> "Medium break"
                                    else -> "Long break"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
