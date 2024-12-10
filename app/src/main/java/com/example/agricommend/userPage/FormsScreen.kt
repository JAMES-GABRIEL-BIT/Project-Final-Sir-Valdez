package com.example.agricommend.userPage

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormsScreen(navController: NavController, cropName: String) {

    val weeksToGrow = remember { mutableStateOf(1) }
    val hasWeed = remember { mutableStateOf<Boolean?>(null) }
    val errorMessage = remember { mutableStateOf("") }
    val weekOptions = (1..15).toList()
    var expanded by remember { mutableStateOf(false) }

    fun validateInput(): Boolean {

        return if (weeksToGrow.value < 1 || weeksToGrow.value > 15) {
            errorMessage.value = "Please select a valid number of weeks."
            false
        } else if (hasWeed.value == null) {
            errorMessage.value = "Please answer if the plant has weed surrounding it."
            false
        } else if (cropName.isBlank()) {
            errorMessage.value = "Please enter a crop name."
            false
        } else {
            errorMessage.value = ""
            true
        }
    }

    fun submitForm() {
        if (validateInput()) {

            navController.navigate("result_screen/${cropName}/${weeksToGrow.value}/${hasWeed.value}") {
                launchSingleTop = true
            }
        } else {
            Toast.makeText(navController.context, errorMessage.value, Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Fill in the details")

        Spacer(modifier = Modifier.height(16.dp))

        // Weed question with radio buttons
        Text("Does your plant have a weed surrounding it?")
        Row {
            RadioButton(selected = hasWeed.value == true, onClick = { hasWeed.value = true })
            Text("Yes")
            RadioButton(selected = hasWeed.value == false, onClick = { hasWeed.value = false })
            Text("No")
        }

        Spacer(modifier = Modifier.height(16.dp))


        Text("How many weeks does the crop has after planting?")
        OutlinedTextField(
            value = weeksToGrow.value.toString(),
            onValueChange = {},
            textStyle = MaterialTheme.typography.bodySmall.copy(color = Color.Black),
            label = { Text("Weeks", color = Color.Black) },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Drop Down"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )


        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            weekOptions.forEach { week ->
                DropdownMenuItem(
                    text = { Text("$week weeks") },
                    onClick = {
                        weeksToGrow.value = week
                        expanded = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        if (errorMessage.value.isNotEmpty()) {
            Text(
                text = errorMessage.value,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))


        Button(onClick = { submitForm() }) {
            Text("Submit")
        }
    }
}
