package br.com.sec4you.authfy.app

import android.graphics.Paint.Align
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.sec4you.authfy.app.ui.screens.home.HomeFooter
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme
import br.com.sec4you.authfy.app.ui.theme.BtnBg
import br.com.sec4you.authfy.app.ui.theme.BtnTxt

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
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentAlignment = Alignment.BottomCenter
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .offset(y = (-36).dp)
          .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {

        Button(
          onClick = { navController.navigate(Screen.DeviceInfoScreen.route) },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = BtnBg,
            contentColor = BtnTxt,
          ),
          contentPadding = PaddingValues(0.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(start = 20.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = "Device Information",
              modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Device Information",
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Button(
          onClick = {},
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = BtnBg,
            contentColor = BtnTxt,
          ),
          contentPadding = PaddingValues(0.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(start = 20.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Image(
              painter = painterResource(id = R.drawable.clipboard_icon_filled),
              contentDescription = "Transactions",
              modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Transactions",
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Button(
          onClick = { navController.navigate(Screen.EvaluateScreen.route) },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = BtnBg,
            contentColor = BtnTxt,
          ),
          contentPadding = PaddingValues(0.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(start = 20.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Image(
              painter = painterResource(id = R.drawable.tool_icon_filled),
              contentDescription = "Toolkit",
              modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Toolkit",
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }


      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun RiskScreenPreview() {
  AuthfySampleTheme {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      RiskScreen(navController = rememberNavController())
    }
  }
}

