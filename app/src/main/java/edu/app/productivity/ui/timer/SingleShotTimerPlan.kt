@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)

package edu.app.productivity.ui.timer

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.app.productivity.R
import edu.app.productivity.domain.Action
import edu.app.productivity.theme.ProductivityTheme
import kotlinx.coroutines.launch


@Composable
fun SingleShotTimerPlanSheet(
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismiss: () -> Unit = {},
    onSelected: (Action) -> Unit = {}
) {
    ModalBottomSheet(sheetState = sheetState, onDismissRequest = onDismiss) {
        SingleShotTimerPlanSheetContent(onPlanSelected = onSelected)
    }
}

@Composable
fun SingleShotTimerPlanSheetContent(
    onPlanSelected: (Action) -> Unit = {}
) {
    val timePickerState = rememberTimePickerState(initialMinute = 30, is24Hour = true)
    var activityName by rememberSaveable { mutableStateOf("") }

    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp)
    ) {
        Text(
            text = stringResource(R.string.single_shot_timer_plan_setup_header),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.padding(vertical = 16.dp))

        DurationPicker(
            state = timePickerState
        )

        Spacer(modifier = Modifier.padding(vertical = 16.dp))
        TextField(
            value = activityName,
            onValueChange = { activityName = it },
            label = { Text(text = stringResource(R.string.timer_setup_activity_type)) },
            singleLine = true,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = Modifier.onFocusEvent { focusState ->
                if (focusState.isFocused) {
                    coroutineScope.launch {
                        bringIntoViewRequester.bringIntoView()
                    }
                }
            }
        )
        Spacer(modifier = Modifier.padding(vertical = 16.dp))

        TextButton(
            onClick = { onPlanSelected(Action.Work(timePickerState.duration, activityName)) },
            modifier = Modifier.bringIntoViewRequester(bringIntoViewRequester)
        ) {
            Text(text = stringResource(R.string.timer_setup_confirm))
            Spacer(modifier = Modifier.padding(horizontal = 6.dp))
            Icon(Icons.Rounded.PlayArrow, contentDescription = null)
        }

    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@OptIn(ExperimentalMaterial3Api::class)
fun PreviewSingleShotTimerPlanSheetLight() {
    ProductivityTheme {
        Surface {
            SingleShotTimerPlanSheetContent()
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@OptIn(ExperimentalMaterial3Api::class)
fun PreviewSingleShotTimerPlanSheetDark() {
    ProductivityTheme(darkTheme = true) {
        Surface {
            SingleShotTimerPlanSheetContent()
        }
    }
}
