package br.com.sec4you.authfy.app

import android.Manifest
import android.content.pm.PackageManager
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme
import br.com.sec4you.authfy.app.ui.theme.BtnBg
import br.com.sec4you.authfy.app.ui.theme.BtnTxt
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.CodeVerifierUtil
import net.openid.appauth.ResponseTypeValues
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom


@Composable
fun AuthScreen(
  navController: NavController,
  authenticated: Boolean,
  onAuthenticatedChange: (Boolean) -> Unit,
  authStateManager: AuthStateManager
) {
  val TAG = "AUTHCUBE:AuthScreen"
  val context = navController.context
  val configPrefs = ConfigPreferences(context)
  val userPrefs = UserPreferences(context)
  val coroutineScope = rememberCoroutineScope()

  val authService = remember { AuthorizationService(context) }
  var errorMessage by remember { mutableStateOf<String?>(null) }


/* LOCATION */

    // State to track if location permission is granted
    val isLocationPermissionGranted = remember { mutableStateOf(false) }

    // Function to get location (you'll implement this - same as in Activity example)
    fun getLocation(context: android.content.Context) {
        // Implement your location retrieval logic here using FusedLocationProviderClient etc.
        // Make sure to use the 'context' passed to this function
        Toast.makeText(context, "Getting Location... (AuthScreen)", Toast.LENGTH_SHORT).show()
        // ... Location retrieval code (FusedLocationProviderClient) ...
        // ... Remember to handle permission check *again* within getLocation() if necessary in more complex scenarios
    }

    // Launcher for requesting location permission
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            isLocationPermissionGranted.value = true
            Toast.makeText(context, "Location permission granted in AuthScreen", Toast.LENGTH_SHORT).show()
            getLocation(context) // Call function to get location after permission is granted
        } else {
            isLocationPermissionGranted.value = false
            Toast.makeText(context, "Location permission denied in AuthScreen", Toast.LENGTH_SHORT).show()
            // Handle permission denial scenario - maybe explain why location is needed for some features
        }
    }

    // Function to check location permission
    fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            isLocationPermissionGranted.value = true
            Toast.makeText(context, "Location permission already granted in AuthScreen", Toast.LENGTH_SHORT).show()
            getLocation(context) // Permission already granted, get location directly
        } else {
            isLocationPermissionGranted.value = false // Not strictly needed, but for clarity
        }
    }

    // Function to request location permission
    fun requestLocationPermission() {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // LaunchedEffect to run code when the Composable is launched (similar to onCreate in Activity)
    LaunchedEffect(Unit) { // Unit means it runs only once on initial composition
        checkLocationPermission() // Check permission when AuthScreen is displayed
        if (!isLocationPermissionGranted.value) {
            requestLocationPermission() // Request permission if not already granted
        }
        // ... other initialization logic for AuthScreen if needed ...
    }


/* LOCATION */




  if (authenticated) {
    navController.navigate(Screen.StartScreen.route)
    return
  }

  // Create an ActivityResultLauncher to handle the result
  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result: ActivityResult ->
    if (result.resultCode != Activity.RESULT_OK) {
      onAuthenticatedChange(false)
      return@rememberLauncherForActivityResult
    }

    val data = result.data ?: run {
      onAuthenticatedChange(false)
      return@rememberLauncherForActivityResult
    }

    val authResponse = AuthorizationResponse.fromIntent(data)
    val authException = AuthorizationException.fromIntent(data)

    if (authException != null) {
      Log.e(TAG, "Auth failed", authException)
      onAuthenticatedChange(false)
      return@rememberLauncherForActivityResult
    }

    if (authResponse == null) {
      onAuthenticatedChange(false)
      return@rememberLauncherForActivityResult
    }

    // Exchange auth code for tokens
    authService.performTokenRequest(
      authResponse.createTokenExchangeRequest()
    ) { tokenResponse, ex ->
      if (ex != null) {
        Log.e(TAG, "Token exchange failed", ex)
        onAuthenticatedChange(false)
        return@performTokenRequest
      }

      // Store tokens
      authStateManager.authState.update(tokenResponse, ex)
      authStateManager.saveState()
      onAuthenticatedChange(true)

      // fetch user info
      val userInfoEndpoint = tokenResponse?.request?.configuration?.discoveryDoc?.userinfoEndpoint
      if (userInfoEndpoint != null) {
        val accessToken = tokenResponse.accessToken
        coroutineScope.launch {
          try {
            val headers = mapOf("Authorization" to "Bearer $accessToken")
            when (val result = NetworkUtils.doGet(userInfoEndpoint.toString(), headers)) {
              is NetworkUtils.NetworkResult.Success -> {
                val username = result.data["uid"]?.toString()

                // store username
                if (username != null) {
                  userPrefs.saveUsername(username = username)
                }
              }
              is NetworkUtils.NetworkResult.Error -> {
                Log.e(TAG, "Failed to fetch user info. Code: ${result.code}, Message: ${result.message}")
              }
              is NetworkUtils.NetworkResult.Exception -> {
                Log.e(TAG, "Failed to fetch user info.", result.e)
              }
            }
          } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while fetching user info", e)
          }
        }
      }
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxWidth(),
    topBar = { TopAuthBar(navController) },
    bottomBar = { Footer() },
  ) { innerPadding ->
    Box(
      modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
      ) {
        // Error message display
        errorMessage?.let { message ->
          Card(
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(vertical = 16.dp)
          ) {
            Row(
              modifier = Modifier
                  .padding(16.dp)
                  .fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer
              )
              IconButton(onClick = { errorMessage = null }) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Dismiss error",
                  tint = MaterialTheme.colorScheme.onErrorContainer
                )
              }
            }
          }
        }

        Button(
          colors = ButtonDefaults.buttonColors(
            containerColor = BtnBg,
            contentColor = BtnTxt,
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
              .width(280.dp)
              .height(60.dp),
          onClick = {
            // https://newpst.authfy.tech/demo/connect

            val config = configPrefs.loadConfig()

            val serviceDiscoveryUri =
              Uri.parse(config.server); //https://demo.authfy.tech/sample-app/connect
            val clientId = config.clientId;
            val appName = config.appName;
            val usePkce = config.pkce

            if (serviceDiscoveryUri.toString().isEmpty()) {
              errorMessage = "Server URI is not configured"
              return@Button
            } else if (clientId.isEmpty()) {
              errorMessage = "Client ID is not configured"
              return@Button
            } else if (appName.isEmpty()) {
              errorMessage = "App name is not configured"
              return@Button
            }

            AuthorizationServiceConfiguration.fetchFromIssuer(
              serviceDiscoveryUri
            ) { serviceConfiguration, ex ->
              if (ex != null) {
                Log.e(TAG, "Failed to retrieve configuration", ex)
                // Trate o erro aqui (exiba mensagem, etc.)
              } else {
                serviceConfiguration?.let { config ->
                  // Armazene o serviceConfiguration para uso posterior
                  // Exemplo: SharedPreferences, ViewModel, etc.

                  val authRequestBuilder = AuthorizationRequest.Builder(
                    config, // <- Seu serviceConfiguration
                    clientId,
                    ResponseTypeValues.CODE,
                    Uri.parse("br.com.sec4you.authfy.app.appsample:/oauth2redirect")
                  )
                    .setScope("openid email profile roles")
                    .setLoginHint("jdoe@user.example.com")
                  //.build()
                  if (usePkce) {
                    val codeVerifier = generateCodeVerifier()
                    val codeChallenge = generateCodeChallenge(codeVerifier)

                    userPrefs.saveCodeVerifier(codeVerifier)

                    authRequestBuilder.setCodeVerifier(
                      codeVerifier, codeChallenge, CodeVerifierUtil.getCodeVerifierChallengeMethod()
                    )
                  }
                  val authRequest = authRequestBuilder.build()
                  val authIntent = authService.getAuthorizationRequestIntent(authRequest)
                  launcher.launch(authIntent)

                }
              }
            }
          }
        ) {
          Text(
            text = "Login",
            fontSize = 24.sp
          )
        }
      }
    }
  }

}

