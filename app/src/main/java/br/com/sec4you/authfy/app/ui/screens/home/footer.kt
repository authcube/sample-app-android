package br.com.sec4you.authfy.app.ui.screens.home

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.sec4you.authfy.app.conditional
import br.com.sec4you.authfy.app.isDebugEnabled
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme
import br.com.sec4you.authfy.app.ui.theme.LogoutButtonBackground

@Composable
fun FooterPanel(modifier: Modifier = Modifier, onLogoutClick: () -> Unit) {
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
        onLogoutClick()
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
      FooterPanel(onLogoutClick = {})
    }
  }
}
