package com.example.agricommend.adminPage

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteCropScreen(navController: NavController) {

    val firestore = FirebaseFirestore.getInstance()

    var searchQuery by remember { mutableStateOf("") }
    var cropList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var filteredCropList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var selectedCropId by remember { mutableStateOf<String?>(null) }


    fun fetchCropsFromFirestore() {
        firestore.collection("crops")
            .get()
            .addOnSuccessListener { result ->
                cropList = result.documents.mapNotNull { doc ->
                    doc.data?.plus("id" to doc.id)
                }
                filteredCropList = cropList
            }
            .addOnFailureListener {
                Toast.makeText(navController.context, "Error fetching crops", Toast.LENGTH_SHORT).show()
            }
    }

    fun deleteCropFromFirestore(cropId: String) {
        firestore.collection("crops")
            .document(cropId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(navController.context, "Crop deleted successfully", Toast.LENGTH_SHORT).show()
                fetchCropsFromFirestore() // Refresh crop list after deletion
            }
            .addOnFailureListener {
                Toast.makeText(navController.context, "Error deleting crop", Toast.LENGTH_SHORT).show()
            }
    }

    LaunchedEffect(Unit) {
        fetchCropsFromFirestore()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Delete Crop", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
                filteredCropList = if (query.isBlank()) {
                    cropList
                } else {
                    cropList.filter { crop ->
                        (crop["cropName"] as? String)?.contains(query, ignoreCase = true) == true
                    }
                }
            },
            label = { Text("Search Crop by Name") },
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black)
        )

        Spacer(modifier = Modifier.height(16.dp))


        if (filteredCropList.isEmpty()) {
            if (searchQuery.isNotBlank()) {

                Text("No crops found matching \"$searchQuery\".", color = MaterialTheme.colorScheme.error)
            } else {
                Text("No crops available.", color = MaterialTheme.colorScheme.error)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxHeight().padding(vertical = 8.dp)) {
                items(filteredCropList.size) { index ->
                    val crop = filteredCropList[index]
                    val cropName = crop["cropName"] as? String ?: "Unknown"
                    val cropId = crop["id"] as? String ?: ""

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedCropId = cropId
                                deleteCropFromFirestore(cropId)
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(cropName, style = MaterialTheme.typography.bodyMedium)
                    }

                    Divider()
                }
            }
        }
    }
}
