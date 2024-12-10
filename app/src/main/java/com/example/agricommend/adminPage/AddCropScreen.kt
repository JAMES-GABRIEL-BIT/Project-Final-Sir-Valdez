package com.example.agricommend.adminPage

import android.widget.Toast
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCropScreen(navController: NavController) {

    val firestore = FirebaseFirestore.getInstance()
    val cropName = remember { mutableStateOf(TextFieldValue("")) }
    val cropType = remember { mutableStateOf("") }
    val fertilizerRecommendations = remember {
        mutableStateOf((1..15).associateWith { "" })
    }
    val cropTypeOptions = listOf("Leafy", "Fruit", "Bulb", "Root","Flower","Stem","Pod","Tuber","Seed","Fungi")
    var expanded by remember { mutableStateOf(false) } // State to control dropdown visibility
    var validationError by remember { mutableStateOf<String?>(null) }

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
                validationError = "All fertilizer recommendations (weeks 1 to 15) must be filled."
                false
            }
            else -> true
        }
    }

    fun saveCropData() {
        if (validateInput()) {

            val sanitizedCropName = cropName.value.text.trim().replace(Regex("[^a-zA-Z0-9_-]"), "_")

            val fertilizerRecommendationsFormatted = fertilizerRecommendations.value.mapKeys { it.key.toString() }

            val cropData = hashMapOf(
                "cropName" to cropName.value.text,
                "cropType" to cropType.value,
                "fertilizerRecommendations" to fertilizerRecommendationsFormatted
            )

            firestore.collection("crops").document(sanitizedCropName)
                .set(cropData)
                .addOnSuccessListener {
                    Log.d("AddCropScreen", "Crop added successfully")
                    Toast.makeText(navController.context, "Crop Added Successfully", Toast.LENGTH_SHORT).show()

                    navController.popBackStack()
                }
                .addOnFailureListener { exception ->
                    Log.e("AddCropScreen", "Error adding crop", exception)
                    Toast.makeText(navController.context, "Error adding crop: ${exception.message}", Toast.LENGTH_SHORT).show()
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
        Text("Add New Crop", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = cropName.value,
            onValueChange = { cropName.value = it },
            label = { Text("Crop Name") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = cropType.value,
                onValueChange = {},
                readOnly = true,
                label = { Text("Crop Type") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                enabled = true,
                textStyle = TextStyle(fontSize = 14.sp)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                cropTypeOptions.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            cropType.value = type
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Fertilizer Recommendations", style = MaterialTheme.typography.bodyMedium)

        for (week in 1..15) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    "Week $week",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                )

                OutlinedTextField(
                    value = fertilizerRecommendations.value[week] ?: "",
                    onValueChange = {
                        val newRecommendations = fertilizerRecommendations.value.toMutableMap()
                        newRecommendations[week] = it
                        fertilizerRecommendations.value = newRecommendations
                    },
                    label = { Text("Enter Fertilizer") },
                    modifier = Modifier
                        .width(150.dp)
                        .padding(start = 8.dp),
                    textStyle = TextStyle(fontSize = 12.sp, color = Color.Black),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = { saveCropData() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Crop")
        }
    }
}
