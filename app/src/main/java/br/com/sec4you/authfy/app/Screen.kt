package br.com.sec4you.authfy.app

sealed class Screen(val route: String) {
  object MainScreen : Screen("main_screen")
  object AuthScreen : Screen("auth_screen")
  object HomeScreen : Screen("home_screen")
  object SeedsScreen : Screen("seeds_screen")
  object ConfigScreen: Screen("config_screen")
  object SplashScreen: Screen("splash_screen")
  object StartScreen: Screen("start_screen")
  object RiskScreen: Screen("risk_screen")
  object DeviceInfoScreen: Screen("device_info_screen")
  object EvaluateScreen: Screen("evaluate_screen")
  object ConnectScreen: Screen("connect_screen")
  object TokensScreen: Screen("tokens_screen")
  object StrongScreen: Screen("strong_screen")
  object TOTPScreen: Screen("totp_screen")
}
