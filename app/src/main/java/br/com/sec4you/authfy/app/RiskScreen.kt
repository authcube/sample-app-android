package br.com.sec4you.authfy.app

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.sec4you.authfy.app.ui.screens.home.HomeFooter
import br.com.sec4you.authfy.app.ui.screens.risk.HorizontalRisk
import br.com.sec4you.authfy.app.ui.screens.risk.VerticalRisk
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme

@Composable
fun RiskScreen(navController: NavController) {
  val currentRoute by remember { mutableStateOf("risk") }

  Scaffold(
    topBar = { TopHomeBar() },
    bottomBar = {
      HomeFooter(
        navController = navController,
        currentRoute = currentRoute
      )
    }
  ) { innerPadding ->
    BoxWithConstraints(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentAlignment = Alignment.BottomCenter
    ) {
        when (this.maxWidth) {
            in (0.dp..600.dp) -> {
                VerticalRisk(navController = navController)
            }
            in (600.dp..900.dp) -> {
                HorizontalRisk(navController = navController)
            }
        }
    }
  }
}

@Preview(
    showSystemUi = true,
    device = "spec:width=673dp,height=841dp,orientation=landscape"
)
@Preview(
    showSystemUi = true,
    device = "spec:width=1080px,height=2424px"
)
@Composable
fun RiskScreenPreview() {
  AuthfySampleTheme {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      RiskScreen(navController = rememberNavController())
    }
  }
}

