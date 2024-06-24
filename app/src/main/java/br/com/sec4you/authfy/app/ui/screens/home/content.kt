package br.com.sec4you.authfy.app.ui.screens.home

import android.os.Build
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
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import br.com.sec4you.authfy.app.DEBUG_IS_ENABLED
import br.com.sec4you.authfy.app.HomeScreen
import br.com.sec4you.authfy.app.conditional
import br.com.sec4you.authfy.app.isDebugEnabled
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme


@Composable
fun OtpTextField(modifier: Modifier = Modifier, maxLength: Int = 6, onValueChanged: (String) -> Unit = {}) {
  var value by remember {
    mutableStateOf(TextFieldValue(text = ""))
  }
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = modifier
      .padding(horizontal = 5.dp)
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
        Text("Type the OTP")
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
        onValueChanged(value.text.substring(0, 6))
      },
      shape = RoundedCornerShape(25),
      modifier = modifier
    ) {
      Text(
        text = "Verify"
      )
    }
  }
}

@Composable
fun OtpValue(modifier: Modifier = Modifier, value: String) {
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
      onClick = {},
      shape = CircleShape,
      modifier = modifier
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
fun OtpCountdown(modifier: Modifier = Modifier) {
  var currentProgress by remember { mutableStateOf(0.5f) }
  val shape = RoundedCornerShape(8.dp)

  Box(
    modifier = modifier
      .padding(horizontal = 35.dp, vertical = 15.dp)
      .background(
        color = MaterialTheme.colorScheme.primary,
        shape = shape,
      )
      .clip(shape),
  ) {
    LinearProgressIndicator(currentProgress,
      modifier = Modifier
        .fillMaxWidth()
        .height(16.dp),
//      trackCornerRadius = 10.p
    )
  }
}

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun ContentPanel(modifier: Modifier = Modifier) {
  var otpValue by remember { mutableStateOf("852123") }

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
      OtpValue(value = otpValue)
      OtpCountdown()
      OtpTextField(onValueChanged = { otpValue = it })
    }
  }
}

@RequiresApi(Build.VERSION_CODES.M)
@Preview(showBackground = true)
@Composable
fun HomeContentPanelPreview() {
  AuthfySampleTheme {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      ContentPanel()
    }
  }
}
