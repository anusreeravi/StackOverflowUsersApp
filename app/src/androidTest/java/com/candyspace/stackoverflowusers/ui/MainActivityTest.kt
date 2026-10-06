package com.candyspace.stackoverflowusers.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createEmptyComposeRule()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun mainActivity_launchesAndDisplaysUserSearchScreen() {
        ActivityScenario.launch(MainActivity::class.java).use {
            composeTestRule.onNodeWithText("StackOverflow Users").assertIsDisplayed()
            composeTestRule.onNodeWithText("Search by name...").assertIsDisplayed()
            composeTestRule.onNodeWithText("Search").assertIsDisplayed()
        }
    }
}
