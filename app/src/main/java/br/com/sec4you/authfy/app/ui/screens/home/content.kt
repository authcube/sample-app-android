package br.com.sec4you.authfy.app.ui.screens.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import br.com.sec4you.authfy.app.AuthStateManager
import br.com.sec4you.authfy.app.DEBUG_IS_ENABLED
import br.com.sec4you.authfy.app.HomeScreen
import br.com.sec4you.authfy.app.NetworkUtils
import br.com.sec4you.authfy.app.conditional
import br.com.sec4you.authfy.app.isDebugEnabled
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import net.openid.appauth.AuthState.AuthStateAction
import net.openid.appauth.AuthorizationService
import java.time.LocalTime
import java.util.Calendar

/*
@Composable
fun OtpTextField(modifier: Modifier = Modifier, maxLength: Int = 6, authStateManager: AuthStateManager) {

  val context = LocalContext.current
  val TAG = "AUTHFY:ContentPanel"

  val coroutineScope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }

  var isRequesting by remember {
    mutableStateOf(false)
  }
  val authService = remember {
    AuthorizationService(context)
  }


  var value by remember {
    mutableStateOf(TextFieldValue(text = ""))
  }

  Row {
    SnackbarHost(hostState = snackbarHostState)
  }


  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = modifier
//      .padding(horizontal = 5.dp)
  ) {
    OutlinedTextField(
      value = value,
      onValueChange = {
        if (it.text.length <= maxLength) value = it
      },
      modifier = modifier
        .width(280.dp)
        .padding(horizontal = 20.dp),
      label = {
        Text("Type the code")
      },
      textStyle = LocalTextStyle.current.copy(
        textAlign = TextAlign.Center,
        letterSpacing = 10.sp,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp
      ),
      supportingText = {
        Text(
          text = "${value.text.length} / $maxLength",
          modifier = Modifier.fillMaxWidth(),
          textAlign = TextAlign.End,
        )
      },
    )
    Button(
      onClick = {
        //
        val verifyOTPEndpoint = "https://demo.authfy.tech/sample-app/mfa/totp/verify"

        // do post
        authStateManager.authState.performActionWithFreshTokens(authService,
          AuthStateAction { accessToken, idToken, ex ->
            if (ex != null) {
              // negotiation for fresh tokens failed, check ex for more details
              Log.e(TAG, "Error getting a fresh token", ex)
              return@AuthStateAction
            }

            isRequesting = true

            // use the access token to do something ...
            val headers = mapOf(
              "Content-Type" to "application/json",
              "Authorization" to "Bearer $accessToken"
            )

            val body = "{\"verbose\": false, \"otp\": \"${value.text}\"}"

            coroutineScope.launch {

              var resultMessage = "Código Válido"

              val result = NetworkUtils.doPost(verifyOTPEndpoint, headers, body)

              if (result != null) {

                val contents = result["contents"] as? List<Map<String, Any>>
                val targetValue = contents?.find { content ->
                  (content["rel"] as? List<String>)?.contains("urn:mfao:totp:verify:status")
                    ?: false
                }?.get("values") as? List<String>

                val res = targetValue?.firstOrNull()
                val isValidCode = targetValue?.firstOrNull() in listOf("success", "true", "1")
                resultMessage = if (isValidCode) "Código Válido" else "Código Inválido"

              } else {
                resultMessage = "Código Inválido"
                Log.e(TAG, "Error sending request to server, [result is NULL]")
              }

              isRequesting = false

              snackbarHostState.showSnackbar(
                message = resultMessage,
                duration = SnackbarDuration.Short
              )

            }
          }
        )
        //

      },
      shape = RoundedCornerShape(25),
      modifier = modifier
    ) {
      if ( isRequesting ) {
        CircularProgressIndicator(modifier =
          Modifier.size(20.dp),
          color = Color.White
        )
      } else {
        Text(
          text = "Verify"
        )
      }
    }
  }
}

@Composable
fun OtpValue(modifier: Modifier = Modifier, value: String, copyOtpClick: () -> Unit) {

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .padding(horizontal = 5.dp)
  ) {
    Text(
      text = "Code: ",
      fontSize = 20.sp
    )
    Text(
      text = value,
      fontSize = 26.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 10.sp,
      modifier = Modifier
        .padding(horizontal = 20.dp)
    )
    Spacer(modifier = Modifier.weight(1.0f)) // fill height with spacer
    Button(
      onClick = {
        copyOtpClick()
      },
      shape = CircleShape,
      modifier = modifier.padding(20.dp)
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

@Composable
fun OtpCountdown(modifier: Modifier = Modifier, progressValue: Float) {
//  var currentProgress by remember { mutableStateOf(0.5f) }
  val shape = RoundedCornerShape(8.dp)

  // 10 segundos restantes (1/3 do progresso) muda cor para vermelho
  val barColor = if (progressValue <= 0.33f) {
    Color.Red
  } else {
    MaterialTheme.colorScheme.primary
  }

  Box(
    modifier = modifier
      .padding(horizontal = 35.dp, vertical = 15.dp)
      .background(
        color = barColor,
        shape = shape,
      )
      .clip(shape),
  ) {
    LinearProgressIndicator(progressValue,
      modifier = Modifier
        .fillMaxWidth()
        .height(16.dp),
        color = barColor
//      trackCornerRadius = 10.p
    )
  }
}

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun ContentPanel(modifier: Modifier = Modifier, authStateManager: AuthStateManager) {

  val context = LocalContext.current
  val TAG = "AUTHFY:ContentPanel"

  val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
  val snackbarHostState = remember { SnackbarHostState() }

  var otpValue by remember {
//    mutableStateOf("852123")
    mutableStateOf("")
  }
  var progressValue by remember {
    mutableStateOf(1.0f)
  }

  // timer ticker
  val coroutineScope = rememberCoroutineScope()
  LaunchedEffect(Unit) {
    coroutineScope.launch(Dispatchers.IO) { // Executa em uma thread de background
      while (isActive) { // Verifica se o composable ainda está ativo
        delay(1000)

        if ( ! authStateManager.authfySdk.hasSeed() ) {
          progressValue = 0f
          otpValue = ""
          return@launch
        }

        if ( otpValue == "" ) {
          otpValue = authStateManager.authfySdk.generateTOTP()
        }

        // Verifica se os segundos são 0 ou 30
        val calendar = Calendar.getInstance()
        val seconds = calendar.get(Calendar.SECOND)
        if (seconds in listOf(0, 30)) {
          // Dispara suas ações aqui (em background)
          otpValue = authStateManager.authfySdk.generateTOTP()
        }

        // Calcula o progresso com base na diferença
        progressValue = when {
          seconds < 30 -> (30 - seconds) / 30f // Progresso de 0 a 1 para segundos 0-29
          else -> (60 - seconds) / 30f // Progresso de 1 a 0 para segundos 30-59
        }
      }
    }
  }

  Column(
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .fillMaxWidth()
      .defaultMinSize(minHeight = 200.dp)
      .conditional(isDebugEnabled(), {
        border(BorderStroke(2.dp, SolidColor(Color.Blue)))
      })
  ) {
    Column() {
      OtpValue(value = otpValue, copyOtpClick = {

        val clipData = ClipData.newPlainText("label", otpValue)
        clipboardManager.setPrimaryClip(clipData)

        coroutineScope.launch {
          snackbarHostState.showSnackbar(
            message = "Código copiado!",
            duration = SnackbarDuration.Short
          )
        }

      })
      OtpCountdown(progressValue = progressValue)
      OtpTextField(authStateManager = authStateManager)
    }
  }

  SnackbarHost(hostState = snackbarHostState)
}

@RequiresApi(Build.VERSION_CODES.M)
@Preview(showBackground = true)
@Composable
fun HomeContentPanelPreview() {
  AuthfySampleTheme {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      ContentPanel(authStateManager = AuthStateManager(null, null, null))
    }
  }
}
*/
