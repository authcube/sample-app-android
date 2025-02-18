package br.com.sec4you.authfy.app.ui.screens.config

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.sec4you.authfy.app.Config
import br.com.sec4you.authfy.app.ConfigPreferences
import br.com.sec4you.authfy.app.ConfigTextField
import br.com.sec4you.authfy.app.ToggleButton
import br.com.sec4you.authfy.app.ui.theme.AuthfySampleTheme
import br.com.sec4you.authfy.app.ui.theme.BtnBg
import br.com.sec4you.authfy.app.ui.theme.BtnTxt
import br.com.sec4you.authfy.app.ui.theme.GrayTxt

@Composable
fun VerticalConfig(configPrefs: ConfigPreferences) {
    val context = LocalContext.current
    val configState = remember { mutableStateOf(Config()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "CONNECT PARAMETERS",
            color = GrayTxt,
            fontWeight = FontWeight.Bold
        )

        ConfigTextField(
            label = "Server",
            value = configState.value.server,
            onValueChange = { configState.value = configState.value.copy(server = it) },
            modifier = Modifier.width(280.dp)
        )
        ConfigTextField(
            label = "Client Id",
            value = configState.value.clientId,
            onValueChange = { configState.value = configState.value.copy(clientId = it) },
            modifier = Modifier.width(280.dp)
        )
        ConfigTextField(
            label = "App Name vert",
            value = configState.value.appName,
            onValueChange = { configState.value = configState.value.copy(appName = it) },
            modifier = Modifier.width(280.dp)
        )

        Spacer(modifier = Modifier.size(12.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(280.dp)
        ) {
            Text(
                text = "Enroll with Risk",
                color = GrayTxt,
                fontWeight = FontWeight.Bold
            )
            ToggleButton(
                checked = configState.value.enrollWithRisk,
                onCheckedChange = {
                    configState.value = configState.value.copy(enrollWithRisk = it)
                }
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(280.dp)
        ) {
            Text(
                text = "PKCE",
                color = GrayTxt,
                fontWeight = FontWeight.Bold
            )
            ToggleButton(
                checked = configState.value.pkce,
                onCheckedChange = { configState.value = configState.value.copy(pkce = it) }
            )
        }

        Spacer(modifier = Modifier.size(12.dp))

        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = BtnBg,
                contentColor = BtnTxt,
            ),
            shape = RoundedCornerShape(12.dp),
            onClick = {
                configPrefs.saveConfig(configState.value)
                Toast.makeText(context, "Successfully saved configuration", Toast.LENGTH_SHORT)
                    .show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .height(60.dp)
        ) {
            Text(
                text = "Save",
                fontSize = 24.sp
            )
        }
    }
}

@Composable
fun HorizontalConfig(configPrefs: ConfigPreferences) {
    val context = LocalContext.current
    val configState = remember { mutableStateOf(Config()) }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title at the top
        Text(
            text = "CONNECT PARAMETERS",
            color = GrayTxt,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        // Row containing the input fields and toggles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left column: text fields
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 48.dp)
            ) {
                ConfigTextField(
                    label = "Server",
                    value = configState.value.server,
                    onValueChange = { configState.value = configState.value.copy(server = it) },
                    modifier = Modifier.width(420.dp)
                )
                ConfigTextField(
                    label = "Client Id",
                    value = configState.value.clientId,
                    onValueChange = { configState.value = configState.value.copy(clientId = it) },
                    modifier = Modifier.width(420.dp)
                )
                ConfigTextField(
                    label = "App Name",
                    value = configState.value.appName,
                    onValueChange = { configState.value = configState.value.copy(appName = it) },
                    modifier = Modifier.width(420.dp)
                )
            }

            // Right column: toggle rows
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 48.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enroll with Risk",
                        color = GrayTxt,
                        fontWeight = FontWeight.Bold
                    )
                    ToggleButton(
                        checked = configState.value.enrollWithRisk,
                        onCheckedChange = {
                            configState.value = configState.value.copy(enrollWithRisk = it)
                        }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PKCE",
                        color = GrayTxt,
                        fontWeight = FontWeight.Bold
                    )
                    ToggleButton(
                        checked = configState.value.pkce,
                        onCheckedChange = { configState.value = configState.value.copy(pkce = it) }
                    )
                }
            }
        }

        // Save button at the bottom
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = BtnBg,
                contentColor = BtnTxt
            ),
            shape = RoundedCornerShape(12.dp),
            onClick = {
                configPrefs.saveConfig(configState.value)
                Toast.makeText(context, "Successfully saved configuration", Toast.LENGTH_SHORT)
                    .show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 64.dp)
                .height(60.dp)
        ) {
            Text(
                text = "Save configuration",
                fontSize = 24.sp
            )
        }
    }
}

@Preview(
    showSystemUi = true,
    device = "spec:width=673dp,height=841dp,orientation=landscape"
)
@Composable
fun ConfigScreenPreview() {
    val configPrefs = ConfigPreferences(LocalContext.current)
    AuthfySampleTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            HorizontalConfig(configPrefs = configPrefs)
        }
    }
}
