package com.malekaltaf.ecommerce_bravekart

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.malekaltaf.ecommerce_bravekart.ui.theme.ECommerceBraveKartTheme

class SignupUserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ECommerceBraveKartTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SignupScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SignupScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Gender",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val genderOptions = listOf("Male", "Female")
                genderOptions.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(end = 24.dp)
                            .clickable { gender = option }
                    ) {
                        RadioButton(
                            selected = (gender == option),
                            onClick = { gender = option }
                        )
                        Text(text = option, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // Validation
                when {
                    name.isBlank() -> errorMessage = "Hey! We'd love to know what to call you! \uD83D\uDC4B"
                    email.isBlank() -> errorMessage = "Looks like you forgot your email address! \uD83D\uDCE7"
                    password.isBlank() -> errorMessage = "A secret password is required to enter the club. \uD83D\uDD11"
                    password.length < 6 -> errorMessage = "Make it a bit longer! 6 characters minimum. \uD83D\uDE09"
                    gender.isBlank() -> errorMessage = "Please select a gender so we can tailor your experience! \uD83D\uDE4B\u200D\u2642\uFE0F"
                    phone.isBlank() -> errorMessage = "We need your digits to keep you updated! \uD83D\uDCF1"
                    else -> {
                        errorMessage = null
                        isLoading = true
                        val auth = FirebaseAuth.getInstance()
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    
                                    // Set Display Name in Firebase Auth Profile
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build()
                                    user?.updateProfile(profileUpdates)

                                    val db = FirebaseFirestore.getInstance()
                                    val userData = hashMapOf(
                                        "name" to name,
                                        "email" to email,
                                        "gender" to gender,
                                        "phone" to phone
                                    )
                                    user?.uid?.let { uid ->
                                        // Save user data asynchronously, but don't block the UI waiting for it 
                                        // since Firestore might take a long time to return if it's establishing a connection
                                        db.collection("users").document(uid).set(userData)
                                    }
                                    
                                    isLoading = false
                                    Toast.makeText(context, "Yay! You're officially in! \uD83C\uDF8A", Toast.LENGTH_SHORT).show()
                                    // When User Click Register Redirect to Login Screen
                                    context.startActivity(Intent(context, LoginUserActivity::class.java))
                                    (context as? ComponentActivity)?.finish()
                                } else {
                                    isLoading = false
                                    val exception = task.exception
                                    val errorMsg = if (exception is FirebaseAuthUserCollisionException) {
                                        "This email is already registered! Try logging in instead. \uD83D\uDE05"
                                    } else {
                                        "Oops, something went wrong: ${exception?.message} \uD83D\uDE22"
                                    }
                                    errorMessage = errorMsg
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
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
                Text("Register")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Already have an account?")
            TextButton(onClick = {
                // Redirect back to Login Screen
                context.startActivity(Intent(context, LoginUserActivity::class.java))
                (context as? ComponentActivity)?.finish()
            }) {
                Text("Login")
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
