package br.com.sec4you.authfy.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.MutableLiveData
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme

public val DEBUG_IS_ENABLED = MutableLiveData<Boolean>();


class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    DEBUG_IS_ENABLED.postValue(false);

    setContent {
      AuthfySampleTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          AuthyNavigator(Screen.SplashScreen.route)
        }
      }
    }

  }
}

@Preview(showBackground = true)
@Composable
fun AuthfyNavigatorPreview() {
  AuthfySampleTheme {
//    AuthyNavigator(Screen.MainScreen.route)
    AuthyNavigator(Screen.SplashScreen.route)
  }
}
