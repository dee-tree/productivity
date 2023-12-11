package edu.app.productivity.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.app.productivity.R
import edu.app.productivity.domain.Action
import edu.app.productivity.theme.ProductivityTheme
import edu.app.productivity.ui.timer.ActionsSetupBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionTemplates(
    onTemplateAdd: (List<Action>) -> Unit = {},
) {

    var showTemplatesSetupBottomSheet by remember { mutableStateOf(false) }
    val templatesSetupSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .heightIn(32.dp, 256.dp)
    ) {
        Text(
            style = MaterialTheme.typography.titleSmall,
            text = stringResource(R.string.action_templates_header)
        )
        // TextButton due to fill width
        TextButton(
            onClick = { showTemplatesSetupBottomSheet = true }
        ) {
            Icon(
                Icons.Rounded.Add,
                "create new template",
                modifier = Modifier.fillMaxWidth(0.65f)
            )
        }
    }

    if (showTemplatesSetupBottomSheet) {
        ActionsSetupBottomSheet(
            sheetState = templatesSetupSheetState,
            onDismiss = { showTemplatesSetupBottomSheet = false },
            onSelected = { newAction ->
                scope.launch { templatesSetupSheetState.hide() }.invokeOnCompletion {
                    if (!templatesSetupSheetState.isVisible) {
                        showTemplatesSetupBottomSheet = false
                        onTemplateAdd(newAction)
                    }
                }
            }
        )
    }

}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewActionTemplatesLight() {
    ProductivityTheme(darkTheme = false) {
        Surface {
            ActionTemplates()
        }
    }
}
