package com.example.agricommend

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController, isAdmin: Boolean) {
    val auth = FirebaseAuth.getInstance()


    val email = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val successMessage = remember { mutableStateOf<String?>(null) }


    fun resetPassword() {
        errorMessage.value = null
        successMessage.value = null

        if (email.value.isBlank()) {
            errorMessage.value = "Email cannot be empty"
        } else {

            auth.sendPasswordResetEmail(email.value)
                .addOnSuccessListener {
                    successMessage.value = "Password reset email sent!"
                }
                .addOnFailureListener { exception ->
                    errorMessage.value = "Error: ${exception.message}"
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
        Text(
            text = if (isAdmin) "Admin Forgot Password" else "User Forgot Password",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Enter Current Email") },
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

        successMessage.value?.let {
            Text(it, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 16.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = { resetPassword() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        ) {
            Text("Send Reset Email")
        }
    }
}
