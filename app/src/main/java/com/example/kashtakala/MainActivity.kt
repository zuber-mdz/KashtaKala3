package com.example.kashtakala

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay

data class CartItem(
    val furnitureName: String = "",
    val quantity: Int = 1,
    val price: Int = 0
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            var showSplash by remember {
                mutableStateOf(true)
            }

            var selectedFurniture by remember {
                mutableStateOf("")
            }

            var selectedImage by remember {
                mutableStateOf(R.drawable.sofa)
            }

            var selectedQuantity by remember {
                mutableStateOf(1)
            }

            var currentScreen by remember {
                mutableStateOf("home")
            }

            LaunchedEffect(Unit) {
                delay(2000)
                showSplash = false
            }

            if (showSplash) {

                SplashScreen()

            } else {

                when (currentScreen) {

                    "home" -> {

                        HomeScreen(
                            onViewClick = { name, image ->

                                selectedFurniture = name
                                selectedImage = image

                                currentScreen = "detail"
                            }
                        )
                    }

                    "detail" -> {

                        DetailScreen(
                            furnitureName = selectedFurniture,
                            furnitureImage = selectedImage,

                            onBackClick = {
                                currentScreen = "home"
                            },

                            onCartClick = { qty ->

                                selectedQuantity = qty
                                currentScreen = "cart"
                            }
                        )
                    }

                    "cart" -> {

                        CartScreen(
                            furnitureName = selectedFurniture,
                            furnitureImage = selectedImage,
                            quantity = selectedQuantity,

                            onBackClick = {
                                currentScreen = "home"
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF6D4C41)),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "🪵",
            fontSize = 70.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Kashta-Kala",
            color = Color.White,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Premium Wooden Furniture",
            color = Color.White,
            fontSize = 18.sp
        )
    }
}

@Composable
fun HomeScreen(
    onViewClick: (String, Int) -> Unit
) {

    var searchText by remember {
        mutableStateOf("")
    }

    var selectedCategory by remember {
        mutableStateOf("All")
    }

    val furnitureList = listOf(

        Triple("Modern Sofa", R.drawable.sofa, "Living Room"),
        Triple("Wooden Bed", R.drawable.bed, "Bedroom"),
        Triple("Dining Table", R.drawable.table, "Dining"),
        Triple("Office Chair", R.drawable.chair, "Office")
    )

    val filteredList = furnitureList.filter {

        val searchMatch =
            it.first.contains(searchText, ignoreCase = true)

        val categoryMatch =
            selectedCategory == "All" || it.third == selectedCategory

        searchMatch && categoryMatch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5EFE6))
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF6D4C41))
                .padding(20.dp)
        ) {

            Column {

                Text(
                    text = "Kashta-Kala",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Digital Furniture Store",
                    color = Color.White
                )
            }
        }

        LazyColumn(
            modifier = Modifier.padding(16.dp)
        ) {

            item {

                OutlinedTextField(
                    value = searchText,

                    onValueChange = {
                        searchText = it
                    },

                    label = {
                        Text("Search Furniture")
                    },

                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    CategoryChip("All", selectedCategory) {
                        selectedCategory = "All"
                    }

                    CategoryChip("Living Room", selectedCategory) {
                        selectedCategory = "Living Room"
                    }

                    CategoryChip("Bedroom", selectedCategory) {
                        selectedCategory = "Bedroom"
                    }

                    CategoryChip("Office", selectedCategory) {
                        selectedCategory = "Office"
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                filteredList.forEach {

                    FurnitureCard(
                        title = it.first,
                        imageRes = it.second,
                        category = it.third,
                        onViewClick = onViewClick
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    title: String,
    selected: String,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier.clickable {
            onClick()
        },

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor =
                if (selected == title)
                    Color(0xFF6D4C41)
                else
                    Color(0xFFD7CCC8)
        )
    ) {

        Text(
            text = title,

            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),

            color =
                if (selected == title)
                    Color.White
                else
                    Color.Black
        )
    }
}

@Composable
fun FurnitureCard(
    title: String,
    imageRes: Int,
    category: String,
    onViewClick: (String, Int) -> Unit
) {

    var isFavorite by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 18.dp),

        shape = RoundedCornerShape(20.dp)
    ) {

        Column {

            Box {

                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = title,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )

                Icon(
                    imageVector =
                        if (isFavorite)
                            Icons.Filled.Favorite
                        else
                            Icons.Outlined.FavoriteBorder,

                    contentDescription = "Favorite",

                    tint = Color.Red,

                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clickable {
                            isFavorite = !isFavorite
                        }
                )
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = category,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        onViewClick(title, imageRes)
                    },

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6D4C41)
                    )
                ) {

                    Text("View Design")
                }
            }
        }
    }
}

@Composable
fun DetailScreen(
    furnitureName: String,
    furnitureImage: Int,
    onBackClick: () -> Unit,
    onCartClick: (Int) -> Unit
) {

    val context = LocalContext.current

    val database = FirebaseDatabase.getInstance()
    val cartRef = database.getReference("Cart")

    var qty by remember {
        mutableStateOf(1)
    }

    var woodPieces by remember {
        mutableStateOf("")
    }

    var woodCost by remember {
        mutableStateOf(0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5EFE6))
            .padding(20.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = furnitureImage),
            contentDescription = furnitureName,

            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = furnitureName,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(onClick = {
                if (qty > 1) qty--
            }) {
                Text("-")
            }

            Spacer(modifier = Modifier.width(20.dp))

            Text(
                text = qty.toString(),
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.width(20.dp))

            Button(onClick = {
                qty++
            }) {
                Text("+")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Price: ₹${qty * 25000}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = woodPieces,

            onValueChange = {
                woodPieces = it
            },

            label = {
                Text("Enter Wood Pieces")
            },

            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {

                val pieces =
                    woodPieces.toIntOrNull() ?: 0

                woodCost = pieces * 500
            }
        ) {

            Text("Calculate Wood Cost")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Wood Cost: ₹$woodCost",
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                val cartItem = CartItem(
                    furnitureName = furnitureName,
                    quantity = qty,
                    price = qty * 25000
                )

                cartRef.push().setValue(cartItem)

                Toast.makeText(
                    context,
                    "Added To Cart",
                    Toast.LENGTH_SHORT
                ).show()

                onCartClick(qty)
            },

            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6D4C41)
            )
        ) {

            Text("Add To Cart")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                onBackClick()
            }
        ) {

            Text("Back")
        }
    }
}

@Composable
fun CartScreen(
    furnitureName: String,
    furnitureImage: Int,
    quantity: Int,
    onBackClick: () -> Unit
) {

    val total = quantity * 25000
    val gst = total * 18 / 100
    val finalAmount = total + gst

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5EFE6))
            .padding(20.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "🛒 My Cart",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = painterResource(id = furnitureImage),
            contentDescription = furnitureName,

            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = furnitureName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text("Quantity: $quantity")
        Text("Product Total: ₹$total")
        Text("GST (18%): ₹$gst")

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Final Amount: ₹$finalAmount",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6D4C41)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                onBackClick()
            }
        ) {

            Text("Continue Shopping")
        }
    }
}