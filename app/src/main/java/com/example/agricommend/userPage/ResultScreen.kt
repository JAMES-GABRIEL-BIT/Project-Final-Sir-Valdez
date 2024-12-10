package com.example.agricommend.userPage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.filled.ArrowBack
import kotlin.system.exitProcess
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(navController: NavController, cropName: String, weeksToGrow: Int, hasWeed: Boolean) {
    val db = FirebaseFirestore.getInstance()
    var fertilizerRecommendation by remember { mutableStateOf("") }
    var cropType by remember { mutableStateOf("") }
    var showExitDialog by remember { mutableStateOf(false) }


    LaunchedEffect(weeksToGrow) {
        db.collection("crops")
            .document(cropName)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val fertilizer = document.get("fertilizerRecommendations") as? Map<String, String>
                    val fertilizerForWeek = fertilizer?.get(weeksToGrow.toString())
                    val type = document.get("cropType") as? String
                    fertilizerRecommendation = fertilizerForWeek ?: "No recommendation found"
                    cropType = type ?: "Unknown type"
                }
            }
    }


    BackHandler {
        showExitDialog = true
    }

    Scaffold(
        topBar = {

            TopAppBar(
                title = { Text(text = "Result Screen") },
                navigationIcon = {
                    IconButton(onClick = {

                        navController.navigate("search_page_screen") {
                            popUpTo("search_page_screen") { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to Search Page"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.elevatedCardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        text = "Crop Name: $cropName",
                        style = TextStyle(fontSize = 30.sp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Crop Type: $cropType",
                        style = TextStyle(fontSize = 30.sp),
                        modifier = Modifier.padding(bottom = 20.dp)
                    )


                    Text(
                        text = "Fertilizer Recommendation: $fertilizerRecommendation",
                        style = TextStyle(fontSize = 30.sp),
                        modifier = Modifier.padding(bottom = 25.dp)
                    )


                    if (hasWeed) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(15.dp))
                            Text(
                                text = "Your plant has a weed surrounding it. We recommend plucking it out first before applying fertilizer.",
                                style = TextStyle(fontSize = 25.sp)
                            )
                        }
                    }
                }
            }


            if (showExitDialog) {
                AlertDialog(
                    onDismissRequest = { showExitDialog = false },
                    title = { Text("Exit") },
                    text = { Text("Are you sure you want to exit the app?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showExitDialog = false
                                exitProcess(0)
                            }
                        ) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showExitDialog = false }) {
                            Text("No")
                        }
                    },
                    properties = DialogProperties(
                        dismissOnBackPress = false,
                        dismissOnClickOutside = false
                    )
                )
            }
        }
    }
}
