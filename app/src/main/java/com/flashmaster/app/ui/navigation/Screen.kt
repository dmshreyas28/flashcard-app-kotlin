package com.flashmaster.app.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Subjects : Screen("subjects")
    object Topics : Screen("topics/{subjectId}") {
        fun createRoute(subjectId: Long) = "topics/$subjectId"
    }
    object Flashcards : Screen("flashcards/{topicId}") {
        fun createRoute(topicId: Long) = "flashcards/$topicId"
    }
    object Study : Screen("study/{topicId}") {
        fun createRoute(topicId: Long) = "study/$topicId"
    }
    object UploadNote : Screen("upload_note/{topicId}/{topicName}") {
        fun createRoute(topicId: Long, topicName: String) = "upload_note/$topicId/$topicName"
    }
}
