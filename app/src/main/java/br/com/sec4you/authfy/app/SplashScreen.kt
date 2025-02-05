package br.com.sec4you.authfy.app

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
  val TAG = "AUTHCUBE:SplashScreen"
  var isInitialized by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    try {
      delay(2000) // 2 seconds
      navController.navigate(Screen.AuthScreen.route)
    } catch (e: Exception) {
      Log.e(TAG, "Initialization failed", e)
    }
  }
  Box(
    modifier = Modifier
        .fillMaxSize()
        .background(color = MaterialTheme.colorScheme.background),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Image(
        painter = painterResource(id = R.drawable.authcube_logo_2),
        contentDescription = "Splash logo",
        modifier = Modifier
          .size(280.dp)
          .padding(16.dp)
      )
    }
  }
}


@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
  AuthfySampleTheme {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      SplashScreen(rememberNavController())
    }
  }
}
