package br.com.sec4you.authfy.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AuthyNavigator(route: String = Screen.MainScreen.route) {
  val navController = rememberNavController()
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
      LoginScreen(navController = navController, authenticated = authenticated)
    }
    composable(route = Screen.AuthScreen.route) {
      AuthScreen(
        navController = navController,
        authenticated = authenticated,
        onAuthenticatedChange = { changeAuthenticateState(it) })
    }
    composable(route = Screen.HomeScreen.route) {
      HomeScreen(
        navController = navController,
        authenticated = authenticated,
        onUserInfoClick = { },
        onLogoutClick = { changeAuthenticateState(false) })
    }
  }
}
