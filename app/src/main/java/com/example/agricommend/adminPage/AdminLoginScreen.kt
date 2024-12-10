package com.example.agricommend.adminPage

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginScreen(navController: NavController) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val isLoading = remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") },  modifier = Modifier
                .fillMaxWidth() // Occupy full width
                .padding(bottom = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth() // Occupy full width
                .padding(bottom = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Error Message
        if (errorMessage.value != null) {
            Text(
                text = errorMessage.value!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }

        Button(
            onClick = {

                if (!isValidEmail(email.value)) {
                    errorMessage.value = "Please enter a valid email address."
                    return@Button
                }


                if (password.value.length < 6) {
                    errorMessage.value = "Password must be at least 6 characters long."
                    return@Button
                }

                isLoading.value = true

                db.collection("users")
                    .whereEqualTo("email", email.value)
                    .whereEqualTo("isAdmin", true) // Check if the user is an admin
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            auth.signInWithEmailAndPassword(email.value, password.value)
                                .addOnCompleteListener { task ->
                                    isLoading.value = false
                                    if (task.isSuccessful) {
                                        navController.navigate("admin_dashboard_screen")
                                    } else {
                                        val errorCode = (task.exception as? FirebaseAuthException)?.errorCode
                                        when (errorCode) {
                                            "ERROR_INVALID_EMAIL" -> errorMessage.value = "Invalid email format."
                                            "ERROR_USER_NOT_FOUND" -> errorMessage.value = "No account found with this email."
                                            "ERROR_WRONG_PASSWORD" -> errorMessage.value = "Incorrect password."
                                            else -> errorMessage.value = "Authentication failed. Try again."
                                        }
                                    }
                                }
                        } else {
                            isLoading.value = false
                            errorMessage.value = "This email is not registered as an admin."
                        }
                    }
                    .addOnFailureListener { exception ->
                        isLoading.value = false
                        errorMessage.value = "Error verifying admin status: ${exception.message}"
                    }
            },
            enabled = !isLoading.value
        ) {
            if (isLoading.value) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Log in as Admin")
            }
        }

        // Forgot Password Link
        TextButton(
            onClick = {
                // Navigate to Forgot Password Screen
                navController.navigate("forgot_password_screen/true")
            }
        ) {
            Text("Forgot Password?",
                color = Color.Black)
        }
    }
}

// Email validation function
fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
