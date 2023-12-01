package edu.app.productivity.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import edu.app.productivity.theme.ProductivityTheme
import org.junit.Rule
import org.junit.Test

class SwitchTest {
    @get:Rule
    val composeRuleTest = createComposeRule()

    @Test
    fun testSwitch() {
        val switchLabel = "fake switch"

        composeRuleTest.setContent {
            ProductivityTheme {
                var switchState by remember { mutableStateOf(false) }
                Switch(
                    label = switchLabel,
                    state = switchState,
                    onStateChange = { switchState = it })
            }
        }

        composeRuleTest.onNode(hasText(switchLabel) and hasClickAction()).assertIsNotSelected()

        composeRuleTest.onNodeWithText(switchLabel).performClick()
        composeRuleTest.onNodeWithText(switchLabel).performClick()
        composeRuleTest.onNode(hasText(switchLabel) and hasClickAction()).assertIsNotSelected()

        composeRuleTest.onNodeWithText(switchLabel).performClick()
        composeRuleTest.onNode(hasText(switchLabel) and hasClickAction()).assertIsSelected()

        composeRuleTest.onNodeWithText(switchLabel).performClick()
        composeRuleTest.onNodeWithText(switchLabel).performClick()
        composeRuleTest.onNode(hasText(switchLabel) and hasClickAction()).assertIsSelected()

        composeRuleTest.onNodeWithText(switchLabel).performClick()
        composeRuleTest.onNode(hasText(switchLabel) and hasClickAction()).assertIsNotSelected()
    }
}