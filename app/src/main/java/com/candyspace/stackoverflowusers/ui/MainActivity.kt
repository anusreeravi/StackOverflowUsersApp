package com.candyspace.stackoverflowusers.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.candyspace.stackoverflowusers.designsystem.StackAppTheme
import com.candyspace.stackoverflowusers.feature.usersearch.presentation.UserSearchViewModel
import com.candyspace.stackoverflowusers.ui.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: UserSearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            viewModel.isLoading.value
        }

        setContent {
            StackAppTheme {
                val navController = rememberNavController()
                AppNavHost(
                    navController = navController,
                )
            }
        }
    }
}
