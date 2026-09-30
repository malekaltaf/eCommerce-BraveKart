package com.malekaltaf.ecommerce_bravekart

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.malekaltaf.ecommerce_bravekart.ui.theme.ECommerceBraveKartTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginUserActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        setContent {
            ECommerceBraveKartTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("BraveKart") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.uid?.let { uid ->
            FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        document.getString("name")?.let { fetchedName ->
                            if (fetchedName.isNotBlank()) {
                                userName = fetchedName
                            }
                        }
                    }
                }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "User Icon",
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "Welcome to $userName!",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                isLoading = true
                coroutineScope.launch {
                    // Slight delay to ensure the user sees the progress bar 
                    // before the instant Firebase local cache clear
                    delay(1000) 
                    
                    FirebaseAuth.getInstance().signOut()
                    isLoading = false
                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    context.startActivity(Intent(context, LoginUserActivity::class.java))
                    (context as? ComponentActivity)?.finish()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Logout")
            }
        }
    }
}