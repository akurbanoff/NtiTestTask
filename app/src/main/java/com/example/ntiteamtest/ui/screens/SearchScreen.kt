package com.example.ntiteamtest.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.ntiteamtest.data.models.Products
import com.example.ntiteamtest.ui.screens.utilScreens.ScreenError
import com.example.ntiteamtest.ui.screens.utilScreens.ScreenLoading
import com.example.ntiteamtest.ui.states.UiState
import com.example.ntiteamtest.ui.view_models.ApiViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navigator: NavHostController,
    apiViewModel: ApiViewModel
) {

    val searchedProducts = apiViewModel.searchedProducts.collectAsState()

    var searchQuery = remember {
        mutableStateOf("")
    }

    val searchJob = remember { mutableStateOf<Job?>(null) }

    val onCartAdd: (Products) -> Unit = { product ->
        apiViewModel.addProduct(product)
    }
    val onDecrease: (Products) -> Unit = { product ->
        apiViewModel.decreaseProduct(product)
    }
    val onIncrease: (Products) -> Unit = { product ->
        apiViewModel.increaseProduct(product)
    }
    val amount: (Products) -> Int = { product ->
        apiViewModel.getProductAmount(product)
    }
    val isCartContains: (Products) -> Boolean = { product ->
        apiViewModel.isContains(product)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = { 
                    TextField(
                        value = searchQuery.value,
                        onValueChange = {
                            searchQuery.value = it
                            searchJob.value?.cancel()
                            searchJob.value = CoroutineScope(Dispatchers.Main).launch {
                                delay(1000) // задержка в миллисекундах для ожидания окончания ввода
                                apiViewModel.searchProducts(searchQuery.value)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Найти блюдо",
                                color = Color.Gray
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        )
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(44.dp)
                            .clickable {
                                navigator.popBackStack()
                            }
                    )
                },
                actions = {
                    if(searchQuery.value.length > 1){
                        Icon(
                            imageVector = Icons.Filled.Cancel,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier
                                .clickable { searchQuery.value = "" }
                                .size(32.dp)
                        )
                    }
                }
            )
        }
    ) { padding ->
        when(val state = searchedProducts.value){
            is UiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = state.throwable.message ?: "Что-то пошло не так :(",
                        textAlign = TextAlign.Center,
                    )
                }
            }
            is UiState.Loading -> {
                if(searchQuery.value.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Введите название блюда, которое ищете",
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    ScreenLoading()
                }
            }
            is UiState.Success<*> -> {
                val list = state.result as List<*>
                if(list.isNotEmpty()) {
                    Content(
                        modifier = Modifier.padding(padding),
                        navigator = navigator,
                        products = list,
                        categories = null,
                        categoryId = null,
                        onCategoryChange = { /*TODO*/ },
                        tabIndex = null,
                        tabColor = null,
                        onCartAdd = onCartAdd,
                        onDecrease = onDecrease,
                        onIncrease = onIncrease,
                        amount = amount,
                        isCartContains = isCartContains
                    )
                }
            }
        }
    }
}