@file:OptIn(ExperimentalMaterial3Api::class)

package edu.app.productivity.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissState
import androidx.compose.material3.SwipeToDismissValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.app.productivity.R
import edu.app.productivity.data.db.ActionsTemplateEntity
import edu.app.productivity.domain.Action
import edu.app.productivity.theme.ProductivityTheme
import edu.app.productivity.ui.timer.ActionsSetupBottomSheet
import kotlinx.coroutines.launch
import java.util.Objects
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionTemplates(
    templates: List<ActionsTemplateEntity> = emptyList(),
    isTemplateNameValid: (name: String) -> Boolean = { it.isNotBlank() },
    onTemplateAdd: (name: String, actions: List<Action>) -> Unit = { _: String, _: List<Action> -> },
    onTemplateDeleted: (name: String, actions: List<Action>) -> Unit = { _: String, _: List<Action> -> },
    onTemplateSelected: (name: String, actions: List<Action>) -> Unit = { _: String, _: List<Action> -> }
) {
    var showTemplatesSetupBottomSheet by remember { mutableStateOf(false) }
    var setupTemplateName by remember { mutableStateOf(false) }
    var currentTemplateName by remember { mutableStateOf("") }
    val templatesSetupSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .heightIn(32.dp, 256.dp)
    ) {
        Text(
            style = MaterialTheme.typography.headlineMedium,
            text = stringResource(R.string.action_templates_header)
        )

        AnimatedVisibility(
            visible = !showTemplatesSetupBottomSheet && !setupTemplateName,
            label = "add new template form button"
        ) {

            // TextButton due to fill width
            TextButton(
                onClick = { currentTemplateName = ""; setupTemplateName = true }
            ) {
                Icon(
                    Icons.Rounded.Add,
                    "create new template",
                    modifier = Modifier.fillMaxWidth(0.65f)
                )
            }
        }

        AnimatedVisibility(
            visible = showTemplatesSetupBottomSheet || setupTemplateName,
            label = "close template form button"
        ) {

            // TextButton due to fill width
            TextButton(
                onClick = {
                    setupTemplateName = false
                    showTemplatesSetupBottomSheet = false
                    currentTemplateName = ""
                }
            ) {
                Icon(
                    Icons.Rounded.Delete,
                    "close template creation menu",
                    modifier = Modifier.fillMaxWidth(0.65f),
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.9f)
                )
            }
        }

        AnimatedVisibility(visible = setupTemplateName, label = "template name scope") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    value = currentTemplateName,
                    onValueChange = { currentTemplateName = it },
                    isError = !isTemplateNameValid(currentTemplateName),
                    label = { Text(stringResource(R.string.action_template_name_field)) },
                )

                TextButton(
                    onClick = { showTemplatesSetupBottomSheet = true; setupTemplateName = false },
                    enabled = isTemplateNameValid(currentTemplateName)
                ) {
                    Text(stringResource(R.string.action_template_name_set))

                }
            }
        }

        AnimatedVisibility(
            visible = !showTemplatesSetupBottomSheet && !setupTemplateName,
            label = "templates column"
        ) {
            TemplatesColumn(
                templates = templates,
                onTemplateDeleted = onTemplateDeleted,
                onTemplateSelected = onTemplateSelected
            )
        }
    }

    if (showTemplatesSetupBottomSheet) {
        ActionsSetupBottomSheet(
            sheetState = templatesSetupSheetState,
            onDismiss = { showTemplatesSetupBottomSheet = false },
            onSelected = { actions ->
                scope.launch { templatesSetupSheetState.hide() }.invokeOnCompletion {
                    if (!templatesSetupSheetState.isVisible) {
                        showTemplatesSetupBottomSheet = false
                        onTemplateAdd(currentTemplateName, actions)
                    }
                }
            }
        )
    }
}

