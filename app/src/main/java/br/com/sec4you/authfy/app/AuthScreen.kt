package br.com.sec4you.authfy.app

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationService.TokenResponseCallback
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues


@Composable
fun AuthScreen(
  navController: NavController,
  authenticated: Boolean,
  onAuthenticatedChange: (Boolean) -> Unit,
  authStateManager: AuthStateManager
) {

  val context = navController.context
  val _activity = context as? Activity

  val authService = remember {
    AuthorizationService(context)
  }


  val TAG = "AUTHFY:AuthScreen"
  val RC_AUTH = 121212

  if (authenticated) {
    navController.navigate(Screen.HomeScreen.route)
    return
  }


  // Create an ActivityResultLauncher to handle the result
  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result: ActivityResult ->
    if (result.resultCode != Activity.RESULT_OK) {
      onAuthenticatedChange(false)
    }

    // Handle the result here
    val res = result.data?.let { AuthorizationResponse.fromIntent(it) }

    authService.performTokenRequest(
      res!!.createTokenExchangeRequest(),
      TokenResponseCallback { resp, ex ->
        if (resp != null) {
          // exchange succeeded
          authStateManager.authState.update(resp, ex)
          authStateManager.saveState()
          onAuthenticatedChange(true)
        } else {
          // authorization failed, check ex for more details
          onAuthenticatedChange(false)
        }
      })

  }


  Column(
    verticalArrangement = Arrangement.spacedBy(15.dp, alignment = Alignment.CenterVertically),
    modifier = Modifier
      .fillMaxWidth()
      .background(Color.DarkGray)
      .padding(horizontal = 50.dp)
  ) {
    Text(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(112, 112, 112)),
      text = "Auth"
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Button(
        onClick = {
          navController.navigate(Screen.MainScreen.route)
        }
      ) {
        Text(
          text = "Back"
        )
      }
      Button(
        onClick = {
          // https://newpst.authfy.tech/demo/connect
          val serviceDiscoveryUri = Uri.parse("https://demo.authfy.tech/sample-app/connect")

          AuthorizationServiceConfiguration.fetchFromIssuer(
            serviceDiscoveryUri,
            object : AuthorizationServiceConfiguration.RetrieveConfigurationCallback {
              override fun onFetchConfigurationCompleted(
                serviceConfiguration: AuthorizationServiceConfiguration?,
                ex: AuthorizationException?
              ) {
                if (ex != null) {
                  Log.e(TAG, "Failed to retrieve configuration", ex)
                  // Trate o erro aqui (exiba mensagem, etc.)
                } else {
                  serviceConfiguration?.let { config ->
                    // Armazene o serviceConfiguration para uso posterior
                    // Exemplo: SharedPreferences, ViewModel, etc.

                    // Use a configuração aqui
                    val authRequestBuilder = AuthorizationRequest.Builder(
                      config, // <- Seu serviceConfiguration
                      "VYMLshT2E4gGZPdpH53slwh7",
                      ResponseTypeValues.CODE,
                      Uri.parse("br.com.sec4you.authfy.app.appsample:/oauth2redirect")
                    )
                      .setScope("openid email profile")
                      .setLoginHint("jdoe@user.example.com")
                      .build()

                    // ... continue com o seu fluxo de autenticação
                    val authIntent = authService.getAuthorizationRequestIntent(authRequestBuilder)

//                    if (_activity != null) {
//                      startActivityForResult(_activity, authIntent, RC_AUTH, null)
//                    }

                    launcher.launch(authIntent)

                  }
                }
              }
            })
        }
      ) {
        Text(
          text = "Home"
        )
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
  AuthfySampleTheme {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      AuthScreen(
        navController = rememberNavController(),
        authenticated = false,
        onAuthenticatedChange = { },
        authStateManager = AuthStateManager(null, null))
    }
  }
}
