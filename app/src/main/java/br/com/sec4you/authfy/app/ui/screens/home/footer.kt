package br.com.sec4you.authfy.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.sec4you.authfy.app.R
import br.com.sec4you.authfy.app.Screen
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme
import br.com.sec4you.authfy.app.ui.theme.BtnBg


@Composable
fun HomeFooter(navController: NavController, currentRoute: String = "home") {
  Row(
    modifier = Modifier
      .background(Color.White)
      .padding(8.dp)
      .fillMaxWidth(),
    horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically
  ) {

    BottomNavItem(
      icon = Icons.Filled.Home,
      label = "Home",
      isSelected = currentRoute == "home",
      onClick = { navController.navigate(Screen.HomeScreen.route) }
    )

    Spacer(modifier = Modifier.width(2.dp))

    BottomNavItem(
      icon = Icons.Filled.Warning,
      label = "Risk",
      isSelected = currentRoute == "risk",
      onClick = { navController.navigate(Screen.RiskScreen.route) }
    )

    Spacer(modifier = Modifier.width(2.dp))

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .padding(8.dp)
        .clickable(onClick = { navController.navigate(Screen.StrongScreen.route) })
    ) {
      Icon(
        painter = painterResource(id = R.drawable.key_vertical),
        contentDescription = "Strong",
        tint = if (currentRoute == "strong") BtnBg else Color.Gray,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Strong",
        color = if (currentRoute == "strong") BtnBg else Color.Gray,
        style = MaterialTheme.typography.labelSmall,
        fontSize = 12.sp
      )
    }


    Spacer(modifier = Modifier.width(2.dp))

    BottomNavItem(
      icon = Icons.Filled.Person,
      label = "Connect",
      isSelected = currentRoute == "connect",
      onClick = { navController.navigate(Screen.ConnectScreen.route) }
    )
  }
}

@Composable
fun BottomNavItem(
  icon: ImageVector,
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .padding(8.dp)
      .clickable(onClick = onClick)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = if (isSelected) BtnBg else Color.Gray,
      modifier = Modifier.size(24.dp)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = label,
      color = if (isSelected) BtnBg else Color.Gray,
      style = MaterialTheme.typography.labelSmall,
      fontSize = 12.sp
    )
  }
}


@Preview(showBackground = true)
@Composable
fun HomeFooterPanelPreview() {
  AuthfySampleTheme {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      HomeFooter(navController = rememberNavController())
    }
  }
}