@Composable
private fun TemplatesColumn(
    templates: List<ActionsTemplateEntity>,
    onTemplateDeleted: (name: String, actions: List<Action>) -> Unit = { _: String, _: List<Action> -> },
    onTemplateSelected: (name: String, actions: List<Action>) -> Unit = { _: String, _: List<Action> -> }
) {
    val scope = rememberCoroutineScope()
    LazyColumn(
        reverseLayout = true,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .heightIn(128.dp, 256.dp)
            .fillMaxWidth()
    ) {
        items(templates.size) { idx ->
            val dismissState = rememberSwipeToDismissState(
                positionalThreshold = { it / 3 }
            )

            LaunchedEffect(dismissState.currentValue) {
                when (dismissState.currentValue) {
                    SwipeToDismissValue.StartToEnd -> scope.launch {
                        val templateToUse = templates[idx]
                        onTemplateSelected(templateToUse.name, templateToUse.actions)
                        dismissState.snapTo(SwipeToDismissValue.Settled)
                    }

                    SwipeToDismissValue.EndToStart -> scope.launch {
                        val templateToDelete = templates[idx]
                        onTemplateDeleted(templateToDelete.name, templateToDelete.actions)
                        dismissState.snapTo(SwipeToDismissValue.Settled)
                    }

                    else -> {}
                }
            }

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = { ListItemDismissDeleteUseBackground(dismissState) }) {
                ActionTemplateCard(
                    name = templates[idx].name,
                    actions = templates[idx].actions
                )
            }
        }
    }
}

@Composable
private fun ListItemDismissDeleteUseBackground(
    dismissState: SwipeToDismissState
) {
    val color by animateColorAsState(
        when (dismissState.targetValue) {
            SwipeToDismissValue.Settled -> MaterialTheme.colorScheme.background
            SwipeToDismissValue.EndToStart -> MaterialTheme.colorScheme.error
            SwipeToDismissValue.StartToEnd -> MaterialTheme.colorScheme.primary
        },
        label = "color animation"
    )

    val scale by animateFloatAsState(
        if (dismissState.targetValue == SwipeToDismissValue.Settled) 1f else 1.5f,
        label = "icon scale animation"
    )

    val iconAlignment = when (dismissState.targetValue) {
        SwipeToDismissValue.StartToEnd -> Alignment.CenterStart
        SwipeToDismissValue.EndToStart -> Alignment.CenterEnd
        else -> Alignment.Center
    }

    val icon = if (dismissState.targetValue == SwipeToDismissValue.StartToEnd)
        Icons.Rounded.Check else Icons.Rounded.Delete

    Box(
        Modifier
            .fillMaxSize()
            .background(color, CardDefaults.shape)
            .padding(horizontal = 20.dp),
        contentAlignment = iconAlignment
    ) {
        Icon(
            icon,
            contentDescription = "swipe action icon",
            modifier = Modifier.scale(scale)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActionTemplateCard(name: String, actions: List<Action>) {
    Card(
        colors = CardDefaults.outlinedCardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            val listState = rememberLazyListState()
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                reverseLayout = true,
                state = listState,
                flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
            ) {
                items(
                    actions.size,
                    key = { idx: Int -> Objects.hash(actions[idx].hashCode(), idx) }
                ) { idx ->
                    ActionCard(actions[idx], idx + 1)
                }
            }
            Spacer(Modifier.padding(vertical = 4.dp))
            Text(name, style = MaterialTheme.typography.titleMedium)
        }
    }
}


private val previewTemplate = ActionsTemplateEntity(
    "My training",
    listOf(
        Action.Work(30.minutes, "Hiking"),
        Action.Rest(15.minutes),
        Action.Work(45.minutes, "Swimming"),
        Action.Rest(15.minutes),
    )
)

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewActionTemplateCardLight() {
    ProductivityTheme(darkTheme = false) {
        Surface {
            ActionTemplateCard(
                previewTemplate.name,
                previewTemplate.actions
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewActionTemplateCardDark() {
    ProductivityTheme(darkTheme = true) {
        Surface {
            ActionTemplateCard(
                previewTemplate.name,
                previewTemplate.actions
            )
        }
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

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewActionTemplatesDark() {
    ProductivityTheme(darkTheme = true) {
        Surface {
            ActionTemplates()
        }
    }
}
