package br.com.sec4you.authfy.app.ui.screens.connect

import br.com.sec4you.authfy.app.AuthStateManager
import br.com.sec4you.authfy.app.R
import br.com.sec4you.authfy.app.TopHomeBar

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.sec4you.authfy.app.Screen
import br.com.sec4you.authfy.app.ui.screens.home.HomeFooter
import br.com.sec4you.authfy.app.ui.screens.risk.RiskHeader
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme
import br.com.sec4you.authfy.app.ui.theme.BtnBg
import br.com.sec4you.authfy.app.ui.theme.BtnTxt
import kotlinx.coroutines.launch


@Composable
fun TokensScreen(navController: NavController, authStateManager: AuthStateManager) {
  val TAG = "AUTHCUBE:TOKENS_SCREEN"
  val currentRoute by remember { mutableStateOf("connect") }
  val clipboardManager = LocalClipboardManager.current
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  var token by remember { mutableStateOf("") }

  Scaffold(
    topBar =  { RiskHeader(navController, Screen.ConnectScreen.route) },
    bottomBar = { HomeFooter(navController = navController, currentRoute = currentRoute) }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Top
    ) {

      // Access Token button
      Button(
        modifier = Modifier
          .padding(bottom = 8.dp)
          .height(48.dp)
          .width(240.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = BtnBg,
          contentColor = BtnTxt,
        ),
        shape = RoundedCornerShape(12.dp),
        onClick = {
          token = authStateManager.authState.accessToken!!
        }
      ) {
        Text(
          text = "Access Token",
          fontSize = 18.sp
        )
      }

      // Refresh token button
      Button(
        modifier = Modifier
          .padding(bottom = 8.dp)
          .height(48.dp)
          .width(240.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = BtnBg,
          contentColor = BtnTxt,
        ),
        shape = RoundedCornerShape(12.dp),
        onClick = {
          val refreshToken = authStateManager.authState.refreshToken
          if (refreshToken.isNullOrEmpty()) {
            Log.e(TAG, "Refresh token is null or empty")
            Toast.makeText(context, "Refresh token is null or empty", Toast.LENGTH_SHORT).show()
            return@Button
          }

          token = refreshToken
        }
      ) {
        Text(
          text = "Refresh Token",
          fontSize = 18.sp
        )
      }

      // Id Token button
      Button(
        modifier = Modifier
          .padding(bottom = 8.dp)
          .height(48.dp)
          .width(240.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = BtnBg,
          contentColor = BtnTxt,
        ),
        shape = RoundedCornerShape(12.dp),
        onClick = { token = authStateManager.authState.idToken!! }
      ) {
        Text(
          text = "Id Token",
          fontSize = 18.sp
        )
      }
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
          .padding(bottom = 16.dp)
          .weight(1f)
      ) {
        TextField(
          colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
          ),
          value = token,
          onValueChange = { },
          modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .border(
              width = 1.dp,
              color = Color.Black,
              shape = RoundedCornerShape(4.dp)
            ),
          readOnly = true,
          label = { }
        )
        Box(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(bottom = 12.dp, end = 12.dp)
            .background(
              color = BtnBg,
              shape = RoundedCornerShape(4.dp)
            )
            .clickable {
              scope.launch {
                clipboardManager.setText(AnnotatedString(token))
                Toast.makeText(context, "Token copied to clipboard", Toast.LENGTH_SHORT).show()
              }
            }
        ) {
          Image(
            painter = painterResource(R.drawable.white_copy_icon),
            contentDescription = "Copy to clipboard",
            modifier = Modifier
              .padding(6.dp)
              .size(24.dp)
          )
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun ConnectScreenPreview() {
  AuthfySampleTheme {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      TokensScreen(navController = rememberNavController(), authStateManager = AuthStateManager(null, null, null))
    }
  }
}
