package com.example.agricommend

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun InitialScreen(navController: NavController) {
    Column(modifier = Modifier.
    fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { navController.navigate("login_screen") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green) ) {
            Text("Log in as a user")
        }

        TextButton(onClick = { navController.navigate("signup_screen") }) {
            Text("Don't have an account? Sign Up",
                color = Color.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("admin_login_screen") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) {
            Text("Log in as an admin",
                color = Color.Black)
        }
    }
}
