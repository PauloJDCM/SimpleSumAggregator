package com.example.simplesumaggregator

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simplesumaggregator.ui.theme.SimpleSumAggregatorTheme
import com.example.simplesumaggregator.viewmodels.HistoryViewModel
import com.example.simplesumaggregator.viewmodels.SummaryViewModel
import com.example.simplesumaggregator.viewmodels.WorkspaceViewModel
import com.example.simplesumaggregator.views.HistoryView
import com.example.simplesumaggregator.views.SummaryView
import com.example.simplesumaggregator.views.WorkspaceView
import java.io.File


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleSumAggregatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    val navController = rememberNavController()
                    val entries = remember { mutableStateListOf<Entry>() }
                    val entriesListState = remember { EntriesListState.NOT_SAVED }
                    val appFolder = getAppFolder(LocalContext.current)

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
                                },
                                onHistoryClick = {
                                    navController.navigate(Routes.HISTORY.name)
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
                        composable(
                            Routes.HISTORY.name,
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
                            HistoryView(
                                viewModel = viewModel {
                                    HistoryViewModel(
                                        entries,
                                        10,
                                        entriesListState,
                                        appFolder
                                    )
                                },
                                onBackClick = {
                                    navController.navigate(Routes.WORKSPACE.name)
                                })
                        }
                    }
                }
            }
        }
    }

    private fun getAppFolder(context: Context): File {
        val appFolder = File(context.filesDir.absolutePath)
        if (!appFolder.exists()) {
            appFolder.mkdirs()
        }
        return appFolder
    }
}

