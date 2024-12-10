package com.example.agricommend.adminPage

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
fun AdminDashboardScreen(navController: NavController) {
    val crops = remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    val searchQuery = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    val firestore = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title and welcome text
        Text("Admin Dashboard", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Welcome, Admin", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = {
                searchQuery.value = it
                searchCrop(searchQuery.value, firestore, crops, errorMessage)
            },
            label = { Text("Search Crop by Name") },
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedTextColor = Color.Black)
        )

        errorMessage.value?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (crops.value.isNotEmpty()) {
            crops.value.forEach { crop ->
                Text("Crop Name: ${crop["cropName"]}", style = MaterialTheme.typography.bodyMedium)
                Text("Crop Type: ${crop["cropType"]}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }


        Spacer(modifier = Modifier.weight(1f))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                navController.navigate("add_crop_screen")
            },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Crop")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                navController.navigate("update_crop_screen")
            },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Crop")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                navController.navigate("delete_crop_screen")
            },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete Crop")
            }

            Spacer(modifier = Modifier.height(25.dp))

            Button(onClick = {
                logOut(navController)
            }) {
                Text("Log Out")
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

fun searchCrop(
    query: String,
    firestore: FirebaseFirestore,
    crops: MutableState<List<Map<String, Any>>>,
    errorMessage: MutableState<String?>
) {
    if (query.isEmpty()) {
        crops.value = emptyList()
        errorMessage.value = null
        return
    }

    firestore.collection("crops")
        .whereEqualTo("cropName", query)
        .get()
        .addOnSuccessListener { result ->
            if (result.isEmpty) {
                crops.value = emptyList()
                errorMessage.value = "No crops found with the name: $query"
            } else {
                crops.value = result.map { document ->
                    document.data
                }
                errorMessage.value = null
            }
        }
        .addOnFailureListener {
            crops.value = emptyList()
            errorMessage.value = "Error searching for crops"
        }
}


fun logOut(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    auth.signOut()


    navController.navigate("admin_login_screen") {
        // Clear the back stack
        popUpTo("admin_dashboard_screen") { inclusive = true }
    }
}
