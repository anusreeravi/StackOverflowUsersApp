package com.candyspace.stackoverflowusers.feature.userprofile.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.candyspace.stackoverflowusers.designsystem.StackAppTheme
import com.candyspace.stackoverflowusers.domain.model.BadgeCounts
import com.candyspace.stackoverflowusers.domain.model.TopTag
import com.candyspace.stackoverflowusers.domain.model.User
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class UserProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun userDetailScreen_loadingState_showsTitleAndLoading() {
        composeTestRule.setContent {
            StackAppTheme {
                UserProfileScreen(
                    uiState = UserProfileUiState.Loading,
                )
            }
        }

        composeTestRule.onNodeWithText("User Profile").assertIsDisplayed()
    }

    @Test
    fun userDetailScreen_errorState_showsErrorMessage() {
        composeTestRule.setContent {
            StackAppTheme {
                UserProfileScreen(
                    uiState = UserProfileUiState.Error(
                        message = "Failed to load user detail",
                    ),
                )
            }
        }

        composeTestRule.onNodeWithText("Error").assertIsDisplayed()
        composeTestRule.onNodeWithText("Failed to load user detail").assertIsDisplayed()
    }

    @Test
    fun userDetailScreen_displaysUserInformationAndTopTags() {
        var backClicked = false

        val testUser = User(
            id = 42L,
            displayName = "Alex Architecture",
            reputation = 9999,
            profileImage = null,
            location = "Tokyo, Japan",
            creationDate = 1538654400L,
            badgeCounts = BadgeCounts(gold = 5, silver = 15, bronze = 25),
        )

        val testTags = listOf(
            TopTag("compose", answerCount = 42, score = 120),
            TopTag("hilt", answerCount = 18, score = 80),
        )

        composeTestRule.setContent {
            StackAppTheme {
                UserProfileScreen(
                    uiState = UserProfileUiState.Success(
                        user = testUser,
                        topTags = testTags,
                    ),
                    onEvent = { event ->
                        if (event is UserProfileEvent.BackClicked) {
                            backClicked = true
                        }
                    },
                )
            }
        }

        composeTestRule.onNodeWithText("User Profile").assertIsDisplayed()
        composeTestRule.onNodeWithText("Alex Architecture").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reputation: 9999").assertIsDisplayed()
        composeTestRule.onNodeWithText("Top Tags").assertIsDisplayed()
        composeTestRule.onNodeWithText("compose (42)").assertIsDisplayed()
        composeTestRule.onNodeWithText("hilt (18)").assertIsDisplayed()
        composeTestRule.onNodeWithText("5 Gold").assertIsDisplayed()
        composeTestRule.onNodeWithText("15 Silver").assertIsDisplayed()
        composeTestRule.onNodeWithText("25 Bronze").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tokyo, Japan").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Navigate back").performClick()
        assertTrue(backClicked)
    }
}
