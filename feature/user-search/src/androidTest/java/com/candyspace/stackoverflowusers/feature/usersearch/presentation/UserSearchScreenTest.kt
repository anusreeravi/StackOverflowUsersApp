package com.candyspace.stackoverflowusers.feature.usersearch.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.candyspace.stackoverflowusers.designsystem.StackAppTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class UserSearchScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchBar_allowsTextInputAndTriggersSearchClick() {
        var queryInput = ""
        var searchClicked = false

        composeTestRule.setContent {
            StackAppTheme {
                UserSearchScreen(
                    uiState = UserSearchUiState.Success(users = emptyList()),
                    searchQuery = queryInput,
                    onEvent = { event ->
                        when (event) {
                            is UserSearchEvent.SearchQueryChanged -> queryInput = event.query
                            is UserSearchEvent.SearchClicked -> searchClicked = true
                            else -> {}
                        }
                    },
                )
            }
        }

        composeTestRule.onNodeWithText("Search by name...").performTextInput("Jon")
        assertEquals("Jon", queryInput)

        composeTestRule.onNodeWithText("Search").performClick()
        assertTrue(searchClicked)
    }

    @Test
    fun searchBar_clearButton_clearsQueryAndTriggersSearch() {
        var queryInput = "Jon"
        var searchClicked = false

        composeTestRule.setContent {
            StackAppTheme {
                UserSearchScreen(
                    uiState = UserSearchUiState.Success(users = emptyList()),
                    searchQuery = queryInput,
                    onEvent = { event ->
                        when (event) {
                            is UserSearchEvent.SearchQueryChanged -> queryInput = event.query
                            is UserSearchEvent.SearchClicked -> searchClicked = true
                            else -> {}
                        }
                    },
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Clear Search").performClick()
        assertEquals("", queryInput)
        assertTrue(searchClicked)
    }

    @Test
    fun emptyState_showsNoUsersFoundMessage() {
        composeTestRule.setContent {
            StackAppTheme {
                UserSearchScreen(
                    uiState = UserSearchUiState.Success(users = emptyList()),
                )
            }
        }

        composeTestRule.onNodeWithText("No users found.").assertIsDisplayed()
    }
}
