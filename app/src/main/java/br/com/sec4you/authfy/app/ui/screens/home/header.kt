package br.com.sec4you.authfy.app.ui.screens.home

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.sec4you.authfy.app.AuthStateManager
import br.com.sec4you.authfy.app.NetworkUtils
import br.com.sec4you.authfy.app.conditional
import br.com.sec4you.authfy.app.isDebugEnabled
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme
import net.openid.appauth.AuthState.AuthStateAction
import net.openid.appauth.AuthorizationService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/*
@Composable
//fun HeaderPanel(modifier: Modifier = Modifier, onUserInfoClick: () -> Unit = {}) {
fun HeaderPanel(modifier: Modifier = Modifier, authStateManager: AuthStateManager) {

  val TAG = "AUTHFY:HEADER"
  val context = LocalContext.current

  var isRequesting by remember {
    mutableStateOf(false)
  }

  val coroutineScope = rememberCoroutineScope()
  var userInfoText by remember {
    mutableStateOf("Click 'Get User Info' to fetch the 'username'")
  }

  val authService = remember {
    AuthorizationService(context)
  }


  Column(
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .fillMaxWidth()
      .defaultMinSize(minHeight = 100.dp)
      .conditional(isDebugEnabled(), {
        border(BorderStroke(2.dp, SolidColor(Color.Magenta)))
      })
  ) {
    Button(
//      onClick = { onUserInfoClick() },
      onClick = {
        authStateManager.authState.performActionWithFreshTokens(authService,
          AuthStateAction { accessToken, idToken, ex ->
            if (ex != null) {
              // negotiation for fresh tokens failed, check ex for more details
              Log.e(TAG, "Error getting a fresh token", ex)
              return@AuthStateAction
            }

            isRequesting = true
            userInfoText = "Fetching user info..."

            // use the access token to do something ...
            val userInfoUrl: String =
              authStateManager.authState.lastTokenResponse?.request?.configuration?.discoveryDoc?.userinfoEndpoint.toString()

            val headers = mapOf("Authorization" to "Bearer $accessToken")

            coroutineScope.launch {
              val result = NetworkUtils.doGet(userInfoUrl, headers)

              if (result != null) {
                userInfoText = result.getValue("uid").toString()
              } else {
                userInfoText = "Error getting userinfo"
                Log.e(TAG, "Error making request to server, [result is NULL]")
              }

              isRequesting = false
            }
          }
        )

      },
      shape = RoundedCornerShape(25),
    ) {
      if ( isRequesting ) {
        CircularProgressIndicator(modifier = Modifier.size(20.dp))
      } else {
      Text( text = "Get User Info" )
      }
    }
    Text(
      modifier = Modifier.padding(top = 10.dp),
      text = userInfoText
    )
  }
}

@Preview(showBackground = true)
@Composable
fun HomeHeaderPanelPreview() {
  AuthfySampleTheme {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
//      HeaderPanel(onUserInfoClick = {})
      HeaderPanel(authStateManager = AuthStateManager(null, null, null))
    }
  }
}
 */
