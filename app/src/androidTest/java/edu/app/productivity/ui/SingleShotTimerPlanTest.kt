package edu.app.productivity.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyPress
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import edu.app.productivity.R
import edu.app.productivity.domain.Action
import edu.app.productivity.theme.ProductivityTheme
import edu.app.productivity.ui.timer.ActionsSetupBottomSheetContent
import junit.framework.TestCase.assertNotNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class SingleShotTimerPlanTest {
    @get:Rule
    val composeRuleTest = createComposeRule()

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun singleShotQuickTestDial() {
        var plan: List<Action>? = null

        var activityNameText = ""
        var createPlanText = ""
        var defaultActivityName = ""

        composeRuleTest.setContent {
            ProductivityTheme {
                activityNameText = stringResource(R.string.timer_setup_activity_type)
                createPlanText = stringResource(R.string.timer_setup_confirm)
                defaultActivityName = stringResource(R.string.action_work_default_activity)

                var actions by remember { mutableStateOf(emptyList<Action>()) }

                ActionsSetupBottomSheetContent(
                    actions = actions,
                    onActionsChange = { actions = it },
                    timerSetupIsDial = true,
                    onPlanSelected = { plan = actions }
                )
            }
        }

        val hours = (0..8).random()
        val minutes = (5..55 step 5).toList().random()
        val activityName = listOf("Work", "Edu", "reading", "", "Riding bike").random()

        composeRuleTest.onNode(hasText(activityNameText))
            .performClick()
            .performTextInput(activityName)

        composeRuleTest.onNode(hasContentDescription("$hours hours") and hasAnyAncestor(hasTestTag("TimePicker")))
            .assertExists("there is no $hours hours selector")
            .assertIsDisplayed()
            .performClick()

        composeRuleTest.onNode(
            hasContentDescription("$minutes minutes") and hasAnyAncestor(
                hasTestTag("TimePicker")
            )
        )
            .assertExists("there is no $minutes minutes selector")
            .assertIsDisplayed()
            .performClick()

        composeRuleTest.onNode(hasText(createPlanText) and hasClickAction())
            .assertExists()
            .assertIsEnabled()
            .performClick()

        assertNotNull(plan)
        assertEquals(1, plan!!.size)

        (plan!!.single().also { assertTrue(it.isWork) } as Action.Work).also { action ->
            assertEquals(hours.hours + minutes.minutes, action.duration)
            if (action.activityName.isBlank()) {
                assertEquals(defaultActivityName, action.activityName)
            }

        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun singleShotQuickTestTextInput() {
        var plan: List<Action>? = null

        var activityNameText = ""
        var createPlanText = ""

        composeRuleTest.setContent {
            ProductivityTheme {
                activityNameText = stringResource(R.string.timer_setup_activity_type)
                createPlanText = stringResource(R.string.timer_setup_confirm)

                var actions by remember { mutableStateOf(emptyList<Action>()) }

                ActionsSetupBottomSheetContent(
                    actions = actions,
                    onActionsChange = { actions = it },
                    timerSetupIsDial = false,
                    onPlanSelected = { plan = actions }
                )
            }
        }

        // test 0:0 time is invalid to go next
        composeRuleTest.onNode(hasContentDescription("for hour") and hasAnyAncestor(hasTestTag("TimeInput")))
            .assertExists("there is no 0 hours selector")
            .performClick()
            .apply { performTextInput("0") }
            .performKeyPress(
                KeyEvent(
                    NativeKeyEvent(
                        NativeKeyEvent.ACTION_DOWN, NativeKeyEvent.KEYCODE_NAVIGATE_NEXT
                    )
                )
            )

        composeRuleTest.onNode(hasContentDescription("for minutes") and hasAnyAncestor(hasTestTag("TimeInput")))
            .assertExists("there is no 0 minutes selector")
            .performClick()
            .performTextInput("0")

        composeRuleTest.onNode(hasText(createPlanText) and hasClickAction())
            .assertExists()
            .assertIsNotEnabled()

        // test selector on random data

        val hours = (0..8).random()
        val minutes = (1..59).random()
        val activityName = listOf("Work", "Edu", "reading", "", "Riding bike").random()

        composeRuleTest.onNode(hasText(activityNameText))
            .performClick()
            .performTextInput(activityName)

        composeRuleTest.onNode(hasContentDescription("for hour") and hasAnyAncestor(hasTestTag("TimeInput")))
            .assertExists("there is no $hours hours selector")
            .performClick()
            .apply { performTextClearance() }
            .performTextInput(hours.toString())

        composeRuleTest.onNode(
            hasContentDescription("for minutes") and hasAnyAncestor(
                hasTestTag("TimeInput")
            )
        )
            .assertExists("there is no $minutes minutes selector")
            .performClick()
            .performTextInput(minutes.toString())

        composeRuleTest.onNode(hasText(createPlanText) and hasClickAction())
            .assertExists()
            .assertIsEnabled()
            .performClick()

        assertNotNull(plan)
        assertEquals(1, plan!!.size)

        (plan!!.single().also { assertTrue(it.isWork) } as Action.Work).also { action ->
            assertEquals(hours.hours + minutes.minutes, action.duration)
            assertEquals(activityName, action.activityName)
        }
    }
}