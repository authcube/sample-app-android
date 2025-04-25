package br.com.sec4you.authfy.app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.sec4you.authfy.app.ui.screens.home.HomeFooter
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme
import br.com.sec4you.authfy.app.ui.theme.BtnBg
import br.com.sec4you.authfy.app.ui.theme.BtnTxt
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.EndSessionResponse


// https://proandroiddev.com/jetpack-compose-tricks-conditionally-applying-modifiers-for-dynamic-uis-e3fe5a119f45
inline fun Modifier.conditional(
    condition: Boolean,
    ifTrue: Modifier.() -> Modifier,
    ifFalse: Modifier.() -> Modifier = { this }
): Modifier = if (condition) {
    then(ifTrue(Modifier))
} else {
    then(ifFalse(Modifier))
}

@Composable
fun Panel(
    modifier: Modifier,
    height: Dp,
    verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.Center
) {
    Column(
        verticalArrangement = verticalArrangement, modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = height)
    ) {}
}


@Composable
fun SimpleButton(text: String = "Button") {
    Button(
        onClick = {}
    ) {
        Text(
            text = text
        )
    }
}


@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authenticated: Boolean,
    onAuthenticatedChange: (Boolean) -> Unit,
    authStateManager: AuthStateManager,
    onCameraClick: () -> Unit
) {
    var TAG = "AUTHCUBE:SC:HO"
    val currentRoute by remember { mutableStateOf("home") }
    val context = LocalContext.current
    val userPrefs = UserPreferences(context)
    val authService = remember {
        AuthorizationService(context)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->

        if (result.resultCode != Activity.RESULT_OK) {
            return@rememberLauncherForActivityResult
        }

        // Handle the result here
        val res = result.data?.let { EndSessionResponse.fromIntent(it) }

        if (res != null) {
            // logout success
            userPrefs.clear()
            onAuthenticatedChange(false)
        }

    }

    if (!authenticated) {
        navController.navigate(Screen.AuthScreen.route)
        return
    }

    Scaffold(
        topBar = { TopHomeBar() },
        bottomBar = {
            HomeFooter(
                navController = navController,
                currentRoute = currentRoute,
                onCameraClick = onCameraClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BtnBg,
                        contentColor = BtnTxt,
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .width(280.dp)
                        .height(60.dp),
                    onClick = {
                        val authState = authStateManager.authState

                        val authServiceConfig = AuthorizationServiceConfiguration(
                            authStateManager.authState.lastTokenResponse?.request?.configuration?.discoveryDoc!!
                        )

                        val endSessionRequest = EndSessionRequest.Builder(authServiceConfig)
                            .setIdTokenHint(authState.idToken)
                            .setPostLogoutRedirectUri(
                                Uri.parse("br.com.sec4you.authfy.app.appsample:/logoutredirect")
                            )
                            .build()

                        val endSessionIntent: Intent =
                            authService.getEndSessionRequestIntent(endSessionRequest)
                        launcher.launch(endSessionIntent)
                    }
                ) {
                    Text(
                        text = "Logout",
                        fontSize = 24.sp
                    )
                }
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .conditional(isDebugEnabled(), {
                border(BorderStroke(2.dp, SolidColor(Color.Red)))
            })
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {

    }
}

@Composable
fun TopHomeBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.authcube_logo),
                contentDescription = "Splash logo",
                modifier = Modifier
                    .width(220.dp)
                    .height(80.dp)
            )
        }
    }

}


private var LOCAL_DEBUG = false;
fun isDebugEnabled(): Boolean {
    return LOCAL_DEBUG || DEBUG_IS_ENABLED.value == true
}

@RequiresApi(Build.VERSION_CODES.M)
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AuthfySampleTheme {
        LOCAL_DEBUG = false

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            HomeScreen(
                navController = rememberNavController(),
                authenticated = true,
                onAuthenticatedChange = {},
                authStateManager = AuthStateManager(null, null, null),
                onCameraClick = { }
            )
        }
    }
}
