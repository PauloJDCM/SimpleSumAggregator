package com.example.simplesumaggregator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simplesumaggregator.ui.theme.SimpleSumAggregatorTheme
import com.example.simplesumaggregator.viewmodels.SummaryViewModel
import com.example.simplesumaggregator.viewmodels.WorkspaceViewModel
import com.example.simplesumaggregator.views.SummaryView
import com.example.simplesumaggregator.views.WorkspaceView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleSumAggregatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    val navController = rememberNavController()
                    val entries = remember { mutableStateListOf<Entry>() }

                    NavHost(
                        navController = navController,
                        startDestination = Routes.WORKSPACE.name,
                        modifier = Modifier.padding(padding)
                    ) {
                        composable(Routes.WORKSPACE.name) {
                            WorkspaceView(
                                viewModel = viewModel { WorkspaceViewModel(entries) },
                                onSummaryClick = {
                                    navController.navigate(Routes.SUMMARY.name)
                                })
                        }
                        composable(
                            Routes.SUMMARY.name,
                            enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(350)
                                )
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(350)
                                )
                            }
                        ) {
                            SummaryView(
                                viewModel = viewModel { SummaryViewModel(entries) },
                                onBackClick = {
                                    navController.navigate(Routes.WORKSPACE.name)
                                })
                        }
                    }
                }
            }
        }
    }
}

