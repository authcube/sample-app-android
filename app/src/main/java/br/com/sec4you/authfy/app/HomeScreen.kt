package br.com.sec4you.authfy.app

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.sec4you.authfy.app.ui.screens.home.ContentPanel
import br.com.sec4you.authfy.app.ui.screens.home.FooterPanel
import br.com.sec4you.authfy.app.ui.screens.home.HeaderPanel
import br.com.sec4you.authfy.app.ui.screens.home.MenuPanel
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme
import br.com.sec4you.authfy.app.ui.theme.LogoutButtonBackground
import net.openid.appauth.AuthState

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
  onUserInfoClick: () -> Unit,
  onLogoutClick: () -> Unit,
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
    ContentPanel()
    Spacer(modifier = Modifier.weight(1.0f)) // fill height with spacer
//    Text(
//      text = authStateManager.authState.accessToken.toString()
//    )
    FooterPanel(onLogoutClick = onLogoutClick)
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
        onUserInfoClick = {},
        onLogoutClick = {},
        authStateManager = AuthStateManager(null, null))
    }
  }
}
