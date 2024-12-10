package com.example.agricommend.adminPage

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateCropScreen(navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()


    val searchQuery = remember { mutableStateOf(TextFieldValue("")) }
    val cropFound = remember { mutableStateOf(false) }
    val cropId = remember { mutableStateOf("") }
    val cropName = remember { mutableStateOf(TextFieldValue("")) }
    val cropType = remember { mutableStateOf("") }
    val fertilizerRecommendations = remember { mutableStateOf((1..15).associateWith { "" }.toMutableMap()) }
    val isLoading = remember { mutableStateOf(false) }


    val cropTypeOptions = listOf("Leafy", "Fruit", "Bulb", "Root","Flower","Stem","Pod","Tuber","Seed","Fungi")
    var cropTypeDropdownExpanded by remember { mutableStateOf(false) }


    var validationError by remember { mutableStateOf<String?>(null) }


    fun searchCrop() {
        isLoading.value = true
        firestore.collection("crops")
            .whereEqualTo("cropName", searchQuery.value.text.trim())
            .get()
            .addOnSuccessListener { documents ->
                isLoading.value = false
                if (documents.isEmpty) {
                    Toast.makeText(navController.context, "Crop not found", Toast.LENGTH_SHORT).show()
                    cropFound.value = false
                } else {
                    val document = documents.first()
                    cropId.value = document.id
                    val cropData = document.data
                    cropName.value = TextFieldValue(cropData["cropName"] as String? ?: "")
                    cropType.value = cropData["cropType"] as String? ?: ""
                    @Suppress("UNCHECKED_CAST")
                    val recommendations = cropData["fertilizerRecommendations"] as? Map<String, String> ?: emptyMap()
                    fertilizerRecommendations.value = recommendations.mapKeys { it.key.toInt() }.toMutableMap()
                    cropFound.value = true
                }
            }
            .addOnFailureListener {
                isLoading.value = false
                Toast.makeText(navController.context, "Error searching for crop", Toast.LENGTH_SHORT).show()
            }
    }


    fun validateInput(): Boolean {
        return when {
            cropName.value.text.isBlank() -> {
                validationError = "Crop name cannot be blank."
                false
            }
            cropType.value.isBlank() -> {
                validationError = "Crop type must be selected."
                false
            }
            fertilizerRecommendations.value.values.any { it.isBlank() } -> {
                validationError = "All fertilizer recommendations must be filled."
                false
            }
            else -> true
        }
    }


    fun updateCropData() {
        if (validateInput()) {
            val updatedData = hashMapOf(
                "cropName" to cropName.value.text,
                "cropType" to cropType.value,
                "fertilizerRecommendations" to fertilizerRecommendations.value.mapKeys { it.key.toString() }
            )

            firestore.collection("crops").document(cropId.value)
                .update(updatedData)
                .addOnSuccessListener {
                    Toast.makeText(
                        navController.context,
                        "Crop Updated Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.popBackStack()
                }
                .addOnFailureListener {
                    Toast.makeText(navController.context, "Error updating crop", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(navController.context, validationError, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Update Crop", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            label = { Text("Search Crop by Name") },
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black)
        )

        Button(
            onClick = { searchCrop() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Search")
        }

        if (isLoading.value) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp))
        }

        if (cropFound.value) {
            Spacer(modifier = Modifier.height(16.dp))


            OutlinedTextField(
                value = cropName.value,
                onValueChange = { cropName.value = it },
                label = { Text("Crop Name") },
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black)
            )


            ExposedDropdownMenuBox(
                expanded = cropTypeDropdownExpanded,
                onExpandedChange = { cropTypeDropdownExpanded = !cropTypeDropdownExpanded }
            ) {
                TextField(
                    value = cropType.value,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Crop Type") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = cropTypeDropdownExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .padding(bottom = 16.dp)
                )

                ExposedDropdownMenu(
                    expanded = cropTypeDropdownExpanded,
                    onDismissRequest = { cropTypeDropdownExpanded = false }
                ) {
                    cropTypeOptions.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                cropType.value = type
                                cropTypeDropdownExpanded = false
                            }
                        )
                    }
                }
            }


            Text("Fertilizer Recommendations", style = MaterialTheme.typography.bodyMedium)

            (1..15).forEach { week ->
                OutlinedTextField(
                    value = fertilizerRecommendations.value[week] ?: "",
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                    onValueChange = {
                        val updatedRecommendations = fertilizerRecommendations.value.toMutableMap()
                        updatedRecommendations[week] = it
                        fertilizerRecommendations.value = updatedRecommendations
                    },
                    label = { Text("Week $week") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { updateCropData() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Crop")
            }
        }
    }
}
