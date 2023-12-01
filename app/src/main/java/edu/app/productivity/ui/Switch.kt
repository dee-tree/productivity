package edu.app.productivity.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.app.productivity.theme.ProductivityTheme

@Composable
fun Switch(label: String, state: Boolean, onStateChange: (Boolean) -> Unit = {}) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .selectable(
                selected = state,
                interactionSource = interactionSource,
                // This is for removing ripple when Row is clicked
                indication = null,
                role = Role.Switch,
                onClick = { onStateChange(!state) }
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, textAlign = TextAlign.Start)
        Spacer(modifier = Modifier.padding(start = 8.dp))
        androidx.compose.material3.Switch(
            checked = state,
            onCheckedChange = {
                onStateChange(it)
            }
        )
    }
}

@Preview
@Composable
private fun SwitchPreview() {
    ProductivityTheme {
        Surface {
            Column {
                Switch(label = "Switch on", state = true)
                Spacer(modifier = Modifier.padding(16.dp))
                Switch(label = "Switch off", state = false)

            }
        }
    }
}