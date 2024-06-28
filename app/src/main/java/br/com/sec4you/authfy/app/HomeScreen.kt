package br.com.sec4you.authfy.app

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.sec4you.authfy.app.ui.screens.home.ContentPanel
import br.com.sec4you.authfy.app.ui.screens.home.FooterPanel
import br.com.sec4you.authfy.app.ui.screens.home.HeaderPanel
import br.com.sec4you.authfy.app.ui.screens.home.MenuPanel
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme

// https://proandroiddev.com/jetpack-compose-tricks-conditionally-applying-modifiers-for-dynamic-uis-e3fe5a119f45
inline fun Modifier.conditional(
  condition: Boolean,
  ifTrue: Modifier.() -> Modifier,
  ifFalse: Modifier.() -> Modifier = { this }
): Modifier = if (condition) {
    then(ifTrue(Modifier))
  } else {
    then(ifFalse(Modifier))
  }

@Composable
fun Panel(
  modifier: Modifier,
  height: Dp,
  verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.Center
) {
  Column(
    verticalArrangement = verticalArrangement, modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = height)
  ) {}
}



@Composable
fun SimpleButton(text: String = "Button") {
  Button(
    onClick = {}
  ) {
    Text(
      text = text
    )
  }
}



@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun HomeScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
  authenticated: Boolean,
  onAuthenticatedChange: () -> Unit,
  authStateManager: AuthStateManager
) {
  var TAG = "AUTHFY:SC:HO"

  if (!authenticated) {
    navController.navigate(Screen.MainScreen.route)
    return
  }

  Column(
    verticalArrangement = Arrangement.Center,
    modifier = modifier
      .fillMaxSize()
      .conditional(isDebugEnabled(), {
        border(BorderStroke(2.dp, SolidColor(Color.Red)))
      })
      .padding(horizontal = 10.dp, vertical = 10.dp)
  ) {
//    Text(
//      text = "asd"
//    )
//    HeaderPanel(onUserInfoClick = { })
    HeaderPanel(modifier, authStateManager)
    MenuPanel(navController, authStateManager)
    ContentPanel(authStateManager = authStateManager)
    Spacer(modifier = Modifier.weight(1.0f)) // fill height with spacer
//    Text(
//      text = authStateManager.authState.accessToken.toString()
//    )
    FooterPanel(authStateManager = authStateManager, onAuthenticatedChange = onAuthenticatedChange)
  }
}

private var LOCAL_DEBUG = false;
fun isDebugEnabled(): Boolean {
  return LOCAL_DEBUG || DEBUG_IS_ENABLED.value == true
}

@RequiresApi(Build.VERSION_CODES.M)
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
  AuthfySampleTheme {
    LOCAL_DEBUG = false

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      HomeScreen(
        navController = rememberNavController(),
        authenticated = true,
        onAuthenticatedChange = {},
        authStateManager = AuthStateManager(null, null, null))
    }
  }
}
