package br.com.sec4you.authfy.app

sealed class Screen(val route: String) {
  object MainScreen : Screen("main_screen")
  object AuthScreen : Screen("auth_screen")
  object HomeScreen : Screen("home_screen")
  object SeedsScreen : Screen("seeds_screen")
}
