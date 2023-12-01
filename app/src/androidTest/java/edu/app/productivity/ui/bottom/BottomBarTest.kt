package edu.app.productivity.ui.bottom

import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import edu.app.productivity.theme.ProductivityTheme
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class BottomBarTest {
    @get:Rule
    val composeRuleTest = createComposeRule()

    private enum class Category {
        Home, Preferences, Statistics
    }

    private fun bottomButton(category: Category) = composeRuleTest.onNode(
        hasContentDescription(category.name) and hasClickAction()
    )

    @Test
    fun testBottomBar() {
        var lastClicked: Category? = null

        composeRuleTest.setContent {
            ProductivityTheme {
                BottomBar(
                    onHomeCLick = { lastClicked = Category.Home },
                    onStatisticsClick = { lastClicked = Category.Statistics },
                    onPreferencesClick = { lastClicked = Category.Preferences }
                )
            }
        }

        bottomButton(Category.Home).assertExists()
        bottomButton(Category.Preferences).assertExists()
        bottomButton(Category.Statistics).assertExists()

        Category.values().forEach { category ->
            repeat(2) {
                bottomButton(category).performClick()
                assertEquals(category, lastClicked).also { lastClicked = null }
            }
        }
    }
}
