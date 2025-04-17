package br.com.sec4you.authfy.app.ui.screens.risk

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.sec4you.authfy.app.AuthStateManager
import br.com.sec4you.authfy.app.ConfigPreferences
import br.com.sec4you.authfy.app.NetworkUtils
import br.com.sec4you.authfy.app.Screen
import br.com.sec4you.authfy.app.UserPreferences
import br.com.sec4you.authfy.app.ui.screens.home.HomeFooter
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme
import br.com.sec4you.authfy.app.ui.theme.BtnBg
import br.com.sec4you.authfy.app.ui.theme.BtnTxt
import br.com.sec4you.authfy.sdk.AuthfySdk
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.launch


@Composable
fun Evaluate(
    navController: NavController,
    authStateManager: AuthStateManager,
    onCameraClick: () -> Unit
) {
    val TAG = "AUTHCUBE:EVALUATE_RISK"

    var key by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    var attributesList by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    var postEvaluate by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val configPrefs = ConfigPreferences(context)
    val userPrefs = UserPreferences(context)
    val scope = rememberCoroutineScope()

    val accessToken = authStateManager.authState.accessToken

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    "Evaluation Result",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    dialogMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        "Close",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )
    }

    Scaffold(
        topBar = { RiskHeader(navController, Screen.RiskScreen.route) },
        bottomBar = {
            HomeFooter(
                navController,
                currentRoute = "risk",
                onCameraClick = onCameraClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = key,
                onValueChange = { key = it },
                label = { Text("Key") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(4.dp)
                    ),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            TextField(
                value = value,
                onValueChange = { value = it },
                label = { Text("Value") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(4.dp)
                    ),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = BtnBg,
                    contentColor = BtnTxt,
                ),
                shape = RoundedCornerShape(12.dp),
                onClick = {
                    if (key.isNotEmpty() && value.isNotEmpty()) {
                        attributesList = attributesList + (key to value)

                        key = ""
                        value = ""
                        Toast.makeText(context, "Attribute added", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Add Attribute")
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Current Attributes:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(attributesList) { (k, v) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("$k: $v")
                        IconButton(
                            onClick = {
                                attributesList = attributesList.filterNot { it.first == k }
                            }
                        ) {
                            Icon(Icons.Default.Delete, "Remove attribute")
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Device Enrollment:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = postEvaluate,
                    onCheckedChange = { postEvaluate = it }
                )
            }

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = BtnBg,
                    contentColor = BtnTxt,
                ),
                shape = RoundedCornerShape(12.dp),
                onClick = {
                    if (accessToken == null) {
                        Toast.makeText(
                            context,
                            "No valid token found. Please login again.",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        return@Button
                    }

                    scope.launch {

                        try {
                            val resp = evaluateData(
                                postEvaluate = postEvaluate,
                                authStateManager = authStateManager,
                                additionalInputs = attributesList,
                                configPrefs = configPrefs,
                                userPrefs = userPrefs
                            )

                            // Log.d(TAG, "Evaluate response: $resp")
                            dialogMessage = "$resp"
                            showDialog = true

                        } catch (e: Exception) {
                            dialogMessage = "Error: ${e.message}"
                            showDialog = true
                            Log.e(TAG, "Error: ${e.message}")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 16.dp)
            ) {
                Text("Evaluate")
            }
        }
    }
}

suspend fun evaluateData(
    additionalInputs: List<Pair<String, String>>,
    postEvaluate: Boolean,
    authStateManager: AuthStateManager,
    configPrefs: ConfigPreferences,
    userPrefs: UserPreferences
): Map<String, Any>? {

    val config = configPrefs.loadConfig()
    val serverUrl = config.server
    val evaluateRiskUrl = serverUrl
        .split("/")
        .dropLast(1)
        .joinToString("/") + "/risk/evaluate"

    val mainJsonObject = JsonObject()
    val additionalInputsArray = JsonArray()
    for (attribute in additionalInputs) {
        val inputObject = JsonObject()
        inputObject.addProperty(attribute.first, attribute.second)
        additionalInputsArray.add(inputObject)
    }
    mainJsonObject.add("additional_inputs", additionalInputsArray)
    mainJsonObject.addProperty("username", userPrefs.getUsername())

    val dna = authStateManager.authfySdk.deviceInfo
    val accessToken = authStateManager.authState.accessToken

    mainJsonObject.addProperty("dna", dna)
    mainJsonObject.addProperty("verbose", false)
    val body = Gson().toJson(mainJsonObject)

    val headers = mapOf(
        "Content-Type" to "application/json",
        "Authorization" to "Bearer $accessToken"
    )

    when (val result = NetworkUtils.doPost(
        urlString = evaluateRiskUrl,
        headers = headers,
        body = body
    )) {
        is NetworkUtils.NetworkResult.Success -> {
            if (postEvaluate) {

                Log.d(
                    "AUTHCUBE:EVALUATE",
                    "Success requesting evaluate. Going for post-evaluate..."
                )

                val postJsonObject = JsonObject()
                val transactionId = extractTransactionId(result.data)
                postJsonObject.add("evaluate-data", transactionId)
                postJsonObject.addProperty("secondary-auth", true)
                postJsonObject.addProperty("username", userPrefs.getUsername())
                postJsonObject.addProperty("verbose", false)
                Log.d("AUTHCUBE:POST_EVAL", "post-evaluate body: $postJsonObject")

                val postEvalBody = Gson().toJson(postJsonObject)
                val postEvalRiskUrl = serverUrl
                    .split("/")
                    .dropLast(1)
                    .joinToString("/") + "/risk/post-evaluate"

                when (val postEvaluateResult = NetworkUtils.doPost(
                    urlString = postEvalRiskUrl,
                    headers = headers,
                    body = postEvalBody
                )) {
                    is NetworkUtils.NetworkResult.Success -> {
                        Log.d(
                            "AUTHCUBE:POST_EVAL",
                            "post-evaluate response: ${postEvaluateResult.data}"
                        )
                        return postEvaluateResult.data
                    }

                    is NetworkUtils.NetworkResult.Error -> {
                        Log.e(
                            "AUTHCUBE:POST_EVAL",
                            "Failed requesting post-evaluate. Code: ${postEvaluateResult.code}, Message: ${postEvaluateResult.message}"
                        )
                    }

                    is NetworkUtils.NetworkResult.Exception -> {
                        Log.e(
                            "AUTHCUBE:POST_EVAl",
                            "Failed requesting post-evaluate.",
                            postEvaluateResult.e
                        )
                    }
                }

            } else {
                return result.data
            }

        }

        is NetworkUtils.NetworkResult.Error -> {
            Log.e(
                "AUTHCUBE:EVALUATE",
                "Failed requesting evaluate. Code: ${result.code}, Message: ${result.message}"
            )
        }

        is NetworkUtils.NetworkResult.Exception -> {
            Log.e("AUTHCUBE:EVALUATE", "Failed requesting evaluate.", result.e)
        }
    }

    return null
}

fun extractTransactionId(response: Map<String, Any>?): JsonElement {
    val transactionId = ((((response
        ?.get("links") as? List<*>)
        ?.firstOrNull() as? Map<*, *>)
        ?.get("parameters") as? Map<*, *>)
        ?.get("evaluate-data") as? Map<*, *>)
        ?.get("transaction_id") as? String

    return JsonObject().apply {
        addProperty("transaction_id", transactionId)
    }
}

@Preview(showBackground = true)
@Composable
fun EvaluatePreview() {
    AuthfySampleTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Evaluate(
                navController = rememberNavController(),
                authStateManager = AuthStateManager(null, null, null),
                onCameraClick = {}
            )
        }
    }
}
