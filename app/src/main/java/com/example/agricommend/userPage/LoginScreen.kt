package com.example.agricommend.userPage

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf<String?>(null) }


    fun loginUser() {
        errorMessage.value = null

        when {
            email.value.isBlank() -> {
                errorMessage.value = "Email cannot be empty"
            }
            password.value.isBlank() -> {
                errorMessage.value = "Password cannot be empty"
            }
            else -> {

                auth.signInWithEmailAndPassword(email.value, password.value)
                    .addOnSuccessListener {

                        navController.navigate("user_dashboard_screen") {
                            popUpTo("login_screen") { inclusive = true }
                        }
                    }
                    .addOnFailureListener { exception ->

                        errorMessage.value = "Invalid credentials: ${exception.message}"
                    }
            }
        }
    }


    fun navigateToForgotPassword() {
        navController.navigate("forgot_password_screen/false")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        // Email input
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black)
        )


        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black)
        )


        errorMessage.value?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 16.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = { loginUser() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)// Padding around the button
        ) {
            Text("Log in")
        }

        Spacer(modifier = Modifier.height(16.dp))

        ClickableText(
            text = buildAnnotatedString {
                append("Forgot Password?")
            },
            onClick = { navigateToForgotPassword() },
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}
