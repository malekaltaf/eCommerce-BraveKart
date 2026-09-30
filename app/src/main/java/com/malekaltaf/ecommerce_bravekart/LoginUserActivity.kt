package com.malekaltaf.ecommerce_bravekart

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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.malekaltaf.ecommerce_bravekart.ui.theme.ECommerceBraveKartTheme

class LoginUserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ECommerceBraveKartTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome Back!",
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

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                when {
                    email.isBlank() -> errorMessage = "Oops! Did you forget your email? \uD83D\uDE05"
                    password.isBlank() -> errorMessage = "Hold on! The password field feels empty. \uD83E\uDD14"
                    else -> {
                        errorMessage = null
                        isLoading = true
                        val auth = FirebaseAuth.getInstance()
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Welcome back to BraveKart! \uD83C\uDF89", Toast.LENGTH_SHORT).show()
                                    context.startActivity(Intent(context, MainActivity::class.java))
                                    (context as? ComponentActivity)?.finish()
                                } else {
                                    val exception = task.exception
                                    if (exception is FirebaseAuthInvalidUserException) {
                                        val errorMsg = "Looks like you don't have an account yet! Let's get you signed up. \uD83D\uDE80"
                                        errorMessage = errorMsg
                                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                        context.startActivity(Intent(context, SignupUserActivity::class.java))
                                        (context as? ComponentActivity)?.finish()
                                    } else if (exception is FirebaseAuthInvalidCredentialsException) {
                                        val errorMsg = "Oops! Password doesn't match or email is incorrect. Please try again! \uD83E\uDD13"
                                        errorMessage = errorMsg
                                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                    } else {
                                        val errorMsg = "Uh oh, that didn't work. Maybe double check your credentials? \uD83E\uDD37\u200D\u2642\uFE0F"
                                        errorMessage = errorMsg
                                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                    }
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
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Don't have an account?")
            TextButton(onClick = { 
                context.startActivity(Intent(context, SignupUserActivity::class.java))
                (context as? ComponentActivity)?.finish()
             }) {
                Text("Sign up")
            }
        }
    }
}