@Composable
fun TopAuthBar(navController: NavController) {
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
            .padding(top = 80.dp)
      )
      Text(
        text = "Sample App",
        fontSize = 32.sp,
        fontWeight = FontWeight.W500
      )
    }
    FloatingActionButton(
      onClick = { navController.navigate(Screen.ConfigScreen.route) },
      containerColor = Color.White,
      elevation = FloatingActionButtonDefaults.elevation(
        defaultElevation = 0.dp,
        pressedElevation = 0.dp
      ),
      modifier = Modifier
        .align(Alignment.TopEnd)
    ) {
      Icon(
        Icons.Filled.Settings,
        contentDescription = "Open settings",
        tint = BtnBg,
        modifier = Modifier
          .size(40.dp)
      )
    }
  }
}

@Composable
fun Footer() {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
  ) {
    Text(
      text = "COPYRIGHTS © SINCE 2009"
    )
    Text(
      text = "All Rights Reserved."
    )
  }
}

class UserPreferences(context: Context) {
  private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

  fun saveUsername(username: String) {
    prefs.edit()
      .putString("username", username)
      .apply()
  }

  fun saveCodeVerifier(codeVerifier: String) {
    prefs.edit()
      .putString("codeVerifier", codeVerifier)
      .apply()
  }

  fun getUsername(): String? {
    return prefs.getString("username", null)
  }

  fun getCodeVerifier(): String? {
    return prefs.getString("codeVerifier", null)
  }

  fun clear() {
    prefs.edit().clear().apply()
  }
}


fun generateCodeVerifier(): String {
  val codeVerifierLength = 64

  val secureRandom = SecureRandom()
  val bytes = ByteArray(codeVerifierLength)
  secureRandom.nextBytes(bytes)
  return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
}

fun generateCodeChallenge(codeVerifier: String): String {
  try {
    val bytes = codeVerifier.toByteArray(charset("US-ASCII"))
    val messageDigest = MessageDigest.getInstance("SHA-256")
    messageDigest.update(bytes)
    val digest = messageDigest.digest()
    return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
  } catch (e: NoSuchAlgorithmException) {
    Log.e("PKCE", "Failed to generate code challenge", e)
    throw RuntimeException("Failed to generate PKCE code challenge", e)
  }
}


@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
  AuthfySampleTheme {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      AuthScreen(
        navController = rememberNavController(),
        authenticated = false,
        onAuthenticatedChange = { },
        authStateManager = AuthStateManager(null, null, null)
      )
    }
  }
}
