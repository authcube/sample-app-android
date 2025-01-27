package br.com.sec4you.authfy.app.ui.screens.risk

import android.graphics.Paint.Align
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.sec4you.authfy.app.R
import br.com.sec4you.authfy.app.Screen
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme

@Composable
fun RiskHeader(navController: NavController, route: String) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(color = MaterialTheme.colorScheme.background),
  ) {
    IconButton(
      onClick = { navController.navigate(route)},
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(12.dp)
    ) {
      Icon(
        imageVector = Icons.Default.ArrowBack,
        contentDescription = "Back",
        modifier = Modifier.size(32.dp)
      )
    }

    Column(
      modifier = Modifier.align(Alignment.TopCenter),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Top
    ) {
      Image(
        painter = painterResource(id = R.drawable.authcube_logo),
        contentDescription = "Splash logo",
        modifier = Modifier
          .width(220.dp)
          .height(180.dp)
          .offset(y = (-40).dp)
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
fun RiskHeaderPreview() {
  AuthfySampleTheme {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      RiskHeader(navController = rememberNavController(), Screen.RiskScreen.route)
    }
  }
}
