package edu.app.productivity.ui

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import edu.app.productivity.R
import edu.app.productivity.theme.ProductivityTheme
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test


class ForgetMeTest {

    @get:Rule
    val composeRuleTest = createComposeRule()

    @Test
    fun testForgetMe() {
        var forgetMeText = ""
        var confirmationTitle = ""
        var confirmationText = ""
        var declineText = ""
        var clearAllData = false

        composeRuleTest.setContent {
            ProductivityTheme {
                forgetMeText = stringResource(R.string.forget_me_confirmation_action)
                confirmationTitle = stringResource(R.string.forget_me_confirmation_title)
                confirmationText = stringResource(R.string.forget_me_confirmation_confirm)
                declineText = stringResource(R.string.forget_me_confirmation_decline)
                ForgetMeRow {
                    clearAllData = true
                }
            }
        }

        assertEquals(false, clearAllData)

        composeRuleTest.onNodeWithText(confirmationText).assertIsNotDisplayed()
        composeRuleTest.onNodeWithText(declineText).assertIsNotDisplayed()
        composeRuleTest.onNodeWithText(confirmationTitle).assertIsNotDisplayed()

        composeRuleTest.onNode(hasText(forgetMeText) and hasClickAction()).performClick()

        composeRuleTest.onNodeWithText(confirmationText).assertIsDisplayed()
        composeRuleTest.onNodeWithText(declineText).assertIsDisplayed()
        composeRuleTest.onNodeWithText(confirmationTitle).assertIsDisplayed()

        composeRuleTest.onNode(hasText(declineText) and hasClickAction()).performClick()

        composeRuleTest.onNodeWithText(confirmationText).assertIsNotDisplayed()
        composeRuleTest.onNodeWithText(declineText).assertIsNotDisplayed()
        composeRuleTest.onNodeWithText(confirmationTitle).assertIsNotDisplayed()

        assertEquals(false, clearAllData)

        composeRuleTest.onNode(hasText(forgetMeText) and hasClickAction()).performClick()

        composeRuleTest.onNode(hasText(confirmationText) and hasClickAction()).performClick()

        composeRuleTest.onNodeWithText(confirmationText).assertIsNotDisplayed()
        composeRuleTest.onNodeWithText(declineText).assertIsNotDisplayed()
        composeRuleTest.onNodeWithText(confirmationTitle).assertIsNotDisplayed()

        assertEquals(true, clearAllData)
    }
}