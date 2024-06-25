package br.com.sec4you.authfy.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.util.concurrent.locks.ReentrantLock


@SuppressLint("NewApi")
@Composable
fun AuthyNavigator(route: String = Screen.MainScreen.route) {
  val navController = rememberNavController()

  val TAG = "AUTHFY::NavController"

  val STORE_NAME = "AuthState"
  var mPrefs: SharedPreferences = remember {
    navController.context.getSharedPreferences(STORE_NAME,
      Context.MODE_PRIVATE)
  }
  var mPrefsLock: ReentrantLock = remember {
    ReentrantLock()
  }
//  val authState: MutableState<AuthState> = remember {
//    mutableStateOf(AuthState())
//  }
  val authStateManager: AuthStateManager = remember {
    AuthStateManager(mPrefs, mPrefsLock)
  }

  var authenticated by remember { mutableStateOf(false) }
  fun changeAuthenticateState(state: Boolean) {
    authenticated = state
    if (state) {
      navController.navigate(Screen.HomeScreen.route)
    } else {
      navController.navigate(Screen.MainScreen.route)
    }
  }

  NavHost(navController = navController, startDestination = route) {

    composable(route = Screen.MainScreen.route) {
      LoginScreen(navController = navController, authenticated = authenticated, authStateManager = authStateManager)
    }
    composable(route = Screen.AuthScreen.route) {
      AuthScreen(
        navController = navController,
        authenticated = authenticated,
        onAuthenticatedChange = { changeAuthenticateState(it) },
        authStateManager = authStateManager
      )
    }
    composable(route = Screen.HomeScreen.route) {
      HomeScreen(
        navController = navController,
        authenticated = authenticated,
        onUserInfoClick = { },
        onLogoutClick = { changeAuthenticateState(false) },
        authStateManager = authStateManager
      )
    }
  }
}
