package br.com.sec4you.authfy.app.ui.screens.strong

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.sec4you.authfy.app.AuthStateManager
import br.com.sec4you.authfy.app.ConfigPreferences
import br.com.sec4you.authfy.app.NetworkUtils
import br.com.sec4you.authfy.app.R
import br.com.sec4you.authfy.app.Screen
import br.com.sec4you.authfy.app.UserPreferences
import br.com.sec4you.authfy.app.ui.screens.home.HomeFooter
import br.com.sec4you.authfy.app.ui.screens.risk.RiskHeader
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme
import br.com.sec4you.authfy.app.ui.theme.BtnBg
import br.com.sec4you.authfy.app.ui.theme.BtnTxt
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


@Composable
fun TOTP(
    navController: NavController,
    authStateManager: AuthStateManager,
    onCameraClick: () -> Unit
) {
    val TAG = "AUTHCUBE:TOTP"

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val configPrefs = ConfigPreferences(context)
    val userPrefs = UserPreferences(context)

    val authService = AuthorizationService(context)

    var timeLeft by remember { mutableIntStateOf(30) }
    var totpCode by remember { mutableStateOf("") }
    var isEnrolled by remember { mutableStateOf(false) }

    // Timer effect
    LaunchedEffect(key1 = true) {
        while (true) {
            if (totpCode == "") {
                val generatedCode = generateTOTP(authStateManager = authStateManager)
                totpCode = generatedCode?.toString() ?: "null"
                isEnrolled = generatedCode != null
            }

            delay(1000L)
            timeLeft = if (timeLeft <= 1) {
                // Generate new TOTP code here
                val newCode = generateTOTP(authStateManager = authStateManager)
                totpCode = newCode?.toString() ?: "null"
                isEnrolled = newCode != null
                30
            } else {
                timeLeft - 1
            }
        }
    }

    var showDialog by remember { mutableStateOf(false) }
    var isCodeValid by remember { mutableStateOf(false) }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isCodeValid) {
                            Icons.Default.CheckCircle
                        } else {
                            Icons.Default.Warning
                        },
                        contentDescription = if (isCodeValid) "Success" else "Error",
                        tint = if (isCodeValid) {
                            Color.Green
                        } else {
                            Color.Red
                        }
                    )
                    Text(
                        text = if (isCodeValid) "Valid Code" else "Invalid Code",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            },
            text = {
                Text(
                    text = if (isCodeValid) {
                        "The TOTP code you entered is valid."
                    } else {
                        "The TOTP code you entered is incorrect. Please try again."
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) { Text("OK") }
            }
        )
    }

    Scaffold(
        topBar = { RiskHeader(navController, Screen.StrongScreen.route) },
        bottomBar = { HomeFooter(navController, "strong", onCameraClick = onCameraClick) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "Two-Factor Code",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // TOTP Code Display
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = if (totpCode != "null") totpCode else "000000",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 4.sp,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(totpCode))
                        Toast.makeText(context, "Code copied!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = painterResource(R.drawable.copy_icon),
                        contentDescription = "Copy code",
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Timer Circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .border(
                        width = 4.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            ) {
                Text(
                    text = timeLeft.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (timeLeft <= 5) Color.Red else MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            var validationCode by remember { mutableStateOf("") }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp)
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = validationCode,
                    onValueChange = {
                        // allow only nums and max to 6 digits
                        if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                            validationCode = it
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    placeholder = { Text("Enter TOTP code") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Button(
                    onClick = {
                        scope.launch {
                            isCodeValid = verifyTOTP(
                                totpCode = validationCode,
                                configPrefs = configPrefs,
                                authStateManager = authStateManager,
                                authService = authService
                            )
                            showDialog = true
                            validationCode = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BtnBg,
                        contentColor = BtnTxt,
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(56.dp)
                ) {
                    Text("Validate")
                }
            }


            // Enrollment button
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = BtnBg,
                    contentColor = BtnTxt,
                ),
                shape = RoundedCornerShape(12.dp),
                onClick = {
                    scope.launch {
                        if (!isEnrolled) {
                            doEnrollment(
                                configPrefs = configPrefs,
                                userPrefs = userPrefs,
                                authStateManager = authStateManager,
                                authService = authService
                            )
                            timeLeft = 30
                            val newCode = generateTOTP(authStateManager = authStateManager)
                            totpCode = newCode ?: "null"
                            isEnrolled = newCode != null
                        } else {
                            doDeleteEnrollment(
                                configPrefs = configPrefs,
                                userPrefs = userPrefs,
                                authStateManager = authStateManager,
                                authService = authService
                            )
                            timeLeft = 30
                            totpCode = "null"
                            isEnrolled = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp)
                    .padding(top = 20.dp)
                    .height(56.dp)
            ) {
                Text(
                    text = if (isEnrolled) "Unroll" else "Enroll",
                    fontSize = 16.sp
                )
            }
        }
    }
}

private suspend fun doEnrollment(
    configPrefs: ConfigPreferences,
    userPrefs: UserPreferences,
    authStateManager: AuthStateManager,
    authService: AuthorizationService
) {
    val TAG = "AUTHCUBE:ENROLL_TOTP"
    val config = configPrefs.loadConfig()
    val enrollmentUrl = config.server
        .split("/")
        .dropLast(1)
        .joinToString("/") + "/mfa/totp/enrollment"



    return suspendCancellableCoroutine { continuation ->
        authStateManager.authState.performActionWithFreshTokens(
            authService,
            AuthState.AuthStateAction { accessToken, _, ex ->
                if (ex != null) {
                    Log.e(TAG, "Error fetching a fresh token: ${ex.message}")
                    return@AuthStateAction
                }

                val mainJsonObject = JsonObject()
                mainJsonObject.addProperty("username", userPrefs.getUsername())
                mainJsonObject.addProperty("verbose", false)
                val body = Gson().toJson(mainJsonObject)

                val headers = mapOf(
                    "Content-Type" to "application/json",
                    "Authorization" to "Bearer $accessToken"
                )

                runBlocking {
                    when (val result = NetworkUtils.doPost(
                        urlString = enrollmentUrl,
                        headers = headers,
                        body = body
                    )) {

                        is NetworkUtils.NetworkResult.Success -> {
                            Log.d(TAG, "Enrollment successful: ${result.data}")

                            try {
                                val response = result.data
                                val contents = response["contents"] as? List<*>

                                val totpDataContent = contents?.firstOrNull { content ->
                                    val content = content as? Map<*, *>
                                    val rels = content?.get("rel") as? List<*>
                                    rels?.contains("urn:mfao:totp:enrollment:data") == true
                                } as? Map<*, *>

                                val values = totpDataContent?.get("values") as? List<*>
                                var totpUrl = values?.firstOrNull() as? String
                                if (!totpUrl.isNullOrEmpty()) {
                                    totpUrl = totpUrl.replace("-", "")
                                    Log.d(TAG, "TOTP URL: $totpUrl")
                                    authStateManager.authfySdk.setSeed(totpUrl)
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error parsing TOTP URI", e)
                            }
                            continuation.resume(Unit)
                        }

                        is NetworkUtils.NetworkResult.Error -> {
                            Log.e(
                                TAG, "Enrollment failed: Code: ${result.code}, " +
                                    "Message: ${result.message}, " +
                                    "Error body: ${result.errorBody}"
                            )
                            // handle error case
                        }

                        is NetworkUtils.NetworkResult.Exception -> {
                            Log.e(TAG, "Enrollment exception", result.e)
                            // handle exception case
                        }
                    }
                }
            })
    }
}

private suspend fun doDeleteEnrollment(
    configPrefs: ConfigPreferences,
    userPrefs: UserPreferences,
    authStateManager: AuthStateManager,
    authService: AuthorizationService
) {
    val TAG = "AUTHCUBE:UNROLL_TOTP"
    val config = configPrefs.loadConfig()
    val deleteEnrollmentUrl = config.server
        .split("/")
        .dropLast(1)
        .joinToString("/") + "/mfa/totp/delete"

    return suspendCancellableCoroutine { continuation ->
        authStateManager.authState.performActionWithFreshTokens(
            authService,
            AuthState.AuthStateAction { accessToken, _, ex ->
                if (ex != null) {
                    Log.e(TAG, "Error fetching a fresh token: ${ex.message}")
                    return@AuthStateAction
                }

                val mainJsonObject = JsonObject()
                mainJsonObject.addProperty("username", userPrefs.getUsername())
                mainJsonObject.addProperty("verbose", false)
                val body = Gson().toJson(mainJsonObject)

                val headers = mapOf(
                    "Content-Type" to "application/json",
                    "Authorization" to "Bearer $accessToken"
                )

                runBlocking {
                    when (val result = NetworkUtils.doPost(
                        urlString = deleteEnrollmentUrl,
                        headers = headers,
                        body = body
                    )) {

                        is NetworkUtils.NetworkResult.Success -> {
                            Log.d(TAG, "Delete enrollment successful: ${result.data}")
                            continuation.resume(Unit)
                            // handle success case
                        }

                        is NetworkUtils.NetworkResult.Error -> {
                            Log.e(
                                TAG, "Delete enrollment failed: Code: ${result.code}, " +
                                    "Message: ${result.message}, " +
                                    "Error body: ${result.errorBody}"
                            )
                            // handle error case
                        }

                        is NetworkUtils.NetworkResult.Exception -> {
                            Log.e(TAG, "Delete enrollment exception", result.e)
                            // handle exception case
                        }
                    }
                }
            })
    }
}

private fun generateTOTP(authStateManager: AuthStateManager): String? {
    if (authStateManager.authfySdk.hasSeed()) {
        return authStateManager.authfySdk.generateTOTP()
    }
    Log.e("AUTHCUBE:GENERATE_TOTP", "Seed is not set")
    return null
}

private suspend fun verifyTOTP(
    totpCode: String,
    configPrefs: ConfigPreferences,
    authStateManager: AuthStateManager,
    authService: AuthorizationService
): Boolean {
    val TAG = "AUTHCUBE:VERIFY_TOTP"
    val config = configPrefs.loadConfig()
    val verifyTOTPUrl = config.server
        .split("/")
        .dropLast(1)
        .joinToString("/") + "/mfa/totp/verify"

    return suspendCancellableCoroutine { continuation ->
        authStateManager.authState.performActionWithFreshTokens(
            authService,
            AuthState.AuthStateAction { accessToken, _, ex ->
                if (ex != null) {
                    Log.e(TAG, "Error fetching a fresh token: ${ex.message}")
                    return@AuthStateAction
                }

                val mainJsonObject = JsonObject()
                mainJsonObject.addProperty("otp", totpCode)
                mainJsonObject.addProperty("verbose", false)
                val body = Gson().toJson(mainJsonObject)

                val headers = mapOf(
                    "Content-Type" to "application/json",
                    "Authorization" to "Bearer $accessToken"
                )

                runBlocking {
                    when (val result = NetworkUtils.doPost(
                        urlString = verifyTOTPUrl,
                        headers = headers,
                        body = body
                    )) {

                        is NetworkUtils.NetworkResult.Success -> {
                            Log.d(TAG, "TOTP is valid: ${result.data}")
                            continuation.resume(true)
                            // handle success case
                        }

                        is NetworkUtils.NetworkResult.Error -> {
                            Log.e(
                                TAG, "Delete enrollment failed: Code: ${result.code}, " +
                                    "Message: ${result.message}, " +
                                    "Error body: ${result.errorBody}"
                            )
                            continuation.resume(false)

                            // handle error case
                        }

                        is NetworkUtils.NetworkResult.Exception -> {
                            Log.e(TAG, "Delete enrollment exception", result.e)
                            continuation.resumeWithException(result.e)

                            // handle exception case
                        }
                    }
                }
            })
    }
}


@Preview(showBackground = true)
@Composable
fun TOTPPreview() {
    AuthfySampleTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            TOTP(
                navController = rememberNavController(),
                authStateManager = AuthStateManager(null, null, null),
                onCameraClick = {}
            )
        }
    }
}
