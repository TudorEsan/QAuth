package net.theluckycoder.qr.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import net.theluckycoder.qr.viewmodel.LoginViewModel

class LoginScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() = Box {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = viewModel<LoginViewModel>()

        val tokens by viewModel.userDataStore.tokens.collectAsState(initial = null)

        LaunchedEffect(tokens) {
            if (tokens != null) {
                navigator.pop()
            }
        }

        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var hidePassword by remember { mutableStateOf(true) }

        Column(Modifier.align(Alignment.Center)) {
            TextField(
                email,
                onValueChange = { email = it },
                label = { Text("Email") },
                placeholder = { Text("Email") }
            )

            Spacer(Modifier.height(16.dp))

            TextField(
                password,
                onValueChange = { password = it },
                label = { Text("Password") },
                placeholder = { Text("Password") },
                visualTransformation = if (hidePassword) PasswordVisualTransformation() else VisualTransformation.None,
                trailingIcon = {
                    IconButton(onClick = { hidePassword = !hidePassword }) {
                        Icon(
                            if (hidePassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                }
            )

            Spacer(Modifier.height(8.dp))

            Button(modifier = Modifier.align(Alignment.End), onClick = { viewModel.login(email, password) }) {
                Text("Logare")
            }
        }
    }
}

@Preview
@Composable
fun LoginPreview() {
    Navigator(LoginScreen())
}