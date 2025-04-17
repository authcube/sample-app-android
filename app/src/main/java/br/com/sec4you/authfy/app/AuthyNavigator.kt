package br.com.sec4you.authfy.app


import QrCodeScanner
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.sec4you.authfy.app.ui.screens.connect.TokensScreen
import br.com.sec4you.authfy.app.ui.screens.risk.DeviceInformation
import br.com.sec4you.authfy.app.ui.screens.risk.Evaluate
import br.com.sec4you.authfy.app.ui.screens.strong.TOTP
import java.util.concurrent.locks.ReentrantLock
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


@SuppressLint("NewApi")
@Composable
//fun AuthyNavigator(route: String = Screen.MainScreen.route) {
fun AuthyNavigator(route: String = Screen.AuthScreen.route) {
    val navController = rememberNavController()

    val TAG = "AUTHCUBE::NavController"

    val scope = rememberCoroutineScope()

    val STORE_NAME = "AuthState"
    var mPrefs: SharedPreferences = remember {
        navController.context.getSharedPreferences(
            STORE_NAME,
            Context.MODE_PRIVATE
        )
    }
    var mPrefsLock: ReentrantLock = remember {
        ReentrantLock()
    }

    val authStateManager: AuthStateManager = remember {
        AuthStateManager(mPrefs, mPrefsLock, navController.context)
    }

    var authenticated by remember { mutableStateOf(false) }
    fun changeAuthenticateState(state: Boolean) {
        authenticated = state
        if (state) {
            navController.navigate(Screen.HomeScreen.route)
        } else {
//      navController.navigate(Screen.MainScreen.route)
            navController.navigate(Screen.AuthScreen.route)
        }
    }

    // needed for the request
    val authService = remember { AuthorizationService(navController.context) }

    var scannedUrl by remember { mutableStateOf<String?>(null) }
    var showScanner by remember { mutableStateOf(false) }

    val openCameraAction: () -> Unit = remember {
        {

            Log.d("AuthyNavigator", "Open Camera button clicked!")

            showScanner = true

        }
    }


    NavHost(navController = navController, startDestination = route) {

        composable(route = Screen.MainScreen.route) {
            LoginScreen(
                navController = navController,
                authenticated = authenticated,
                authStateManager = authStateManager
            )
        }

        composable(route = Screen.AuthScreen.route) {
            AuthScreen(
                navController = navController,
                authenticated = authenticated,
                onAuthenticatedChange = { changeAuthenticateState(it) },
                authStateManager = authStateManager
            )
        }

        composable(route = Screen.HomeScreen.route) {
            HomeScreen(
                navController = navController,
                authenticated = authenticated,
                onAuthenticatedChange = { changeAuthenticateState(it) },
                authStateManager = authStateManager,
                onCameraClick = openCameraAction
            )
        }

        composable(route = Screen.ConfigScreen.route) {
            ConfigScreen(navController = navController)
        }

        composable(route = Screen.SplashScreen.route) {
            SplashScreen(navController = navController)
        }

        composable(route = Screen.StartScreen.route) {
            StartScreen(navController = navController)
        }

        composable(route = Screen.RiskScreen.route) {
            RiskScreen(navController = navController, onCameraClick = openCameraAction)
        }

        composable(route = Screen.DeviceInfoScreen.route) {
            DeviceInformation(
                navController = navController,
                authStateManager = authStateManager,
                onCameraClick = openCameraAction
            )
        }

        composable(route = Screen.EvaluateScreen.route) {
            Evaluate(
                navController = navController,
                authStateManager = authStateManager,
                onCameraClick = openCameraAction
            )
        }

        composable(route = Screen.ConnectScreen.route) {
            ConnectScreen(
                navController = navController,
                authStateManager = authStateManager,
                onCameraClick = openCameraAction
            )
        }

        composable(route = Screen.TokensScreen.route) {
            TokensScreen(
                navController = navController,
                authStateManager = authStateManager,
                onCameraClick = openCameraAction
            )
        }

        composable(route = Screen.StrongScreen.route) {
            StrongScreen(navController = navController, onCameraClick = openCameraAction)
        }

        composable(route = Screen.TOTPScreen.route) {
            TOTP(
                navController = navController,
                authStateManager = authStateManager,
                onCameraClick = openCameraAction
            )
        }
    } // fim do NavHost

    // Será desenhado por cima do NavHost quando showScanner for true
    if (showScanner) {
        Log.d("AuthyNavigator", "Compondo QrCodeScanner (Overlay)")
        QrCodeScanner(
            modifier = Modifier.fillMaxSize(), // Ocupa toda a tela
            onQrCodeScanned = { url ->
                Log.d("AuthyNavigator", "Scanner Overlay retornou URL: $url")
                scannedUrl = url    // Atualiza o estado aqui no AuthyNavigator
                showScanner = false // Esconde o scanner


                //*****
                scope.launch {
                    sendQRCodeCallBackToServer(
                        authStateManager,
                        authService,
                        url,
                        navController.context
                    )
                }
                //*****
            }
        )
        // Botão de Cancelar opcional sobreposto ao scanner
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopEnd // Ou BottomCenter, etc.
        ) {
            Button(onClick = {
                Log.d("AuthyNavigator", "Cancelando scanner overlay")
                showScanner = false
            }) { Text("Cancelar") }
        }
    }
    // --- Fim da Camada do Scanner ---
}

private suspend fun sendQRCodeCallBackToServer(
    authStateManager: AuthStateManager,
    authService: AuthorizationService,
    url: String,
    context: Context
): Boolean {
    val TAG = "AUTHCUBE:sendQRCodeAuth"

    return suspendCancellableCoroutine { continuation ->
        authStateManager.authState.performActionWithFreshTokens(
            authService,
            AuthState.AuthStateAction { accessToken, _, ex ->
                if (ex != null) {
                    Log.e(TAG, "Error fetching a fresh token: ${ex.message}")
                    return@AuthStateAction
                }


                val headers = mapOf(
                    "Content-Type" to "application/json",
                    "Authorization" to "Bearer $accessToken"
                )

                runBlocking {
                    when (val result = NetworkUtils.doGet(
                        urlString = url,
                        headers = headers,
                    )) {

                        is NetworkUtils.NetworkResult.Success -> {
                            Log.d(TAG, "send auth qrcode success: ${result.data}")
                            continuation.resume(true)
                            // handle success case
                            ContextCompat.getMainExecutor(context).execute {
                                // Usar applicationContext é mais seguro aqui
                                Toast.makeText(
                                    context.applicationContext, // Use Application Context
                                    "Request Sent Successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        is NetworkUtils.NetworkResult.Error -> {
                            Log.e(
                                TAG, "send auth qrcode failed: Code: ${result.code}, " +
                                    "Message: ${result.message}, " +
                                    "Error body: ${result.errorBody}"
                            )
                            continuation.resume(false)

                            // handle error case
                            ContextCompat.getMainExecutor(context).execute {
                                // Usar applicationContext é mais seguro aqui
                                Toast.makeText(
                                    context.applicationContext, // Use Application Context
                                    "send auth qrcode failed: Code: ${result.code}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        is NetworkUtils.NetworkResult.Exception -> {
                            Log.e(TAG, "send auth qrcode exception", result.e)
                            continuation.resumeWithException(result.e)

                            // handle exception case
                            ContextCompat.getMainExecutor(context).execute {
                                // Usar applicationContext é mais seguro aqui
                                Toast.makeText(
                                    context.applicationContext, // Use Application Context
                                    "send auth qrcode exception ${result.e}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }
        )
    }
}
