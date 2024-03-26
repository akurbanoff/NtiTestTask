package com.example.ntiteamtest.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CurrencyRuble
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material.icons.filled.FiberNew
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.ntiteamtest.R
import com.example.ntiteamtest.data.models.Products
import com.example.ntiteamtest.ui.view_models.ApiViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductDetailScreen(
    product: Products,
    navigator: NavHostController,
    apiViewModel: ApiViewModel
) {
    val properties = remember {
        listOf(
            "Вес" to "${product.measure} ${product.measureUnit}",
            "Энерг. ценность" to "${product.energyPer100Grams} ккал",
            "Белки" to "${product.proteinsPer100Grams} г",
            "Жиры" to "${product.fatsPer100Grams} г",
            "Углеводы" to "${product.carbohydratesPer100Grams} г"
        )
    }
    val isCartContains: () -> Boolean = {
        apiViewModel.isContains(product)
    }
    val onDecrease: () -> Unit = {
        apiViewModel.decreaseProduct(product)
    }
    val onIncrease: () -> Unit = {
        apiViewModel.increaseProduct(product)
    }
    val amount: () -> Int = {
        apiViewModel.getProductAmount(product)
    }

    var isAddClicked by remember {
        mutableStateOf(isCartContains())
    }

    var productAmount by remember { mutableStateOf(amount()) }

    SideEffect {
        isAddClicked = isCartContains()
        productAmount = amount()
    }

    Scaffold(
        modifier = Modifier.padding(16.dp),
        bottomBar = {
            if(!isAddClicked) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            isAddClicked = true
                            apiViewModel.addProduct(product)
                        },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "В корзину за ${product.priceCurrent.toString().take(3)}",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Icon(imageVector = Icons.Default.CurrencyRuble, contentDescription = null)
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            onDecrease()
                            productAmount -= 1
                            if(productAmount < 1) {
                                productAmount = 1
                                isAddClicked = false
                            }
                        },
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        contentPadding = PaddingValues(6.dp),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Text(
                        text = productAmount.toString(),
                        modifier = Modifier.padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Button(
                        onClick = {
                            onIncrease()
                            productAmount = amount()
                        },
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        contentPadding = PaddingValues(6.dp),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            stickyHeader {
                ElevatedButton(
                    onClick = { navigator.popBackStack() },
                    shape = CircleShape,
                    contentPadding = PaddingValues(8.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    elevation = ButtonDefaults.elevatedButtonElevation(),
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(35.dp),
                        tint = Color.Black
                    )
                }
            }
            item {
                Box {
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        product.tagIds.forEach { tag ->
                            TagItem(tag = tag, modifier = Modifier
                                .padding(end = 4.dp)
                                .size(44.dp))
                        }
                    }
                    Image(
                        painter = painterResource(id = R.drawable.ic_product),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)
                            .align(Alignment.Center),
                        contentScale = ContentScale.FillBounds
                    )
                }
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = product.description,
                    color = Color.Gray
                )
                HorizontalDivider()
            }
            items(properties){ property ->
                ProductPropertyUnit(property = property.first, value = property.second)
                HorizontalDivider()
            }
        }
    }
}

@Composable
@Stable
private fun ProductPropertyUnit(property: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = property)
        Text(text = value)
    }
}