package br.com.sec4you.authfy.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme
import br.com.sec4you.authfy.app.ui.theme.BtnBg
import br.com.sec4you.authfy.app.ui.theme.BtnTxt
import br.com.sec4you.authfy.app.ui.theme.GrayTxt


@Composable
fun StartScreen(
  navController: NavController,
) {
  val TAG = "AUTHCUBE:StartScreen"
  val context = LocalContext.current
  val userPrefs = UserPreferences(context)
  var errorMessage by remember { mutableStateOf<String?>(null) }

  val username = userPrefs.getUsername()

  Scaffold(
    modifier = Modifier.fillMaxWidth(),
    topBar = { TopAuthBar(navController) },
    bottomBar = { Footer() },
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Error message display
        errorMessage?.let { message ->
          Card(
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp)
              .padding(vertical = 16.dp)
          ) {
            Row(
              modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer
              )
              IconButton(onClick = { errorMessage = null }) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Dismiss error",
                  tint = MaterialTheme.colorScheme.onErrorContainer
                )
              }
            }
          }
        }

        Column(
          modifier = Modifier
            .fillMaxSize()
            .weight(1f),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Text(
            text = "Welcome back, $username",
            color = GrayTxt,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom=120.dp)
          )

          Button(
            colors = ButtonDefaults.buttonColors(
              containerColor = BtnBg,
              contentColor = BtnTxt,
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .padding(bottom = 120.dp)
              .width(280.dp)
              .height(60.dp),
            onClick = { navController.navigate(Screen.HomeScreen.route) }
          ) {
            Text(
              text = "Enter",
              fontSize = 24.sp
            )
          }
        }
      }
    }
  }

}


@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {
  AuthfySampleTheme {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      StartScreen(
        navController = rememberNavController()
      )
    }
  }
}
