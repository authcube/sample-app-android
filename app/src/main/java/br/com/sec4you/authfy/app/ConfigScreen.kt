package br.com.sec4you.authfy.app

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.sec4you.authfy.app.ui.screens.config.HorizontalConfig
import br.com.sec4you.authfy.app.ui.screens.config.VerticalConfig
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme
import br.com.sec4you.authfy.app.ui.theme.BtnBg
import br.com.sec4you.authfy.app.ui.theme.GrayTxt
import kotlinx.coroutines.launch

data class Config(
  var server: String = "",
  var clientId: String = "",
  var appName: String = "",
  var enrollWithRisk: Boolean = false,
  var pkce: Boolean = true
)
@Composable
fun ConfigScreen(navController: NavController) {
    val TAG = "AUTHCUBE:ConfigScreen"
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val configPrefs = ConfigPreferences(context)
    val configState = remember { mutableStateOf(Config()) }

    LaunchedEffect(context) { configState.value = configPrefs.loadConfig() }

    val reloadConfig: () -> Unit = {
        configState.value = configPrefs.loadConfig()
        scope.launch {
            Toast.makeText(context, "Configuration cleared", Toast.LENGTH_SHORT).show()
        }

    }

    Scaffold(
        topBar = {
            TopConfigBar(
                navController = navController,
                configPrefs = configPrefs,
                onConfigCleared = reloadConfig
            )
        },
        bottomBar = { Footer() }
    ) { innerPadding ->
        BoxWithConstraints(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (this.maxWidth) {
                in (0.dp..600.dp) -> {
                    VerticalConfig(configPrefs = configPrefs)
                }

                in (600.dp..900.dp) -> {
                    HorizontalConfig(configPrefs = configPrefs)
                }
            }
        }
    }
}

@Composable
fun TopConfigBar(navController: NavController, configPrefs: ConfigPreferences, onConfigCleared: () -> Unit) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .padding(12.dp)
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .align(Alignment.Center)
    ) {
      Image(
        painter = painterResource(id = R.drawable.authcube_logo_2),
        contentDescription = "Authcube logo",
        modifier = Modifier
          .width(280.dp)
      )
      Text(
        text = "Sample App",
        fontSize = 32.sp,
        fontWeight = FontWeight.W500,
      )
    }
    FloatingActionButton(
      onClick = { navController.navigate(Screen.AuthScreen.route) },
      containerColor = Color.White,
      elevation = FloatingActionButtonDefaults.elevation(
        defaultElevation = 0.dp,
        pressedElevation = 0.dp
      ),
      modifier = Modifier
          .padding(top = 12.dp, start = 12.dp)
        .align(Alignment.TopStart)
    ) {
      Icon(
        Icons.Filled.ArrowBack,
        contentDescription = "Go back",
        tint = BtnBg,
        modifier = Modifier
          .size(40.dp)
      )
    }

    FloatingActionButton(
      onClick = {
        configPrefs.clearConfig()
        onConfigCleared()
      },
      containerColor = Color.White,
      elevation = FloatingActionButtonDefaults.elevation(
        defaultElevation = 0.dp,
        pressedElevation = 0.dp
      ),
      modifier = Modifier
          .padding(top = 12.dp, end = 12.dp)
        .align(Alignment.TopEnd)
    ) {
      Icon(
        Icons.Filled.Refresh,
        contentDescription = "Restore configurations",
        tint = BtnBg,
        modifier = Modifier
          .size(40.dp)
      )
    }
  }
}

@Composable
fun ConfigTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(label, color=GrayTxt, fontWeight = FontWeight.Bold) },
    modifier = modifier
      .heightIn(min = 56.dp),
    singleLine = true,
    maxLines = 1
  )
}

@Composable
fun ToggleButton(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
  Switch(
    checked = checked,
    onCheckedChange = onCheckedChange
  )
}

open class ConfigPreferences(context: Context?) {
  private val prefs = context?.getSharedPreferences("app_config", Context.MODE_PRIVATE)

  open fun saveConfig(config: Config) {
    prefs?.edit()?.apply {
      putString("server", config.server)
      putString("client_id", config.clientId)
      putString("app_name", config.appName)
      putBoolean("enroll_with_risk", config.enrollWithRisk)
      putBoolean("pkce", config.pkce)
      apply()
    }
  }

  open fun loadConfig(): Config {
    return Config(
      server = prefs?.getString("server", "") ?: "",
      clientId = prefs?.getString("client_id", "") ?: "",
      appName = prefs?.getString("app_name", "") ?: "",
      enrollWithRisk = prefs?.getBoolean("enroll_with_risk", false) ?: false,
      pkce = prefs?.getBoolean("pkce", true) ?: true
    )
  }

  fun clearConfig() {
    prefs?.edit()?.apply {
      clear()
      apply()
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
fun ConfigScreenPreview() {
  AuthfySampleTheme {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      ConfigScreen(navController = rememberNavController())
    }
  }
}
