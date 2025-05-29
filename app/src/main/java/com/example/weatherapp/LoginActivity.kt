package com.example.weatherapp

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import android.content.Intent
import com.example.weatherapp.RegisterActivity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.ui.theme.WeatherAppTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginPage(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun LoginPage(modifier: Modifier = Modifier) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val activity = LocalContext.current as? Activity

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bem-vindo/a!",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.size(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Digite seu e-mail") },
            modifier = Modifier.fillMaxWidth(0.9f)
        )

        Spacer(modifier = Modifier.size(24.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Digite sua senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(0.9f)
        )

        Spacer(modifier = Modifier.size(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        activity?.startActivity(
                            Intent(activity, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        )
                    }
                },
                enabled = email.isNotEmpty() && password.isNotEmpty(),
                modifier = Modifier.weight(1f)
            ) {
                Text("Login")
            }


            Button(
                onClick = {
                    email = ""
                    password = ""
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Limpar")
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        Button(
            onClick = {
                activity?.startActivity(Intent(activity, RegisterActivity::class.java))
            },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Text("Criar nova conta")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    WeatherAppTheme {
        LoginPage()
    }
}
