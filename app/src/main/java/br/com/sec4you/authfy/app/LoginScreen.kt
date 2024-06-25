package br.com.sec4you.authfy.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme
import net.openid.appauth.AuthState

@Composable
fun LoginScreen(navController: NavController, authenticated: Boolean, authStateManager: AuthStateManager) {
  if (authenticated) {
    navController.navigate(Screen.HomeScreen.route)
    return
  }

  Column(
    verticalArrangement = Arrangement.Center,
    modifier = Modifier
      .fillMaxWidth()
      .background(Color.Gray)
      .padding(horizontal = 50.dp)
  ) {
    Text(
      text = "You not authenticated yet. Please click on Login button to start"
    )
    Button(
      onClick = {
        navController.navigate(Screen.AuthScreen.route)
      }
    ) {
      Text(
        text = "Login"
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
  AuthfySampleTheme {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      LoginScreen(navController = rememberNavController(), authenticated = false, authStateManager = AuthStateManager(null, null))
    }
  }
}
