package br.com.sec4you.authfy.app.ui.screens.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.sec4you.authfy.app.AuthStateManager
import br.com.sec4you.authfy.app.conditional
import br.com.sec4you.authfy.app.isDebugEnabled
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme
import br.com.sec4you.authfy.app.ui.theme.LogoutButtonBackground
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.EndSessionResponse


@Composable
fun FooterPanel(
  modifier: Modifier = Modifier,
  authStateManager: AuthStateManager,
  onAuthenticatedChange: (Boolean) -> Unit
) {

  val context = LocalContext.current
  val authService = remember {
    AuthorizationService(context)
  }

  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result: ActivityResult ->

    if (result.resultCode != Activity.RESULT_OK) {
      return@rememberLauncherForActivityResult
    }

    // Handle the result here
    val res = result.data?.let { EndSessionResponse.fromIntent(it) }

    if ( res != null ) {
      // logout success
      onAuthenticatedChange(false)
    }

  }


  Column(
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .fillMaxWidth()
      .defaultMinSize(minHeight = 100.dp)
      .conditional(isDebugEnabled(), {
        border(BorderStroke(2.dp, SolidColor(Color.Cyan)))
      })
  ) {
    Button(
      colors = ButtonDefaults.buttonColors(containerColor = LogoutButtonBackground),
      shape = RoundedCornerShape(25),
      modifier = modifier
        .size(width = 150.dp, height = 40.dp),
      onClick = {


        val authState = authStateManager.authState

        val authServiceConfig = AuthorizationServiceConfiguration(
          authStateManager.authState.lastTokenResponse?.request?.configuration?.discoveryDoc!!
        )

        val endSessionRequest = EndSessionRequest.Builder(authServiceConfig)
          .setIdTokenHint(authState.idToken)
          .setPostLogoutRedirectUri(
            Uri.parse("br.com.sec4you.authfy.app.appsample:/logoutredirect")
          )
          .build()

        val endSessionItent: Intent = authService.getEndSessionRequestIntent(endSessionRequest)
        launcher.launch(endSessionItent)


      }
    ) {
      Text(
        text = "Logout"
      )
    }
  }
}


@Preview(showBackground = true)
@Composable
fun HomeFooterPanelPreview() {
  AuthfySampleTheme {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      FooterPanel(
        authStateManager = AuthStateManager(null, null, null),
        onAuthenticatedChange = { })
    }
  }
}
