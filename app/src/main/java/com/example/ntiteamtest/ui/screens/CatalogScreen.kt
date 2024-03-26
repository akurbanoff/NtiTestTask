package com.example.ntiteamtest.ui.screens

import android.view.WindowInsets.Side
import android.widget.HorizontalScrollView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CurrencyRuble
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material.icons.filled.FiberNew
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.ntiteamtest.ui.screens.general.TopBar
import com.example.ntiteamtest.R
import com.example.ntiteamtest.data.models.Categories
import com.example.ntiteamtest.data.models.Products
import com.example.ntiteamtest.data.models.Tags
import com.example.ntiteamtest.ui.navigation.NavRoutes
import com.example.ntiteamtest.ui.screens.utilScreens.ScreenError
import com.example.ntiteamtest.ui.screens.utilScreens.ScreenLoading
import com.example.ntiteamtest.ui.states.UiState
import com.example.ntiteamtest.ui.view_models.ApiViewModel

@Composable
fun CatalogScreen(
    apiViewModel: ApiViewModel,
    navigator: NavHostController
) {
    val categoryId = remember { mutableStateOf(676153) }

    LaunchedEffect(key1 = true) {
        apiViewModel.updateProducts(categoryId.value)
        apiViewModel.updateCategories()
        apiViewModel.updateTags()
    }

    val productsState = apiViewModel.productsState.collectAsState()
    val categories = apiViewModel.categoriesState.collectAsState()
    val tags = apiViewModel.tags.collectAsState()
    val filteredProducts = apiViewModel.filteredProducts.collectAsState()

    val filterTagList = remember {
        mutableListOf<Int>()
    }

    val cart = apiViewModel.cartProducts.collectAsState()

    val tabIndex = remember {
        mutableIntStateOf(0)
    }
    val tabColor = remember {
        mutableStateOf(Color.Transparent)
    }
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
    val onFilter: (List<Int>) -> Unit = {  list ->
        apiViewModel.filterProducts(list)
    }

    Scaffold(
        topBar = { TopBar(
            tags = tags,
            filterTagList = filterTagList,
            onFilter = onFilter,
            navigator = navigator,
            cartProductsAmount = cart.value.size
        ) },
        bottomBar = {
            if(cart.value.isNotEmpty()){
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = remember{{ navigator.navigate(NavRoutes.CartScreen.route) }},
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Text(
                            text = apiViewModel.getTotalPrice().toString(),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Icon(imageVector = Icons.Default.CurrencyRuble, contentDescription = null)
                    }
                }
            }
        },
        modifier = Modifier.padding(16.dp)
    ) { padding ->
        when(val state = productsState.value){
            is UiState.Error -> ScreenError(error = state.throwable, onReload =  remember{{
                apiViewModel.updateProducts(categoryId.value)
            }})
            is UiState.Loading -> ScreenLoading()
            is UiState.Success<*> -> {
                when(val categoriesState = categories.value){
                    is UiState.Error -> ScreenError(error = categoriesState.throwable, onReload =  remember{{
                        apiViewModel.updateCategories()
                    }})
                    is UiState.Loading -> ScreenLoading()
                    is UiState.Success<*> -> {
                        var list = state.result as List<*>
                        if(filteredProducts.value.isNotEmpty() && filterTagList.isNotEmpty()) list = filteredProducts.value
                        Content(
                            modifier = Modifier.padding(padding),
                            products = list,
                            categories = categoriesState.result as List<*>,
                            categoryId = categoryId,
                            onCategoryChange = remember{{ apiViewModel.updateProducts(categoryId.value) }},
                            tabIndex = tabIndex,
                            tabColor = tabColor,
                            navigator = navigator,
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
}

@Composable
fun Content(
    modifier: Modifier = Modifier,
    navigator: NavHostController,
    products: List<*>,
    categories: List<*>? = null,
    categoryId: MutableState<Int>? = null,
    onCategoryChange: () -> Unit,
    tabIndex: MutableState<Int>? = null,
    tabColor: MutableState<Color>? = null,
    onCartAdd: (Products) -> Unit,
    onDecrease: (Products) -> Unit,
    onIncrease: (Products) -> Unit,
    amount: (Products) -> Int,
    isCartContains: (Products) -> Boolean
) {
    Column(
        modifier = modifier
    ) {
        if(tabColor != null && tabIndex != null && categoryId != null && categories != null) {
            ScrollableTabRow(
                selectedTabIndex = tabIndex.value,
                divider = {},
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                indicator = {
                    if (tabIndex.value < it.size) {
                        tabColor.value = MaterialTheme.colorScheme.primary
                    }
                },
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            ) {
                categories.forEachIndexed { index, category ->
                    //CategoryItem(title = category)
                    if (category is Categories) {
                        Tab(
                            selected = tabIndex.value == index,
                            onClick = {
                                tabIndex.value = index
                                categoryId.value = category.id
                                onCategoryChange()
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(if (tabIndex.value == index) tabColor.value else Color.Transparent)
                            ) {
                                Text(
                                    text = category.name,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .align(Alignment.Center),
                                    color = if (tabIndex.value == index) Color.White else Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
        if(products.isEmpty()){
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Таких блюд нет :(")
                Text(text = "Попробуйте изменить фильтры")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2)
            ) {
                items(
                    items = products,
                    key = {
                        it as Products
                        it.id
                    },
                    contentType = {
                        it as Products
                        Products
                    }
                ) { product ->
                    if (product is Products) {
                        ProductItem(
                            product = product,
                            navigator = navigator,
                            onCartAdd = onCartAdd,
                            onIncrease = onIncrease,
                            onDecrease = onDecrease,
                            isCartContains = isCartContains,
                            amount = amount,
                        )
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProductItem(
    modifier: Modifier = Modifier,
    product: Products,
    navigator: NavHostController,
    onCartAdd: (Products) -> Unit,
    onDecrease: (Products) -> Unit,
    onIncrease: (Products) -> Unit,
    amount: (Products) -> Int,
    isCartContains: (Products) -> Boolean,
) {
    val isProductAdd = remember { mutableStateOf(isCartContains(product))}
    val productAmount = remember { mutableStateOf(amount(product)) }

    SideEffect {
        productAmount.value = amount(product)
        isProductAdd.value = isCartContains(product)
    }

    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .padding(4.dp)
            .clickable { navigator.navigate(NavRoutes.ProductDetailScreen.withArgs(product.id)) },
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray
        ),

    ) {
        Box {
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                product.tagIds.forEach { tag ->
                    TagItem(tag = tag, modifier = Modifier.padding(end = 4.dp))
                }
            }
            Image(
                painter = painterResource(id = R.drawable.ic_product),
                contentDescription = product.image,
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth()
                    .align(Alignment.Center),
                contentScale = ContentScale.FillBounds
            )
        }
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )
            Text(
                text = "${product.measure} ${product.measureUnit}",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
        }
        if(isProductAdd.value){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                          onDecrease(product)
                        productAmount.value -= 1
                        if(productAmount.value < 1) {
                            productAmount.value = 1
                            isProductAdd.value = false
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
                    text = productAmount.value.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                Button(
                    onClick = {
                              onIncrease(product)
                        productAmount.value = amount(product)
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
        } else {
            ElevatedButton(
                onClick = {
                    onCartAdd(product)
                    isProductAdd.value = true
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = Color.White
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = product.priceCurrent.toString().take(3),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    Icon(
                        imageVector = Icons.Default.CurrencyRuble,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Black
                    )
                    if (product.priceOld != null) {
                        Text(
                            text = product.priceOld.toString().take(3),
                            textDecoration = TextDecoration.LineThrough,
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Gray
                        )
                        Icon(
                            imageVector = Icons.Default.CurrencyRuble,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TagItem(
    modifier: Modifier = Modifier,
    tag: Int
) {
    when(tag){
        1 -> {
            Box(
                modifier = modifier
                    .background(
                        color = Color.Red,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.FiberNew, contentDescription = null)
            }
        }
        2 -> {
            Box(
                modifier = modifier
                    .background(
                        color = Color.Green,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.EnergySavingsLeaf, contentDescription = null)
            }
        }
        3 -> {
            Box(
                modifier = modifier
                    .background(
                        color = Color.Black,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Whatshot, contentDescription = null, tint = Color.Red)
            }
        }
        4 -> {
            Box(
                modifier = modifier
                    .background(
                        color = Color.Red,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.LocalFireDepartment, contentDescription = null)
            }
        }
        5 -> {
            Box(
                modifier = modifier
                    .background(
                        color = Color.Blue,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Bolt, contentDescription = null)
            }
        }
    }
}