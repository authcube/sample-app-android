package br.com.sec4you.authfy.app

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme
import kotlinx.coroutines.launch
import net.openid.appauth.AuthState.AuthStateAction
import net.openid.appauth.AuthorizationService

@Composable
fun SeedsScreen(navController: NavHostController, authStateManager: AuthStateManager) {

  val TAG = "AUTHFY:SEEDS"
  val context = LocalContext.current

  val coroutineScope = rememberCoroutineScope()
  var seedInformation by remember {
    mutableStateOf("")
  }

  val authService = remember {
    AuthorizationService(context)
  }

  // TODO try to get at least part of URL from discoverDoc
  val enrollEndpoint = "https://demo.authfy.tech/sample-app/mfa/totp/enrollment"

  Column(
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .fillMaxWidth()
      .defaultMinSize(minHeight = 100.dp)
      .conditional(isDebugEnabled(), {
        border(BorderStroke(2.dp, SolidColor(Color.Magenta)))
      })
  )
  {
    Row(modifier = Modifier
      .padding(vertical = 10.dp)
    ) {
      Text(text = "OTP Seeds")
    }
    Row {
      Button(onClick = {

        // do post
        authStateManager.authState.performActionWithFreshTokens(authService,
          AuthStateAction { accessToken, idToken, ex ->
            if (ex != null) {
              // negotiation for fresh tokens failed, check ex for more details
              Log.e(TAG, "Error getting a fresh token", ex)
              return@AuthStateAction
            }

            seedInformation = "Performing enrollment ..."

            // use the access token to do something ...
            val headers = mapOf(
              "Content-Type" to "application/json",
              "Authorization" to "Bearer $accessToken"
            )

            val body = "{\"verbose\": false}"

            coroutineScope.launch {
              seedInformation = "Performing enrollment ..."
              val result = NetworkUtils.doPost(enrollEndpoint, headers, body)

              if (result != null) {

                val contents = result["contents"] as? List<Map<String, Any>>
                val targetValue = contents?.find { content ->
                  (content["rel"] as? List<String>)?.contains("urn:mfao:totp:enrollment:data") ?: false
                }?.get("values") as? List<String>
                val otpSeed = targetValue?.firstOrNull()

                seedInformation = otpSeed ?: "Error during enrollment"

              } else {
                seedInformation = "Error during enrollment"
                Log.e(TAG, "Error making request to server, [result is NULL]")
              }
            }
          }
        )

      }) {
        Text(text = "Enroll")
      }
    }
    Row(Modifier.padding(vertical = 10.dp, horizontal = 15.dp)) {
      Text(text = seedInformation)
    }
  }

}

@Preview(showBackground = true)
@Composable
fun SeedsScreenPreview() {
  AuthfySampleTheme {

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      SeedsScreen(
        navController = rememberNavController(),
        authStateManager = AuthStateManager(null, null))
    }
  }
}
