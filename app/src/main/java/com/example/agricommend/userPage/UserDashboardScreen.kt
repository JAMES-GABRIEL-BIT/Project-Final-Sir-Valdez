package com.example.agricommend.userPage

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()


    val username = remember { mutableStateOf("") }


    LaunchedEffect(auth.currentUser?.uid) {
        auth.currentUser?.uid?.let { userId ->
            // Fetch user data from Firestore using UID
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Set the username from Firestore document
                        username.value = document.getString("username") ?: "User"
                    } else {
                        Toast.makeText(navController.context, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(navController.context, "Error fetching user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


    fun logOut() {
        FirebaseAuth.getInstance().signOut()
        navController.navigate("initial_screen") {
            popUpTo("initial_screen") { inclusive = true }
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
            text = "Welcome to Agri-commend, ${username.value}!",
            style = MaterialTheme.typography.headlineLarge,
        )

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = { navController.navigate("search_page_screen") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        ) {
            Text("Go to Search Page")
        }

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = { logOut() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        ) {
            Text("Log Out")
        }
    }
}
