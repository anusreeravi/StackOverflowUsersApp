package com.candyspace.stackoverflowusers.ui

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.candyspace.stackoverflowusers.HiltTestActivity
import com.candyspace.stackoverflowusers.designsystem.StackAppTheme
import com.candyspace.stackoverflowusers.navigation.SearchUserRoute
import com.candyspace.stackoverflowusers.navigation.UserProfileRoute
import com.candyspace.stackoverflowusers.ui.navigation.AppNavHost
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class AppNavHostTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun appNavHost_verifyStartDestination_isSearchUserRoute() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())

            StackAppTheme {
                AppNavHost(
                    navController = navController,
                )
            }
        }

        assertTrue(navController.currentBackStackEntry?.destination?.hasRoute<SearchUserRoute>() == true)
        composeTestRule.onNodeWithText("StackOverflow Users").assertIsDisplayed()
    }

    @Test
    fun appNavHost_verifyNextDestination_isUserProfileRoute() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())

            StackAppTheme {
                AppNavHost(
                    navController = navController,
                )
            }
        }

        composeTestRule.runOnIdle {
            navController.navigate(UserProfileRoute(userId = "100"))
        }

        composeTestRule.waitForIdle()

        assertTrue(navController.currentBackStackEntry?.destination?.hasRoute<UserProfileRoute>() == true)
        composeTestRule.onNodeWithText("User Profile").assertIsDisplayed()
    }
}
