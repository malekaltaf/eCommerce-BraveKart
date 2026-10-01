package com.malekaltaf.ecommerce_bravekart

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.malekaltaf.ecommerce_bravekart.ui.theme.ECommerceBraveKartTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class Product(val id: String = "", val name: String = "", val description: String = "", val price: Double = 0.0)

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
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Catalog", "Orders", "Profile")
    val icons = listOf(Icons.Default.ShoppingCart, Icons.AutoMirrored.Filled.List, Icons.Default.Person)

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedItem) {
            0 -> CatalogScreen(modifier = Modifier.padding(innerPadding))
            1 -> OrdersScreen(modifier = Modifier.padding(innerPadding))
            2 -> ProfileScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun CatalogScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var isOrdering by remember { mutableStateOf<String?>(null) } // Store product ID being ordered
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("ecommerce_items").addSnapshotListener { snapshot, error ->
            isLoading = false
            if (error != null) {
                Toast.makeText(context, "Error fetching products.", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val fetchedProducts = snapshot.documents.mapNotNull { doc ->
                    val name = doc.getString("product_name") ?: ""
                    val desc = doc.getString("product_description") ?: ""
                    val price = doc.getDouble("product_price") ?: doc.getLong("product_price")?.toDouble() ?: 0.0
                    Product(doc.id, name, desc, price)
                }
                products = fetchedProducts
            }
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Text(
            text = "Shop the Catalog",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No products available.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(products) { product ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(product.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(product.description, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 4.dp))
                            Text("$${product.price}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(12.dp))
                            
                            Button(
                                onClick = {
                                    isOrdering = product.id
                                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                                    if (userId != null) {
                                        val db = FirebaseFirestore.getInstance()
                                        val order = hashMapOf(
                                            "userId" to userId,
                                            "productId" to product.id,
                                            "productName" to product.name,
                                            "price" to product.price,
                                            "status" to "pending",
                                            "timestamp" to System.currentTimeMillis()
                                        )
                                        db.collection("orders").add(order)
                                            .addOnSuccessListener {
                                                isOrdering = null
                                                Toast.makeText(context, "Order placed successfully! \uD83C\uDF89", Toast.LENGTH_SHORT).show()
                                            }
                                            .addOnFailureListener {
                                                isOrdering = null
                                                Toast.makeText(context, "Failed to place order. \uD83D\uDE22", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = isOrdering != product.id
                            ) {
                                if (isOrdering == product.id) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                                } else {
                                    Text("Buy Now")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrdersScreen(modifier: Modifier = Modifier) {
    var orders by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            FirebaseFirestore.getInstance().collection("orders")
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, _ ->
                    isLoading = false
                    if (snapshot != null) {
                        val fetchedOrders = snapshot.documents.mapNotNull { it.data }
                        // Sort locally by timestamp descending
                        orders = fetchedOrders.sortedByDescending { it["timestamp"] as? Long ?: 0L }
                    }
                }
        } else {
            isLoading = false
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "My Orders",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("You haven't placed any orders yet.")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(orders) { order ->
                    val productName = order["productName"] as? String ?: "Unknown Product"
                    val price = order["price"] as? Double ?: 0.0
                    val status = order["status"] as? String ?: "unknown"
                    
                    val statusColor = when (status.lowercase()) {
                        "approve", "approved" -> Color(0xFF4CAF50) // Green
                        "deliver", "delivered" -> Color(0xFF2196F3) // Blue
                        "pending" -> Color(0xFFFF9800) // Orange
                        else -> Color.Gray
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(productName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("$$price", style = MaterialTheme.typography.bodyMedium)
                            }
                            Surface(
                                color = statusColor.copy(alpha = 0.1f),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = status.uppercase(),
                                    color = statusColor,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
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
