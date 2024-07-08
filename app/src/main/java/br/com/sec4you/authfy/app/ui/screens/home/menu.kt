package br.com.sec4you.authfy.app.ui.screens.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.sec4you.authfy.app.AuthStateManager
import br.com.sec4you.authfy.app.DEBUG_IS_ENABLED
import br.com.sec4you.authfy.app.Screen
import br.com.sec4you.authfy.app.conditional
import br.com.sec4you.authfy.app.isDebugEnabled
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme
import kotlinx.coroutines.launch


@Composable
fun MenuPanel( navController: NavController,
              authStateManager: AuthStateManager) {

  val context = LocalContext.current
  val TAG = "AUTHFY:ContentPanel"

  val coroutineScope = rememberCoroutineScope()
  val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
  val snackbarHostState = remember { SnackbarHostState() }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .conditional(isDebugEnabled(), {
        border(BorderStroke(2.dp, SolidColor(Color.Green)))
      })
      .padding(vertical = 10.dp)
  ) {
    MenuItem(title = "ID Tokens", onButtonClick = {
      val clipData = ClipData.newPlainText("label",
        authStateManager.authState.idToken)
      clipboardManager.setPrimaryClip(clipData)

      coroutineScope.launch {
        snackbarHostState.showSnackbar(
          message = "idToken copiado!",
          duration = SnackbarDuration.Short
        )
      }
    })
    MenuItem(title = "Access Token", onButtonClick = {
      val clipData = ClipData.newPlainText("label",
        authStateManager.authState.accessToken)
      clipboardManager.setPrimaryClip(clipData)

      coroutineScope.launch {
        snackbarHostState.showSnackbar(
          message = "accessToken copiado!",
          duration = SnackbarDuration.Short
        )
      }
    })
    MenuItem(title = "Refresh Token", onButtonClick = {
      val clipData = ClipData.newPlainText("label",
        authStateManager.authState.refreshToken)
      clipboardManager.setPrimaryClip(clipData)

      coroutineScope.launch {
        snackbarHostState.showSnackbar(
          message = "refreshToken copiado!",
          duration = SnackbarDuration.Short
        )
      }
    })
    MenuItem(title = "Seeds", onButtonClick = {
      navController.navigate(Screen.SeedsScreen.route)
    })

    Row {
      SnackbarHost(hostState = snackbarHostState)
    }
  }
}

@Composable
fun MenuItem(title: String, onButtonClick: () -> Unit) {

  val modifier = Modifier

  Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = modifier
      .padding(horizontal = 25.dp, vertical = 5.dp)
      .fillMaxWidth()
  ) {
  Text(
      text = title
    )
    Button(
      onClick = onButtonClick,
      shape = CircleShape,
      modifier = modifier
        .size(30.dp),
      contentPadding = PaddingValues(1.dp)
    ) {
      Icon(
        imageVector = Icons.Outlined.Done,
        contentDescription = "Copy",
        modifier = Modifier.size(20.dp)
      )
    }
  }
}


private var LOCAL_DEBUG = false;
fun isDebugEnabled(): Boolean {
  return LOCAL_DEBUG || DEBUG_IS_ENABLED.value == true
}

@Preview(showBackground = true)
@Composable
fun HomeMenuuPanelPreview() {
  AuthfySampleTheme {
    LOCAL_DEBUG = true

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      MenuPanel(navController = rememberNavController(),
        authStateManager = AuthStateManager(null, null, null)
      )
    }
  }
}
