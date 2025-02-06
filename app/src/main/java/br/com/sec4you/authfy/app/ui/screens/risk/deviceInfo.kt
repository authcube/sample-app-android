package br.com.sec4you.authfy.app.ui.screens.risk

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.sec4you.authfy.app.AuthStateManager
import br.com.sec4you.authfy.app.ConfigPreferences
import br.com.sec4you.authfy.app.NetworkUtils
import br.com.sec4you.authfy.app.Screen
import br.com.sec4you.authfy.app.ui.screens.home.HomeFooter
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme
import br.com.sec4you.authfy.app.ui.theme.BtnBg
import br.com.sec4you.authfy.app.ui.theme.BtnTxt
import br.com.sec4you.authfy.sdk.AuthfySdk
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun DeviceInformation(
    navController: NavController,
    authStateManager: AuthStateManager
) {
    val currentRoute by remember { mutableStateOf("risk") }
    val context = LocalContext.current
    val configPrefs = ConfigPreferences(context)
    val deviceInfo = remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(false) }
    var useJWE by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { RiskHeader(navController = navController, route = Screen.RiskScreen.route) },
        bottomBar = {
            HomeFooter(
                navController = navController,
                currentRoute = currentRoute
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BtnBg,
                        contentColor = BtnTxt,
                    ),
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        scope.launch {
                            isLoading.value = true
                            try {
                                if (useJWE) {
                                    val jwk = getJwkFromIDP(configPrefs = configPrefs)
                                    val key = findEncKeyFromJwks(jwk)
                                    val response = authStateManager.authfySdk.getEncryptedDeviceInfo(key)
                                    deviceInfo.value = response
                                } else {
                                    val response = authStateManager.authfySdk.deviceInfo
                                    deviceInfo.value = response
                                }
                            } catch (e: Exception) {
                                deviceInfo.value = "Error: ${e.message}"
                            } finally {
                                isLoading.value = false
                            }
                        }
                    },
                    modifier = Modifier
                        .height(56.dp)
                ) {
                    if (isLoading.value) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = "Collect Device Information",
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.size(24.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Use JWE",
                        fontWeight = FontWeight.SemiBold
                    )
                    Switch(
                        checked = useJWE,
                        onCheckedChange = { useJWE = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                value = deviceInfo.value,
                onValueChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(4.dp)
                    ),
                readOnly = true,
                label = { }
            )
        }
    }
}

suspend fun getJwkFromIDP(configPrefs: ConfigPreferences): String {
    val config = configPrefs.loadConfig()
    val jwksUrl = config.server
        .split("/")
        .dropLast(1)
        .joinToString("/") + "/connect/jwks"

    when (val result = NetworkUtils.doGet(urlString = jwksUrl, headers = null)) {
        is NetworkUtils.NetworkResult.Success -> {
            return result.data.toString()
        }

        is NetworkUtils.NetworkResult.Error -> {
            Log.e(
                "AUTHCUBE:FETCH_JWK",
                "Failed requesting JWK. Code: ${result.code}, Message: ${result.message}"
            )
        }

        is NetworkUtils.NetworkResult.Exception -> {
            Log.e("AUTHCUBE:FETCH_JWK", "Failed requesting JWK.", result.e)
        }
    }

    return ""
}

fun findEncKeyFromJwks(jwksString: String): String {
    val jwksObject = JSONObject(jwksString)
    val keysArray = jwksObject.getJSONArray("keys")
    var encryptionKey = ""

    for (i in 0 until keysArray.length()) {
        val currentKey = keysArray.getJSONObject(i)

        if (currentKey.getString("use") == "enc") {
            encryptionKey = currentKey.toString()
            break
        }
    }

    return encryptionKey

}

@Preview(showBackground = true)
@Composable
fun DeviceInformationPreview() {
    AuthfySampleTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            DeviceInformation(
                navController = rememberNavController(),
                authStateManager = AuthStateManager(null, null, null)
            )
        }
    }
}
