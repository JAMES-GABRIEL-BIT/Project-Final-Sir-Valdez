package com.example.agricommend.userPage

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()


    val username = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }


    val errorMessage = remember { mutableStateOf<String?>(null) }


    val emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")


    fun checkIfCredentialsExist(username: String, email: String, onResult: (Boolean) -> Unit) {
        firestore.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    onResult(true)
                } else {
                    firestore.collection("users")
                        .whereEqualTo("email", email)
                        .get()
                        .addOnSuccessListener { emailDocuments ->
                            onResult(!emailDocuments.isEmpty)
                        }
                        .addOnFailureListener {
                            onResult(false)
                        }
                }
            }
            .addOnFailureListener {
                onResult(false)
            }
    }


    fun signUp() {
        errorMessage.value = null

        when {
            username.value.length < 8 -> {
                errorMessage.value = "Username must be at least 8 characters long"
            }
            !emailPattern.matcher(email.value).matches() -> {
                errorMessage.value = "Invalid email format"
            }
            password.value.length < 6 -> {
                errorMessage.value = "Password must be at least 6 characters long"
            }
            else -> {

                checkIfCredentialsExist(username.value, email.value) { exists ->
                    if (exists) {
                        errorMessage.value = "Username or email already exists"
                    } else {

                        auth.createUserWithEmailAndPassword(email.value, password.value)
                            .addOnSuccessListener {

                                val userData = hashMapOf(
                                    "username" to username.value,
                                    "email" to email.value
                                )
                                firestore.collection("users")
                                    .document(auth.currentUser?.uid ?: "")
                                    .set(userData)
                                    .addOnSuccessListener {
                                        Toast.makeText(navController.context, "Sign-up Successful", Toast.LENGTH_SHORT).show()
                                        navController.navigate("login_screen")
                                    }
                                    .addOnFailureListener {
                                        errorMessage.value = "Error saving user data"
                                    }
                            }
                            .addOnFailureListener {
                                errorMessage.value = "Error creating user: ${it.message}"
                            }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sign Up", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = username.value,
            onValueChange = { username.value = it },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black)
        )


        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black)
        )


        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
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
            onClick = { signUp() },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Sign up")
        }
    }
}
