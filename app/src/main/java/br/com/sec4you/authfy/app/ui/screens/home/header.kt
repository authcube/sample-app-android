package br.com.sec4you.authfy.app.ui.screens.home

import android.os.Build
import android.service.autofill.OnClickAction
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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

@Composable
fun HeaderPanel(modifier: Modifier = Modifier, onUserInfoClick: () -> Unit = {}) {
  Column(
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .fillMaxWidth()
      .defaultMinSize(minHeight = 100.dp)
      .conditional(isDebugEnabled(), {
        border(BorderStroke(2.dp, SolidColor(Color.Magenta)))
      })
  ) {
    Button(
      onClick = { onUserInfoClick() },
      shape = RoundedCornerShape(25),
    ) {
      Text(
        text = "Get User Info"
      )
    }
    Text(
      modifier = Modifier.padding(top = 10.dp),
      text = "Click 'Get User Info' to fetch the 'username'"
    )
  }
}

@Preview(showBackground = true)
@Composable
fun HomeHeaderPanelPreview() {
  AuthfySampleTheme {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      HeaderPanel(onUserInfoClick = {})
    }
  }
}
