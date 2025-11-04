package com.flashmaster.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.flashmaster.app.ui.navigation.AppNavigation
import com.flashmaster.app.ui.navigation.Screen
import com.flashmaster.app.ui.theme.FlashMasterTheme
import com.flashmaster.app.ui.viewmodel.AuthState
import com.flashmaster.app.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            FlashMasterTheme {
                val navController = rememberNavController()
                val authState by authViewModel.authState.collectAsStateWithLifecycle()
                
                val startDestination = when (authState) {
                    is AuthState.Authenticated -> Screen.Subjects.route
                    else -> Screen.Login.route
                }
                
                AppNavigation(
                    navController = navController,
                    startDestination = startDestination
                )
            }
        }
    }
}
