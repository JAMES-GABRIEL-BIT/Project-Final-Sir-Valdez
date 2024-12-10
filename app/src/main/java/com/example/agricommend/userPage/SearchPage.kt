package com.example.agricommend.userPage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.focus.onFocusChanged
import com.google.firebase.firestore.FirebaseFirestore
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(navController: NavController) {
    // Mutable states
    var query by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val db = FirebaseFirestore.getInstance()

    val coroutineScope = rememberCoroutineScope()

    fun searchCrop(cropName: String) {
        errorMessage = ""
        isLoading = true


        db.collection("crops")
            .get()
            .addOnSuccessListener { result ->
                var cropFound = false
                for (document in result) {
                    val cropNameFromDB = document.getString("cropName") ?: ""
                    if (cropNameFromDB.lowercase() == cropName.lowercase()) {
                        cropFound = true
                        try {
                            navController.navigate("forms_screen/${document.id}/${cropNameFromDB}") {
                                launchSingleTop = true
                            }
                        } catch (e: Exception) {
                            errorMessage = "Navigation error occurred. Please try again."
                        }
                    }
                }

                if (!cropFound) {
                    errorMessage = "Crop not found. Please check the name and try again."
                }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
                errorMessage = "An error occurred while searching. Please try again later."
            }
    }


    LaunchedEffect(query) {
        if (query.isNotEmpty()) {
            coroutineScope.launch {
                delay(1000)
                searchCrop(query)
            }
        } else {
            errorMessage = ""
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.LightGray),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search") },
            placeholder = { Text("Search for a crop") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .onFocusChanged { focusState -> isFocused = focusState.isFocused },
            singleLine = true,
            isError = errorMessage.isNotEmpty(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Clear"
                        )
                    }
                }
            },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )


        Spacer(modifier = Modifier.height(45.dp))


        Text(
            text = "Search for a vegetable crop",
            style = TextStyle(fontSize = 30.sp),
            modifier = Modifier.padding(bottom = 16.dp),
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))


        if (isLoading) {
            CircularProgressIndicator()
        }


        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
