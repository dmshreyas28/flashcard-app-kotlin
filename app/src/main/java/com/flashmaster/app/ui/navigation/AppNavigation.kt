package com.flashmaster.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flashmaster.app.ui.screen.*

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Subjects.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Subjects.route) {
            SubjectsScreen(
                onSubjectClick = { subjectId ->
                    navController.navigate(Screen.Topics.createRoute(subjectId))
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Topics.route,
            arguments = listOf(navArgument("subjectId") { type = NavType.LongType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getLong("subjectId") ?: 0L
            TopicsScreen(
                subjectId = subjectId,
                onBackClick = { navController.popBackStack() },
                onTopicClick = { topicId ->
                    navController.navigate(Screen.Flashcards.createRoute(topicId))
                }
            )
        }

        composable(
            route = Screen.Flashcards.route,
            arguments = listOf(navArgument("topicId") { type = NavType.LongType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getLong("topicId") ?: 0L
            FlashcardsScreen(
                topicId = topicId,
                onBackClick = { navController.popBackStack() },
                onStudyClick = {
                    navController.navigate(Screen.Study.createRoute(topicId))
                },
                onUploadClick = {
                    // Default topic name if not loaded yet
                    val topicName = "Topic"
                    navController.navigate(Screen.UploadNote.createRoute(topicId, topicName))
                }
            )
        }

        composable(
            route = Screen.Study.route,
            arguments = listOf(navArgument("topicId") { type = NavType.LongType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getLong("topicId") ?: 0L
            FlashcardStudyScreen(
                topicId = topicId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.UploadNote.route,
            arguments = listOf(
                navArgument("topicId") { type = NavType.LongType },
                navArgument("topicName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getLong("topicId") ?: 0L
            val topicName = backStackEntry.arguments?.getString("topicName") ?: ""
            UploadNoteScreen(
                topicId = topicId,
                topicName = topicName,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}
