package com.example.ntiteamtest.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ntiteamtest.SplashScreen
import com.example.ntiteamtest.data.models.Products
import com.example.ntiteamtest.ui.screens.CartScreen
import com.example.ntiteamtest.ui.screens.CatalogScreen
import com.example.ntiteamtest.ui.screens.ProductDetailScreen
import com.example.ntiteamtest.ui.screens.SearchScreen
import com.example.ntiteamtest.ui.screens.utilScreens.ScreenError
import com.example.ntiteamtest.ui.screens.utilScreens.ScreenLoading
import com.example.ntiteamtest.ui.states.UiState
import com.example.ntiteamtest.ui.view_models.ApiViewModel

@Composable
fun Navigator() {
    val navigator = rememberNavController()
    val apiViewModel = hiltViewModel<ApiViewModel>()
    val singleProductState = apiViewModel.singleProductState.collectAsState()

    var isProductDetailPush by remember { mutableStateOf(false) }
    var isSearchPush by remember { mutableStateOf(false) }
    var isCartPush by remember { mutableStateOf(false) }

    NavHost(navController = navigator, startDestination = NavRoutes.SplashScreen.route){
        composable(
            route = NavRoutes.SplashScreen.route,
            exitTransition = {
                slideOutHorizontally (
                    targetOffsetX = { it },
                    animationSpec = tween(durationMillis = 500, delayMillis = 100)
                )
            }
        ){
            SplashScreen(navigator = navigator)
        }
        composable(
            route = NavRoutes.CatalogScreen.route,
            enterTransition = {
                if(isProductDetailPush || isSearchPush || isCartPush) {
                    isProductDetailPush = false
                    isSearchPush = false
                    isCartPush = false
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(durationMillis = 500, delayMillis = 100)
                    ) + expandHorizontally()
                } else {
                    EnterTransition.None
                }
            },
            exitTransition = {
                if(isProductDetailPush || isSearchPush || isCartPush){
                    ExitTransition.None
                } else {
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(durationMillis = 500, delayMillis = 100)
                    )
                }
            }
        ){
            CatalogScreen(
                apiViewModel = apiViewModel,
                navigator = navigator
            )
        }

        composable(
            route = NavRoutes.ProductDetailScreen.route + "/{id}",
            arguments = listOf(navArgument("id"){type = NavType.IntType}),
            enterTransition = {
                isProductDetailPush = true
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(durationMillis = 500, delayMillis = 100)
                ) + expandHorizontally()
            },
            exitTransition = {
                if(isProductDetailPush){
                    ExitTransition.None
                } else {
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(durationMillis = 500, delayMillis = 100)
                    )
                }
            }
        ){ navBackStackEntry ->
            navBackStackEntry.arguments?.let {
                val id = it.getInt("id")
                apiViewModel.getProduct(id)

                when(val state = singleProductState.value){
                    is UiState.Error -> ScreenError(error = state.throwable) {
                        navigator.popBackStack()
                    }
                    is UiState.Loading -> ScreenLoading()
                    is UiState.Success<*> -> {
                        ProductDetailScreen(
                            navigator = navigator,
                            product = state.result as Products,
                            apiViewModel = apiViewModel
                        )
                    }
                }
            }
        }

        composable(
            route = NavRoutes.CartScreen.route,
            enterTransition = {
                isCartPush = true
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(durationMillis = 500, delayMillis = 100)
                ) + expandHorizontally()
            },
            exitTransition = {
                if(isCartPush){
                    ExitTransition.None
                } else {
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(durationMillis = 500, delayMillis = 100)
                    )
                }
            }
        ){
            CartScreen(navigator = navigator, apiViewModel = apiViewModel)
        }

        composable(
            route = NavRoutes.SearchScreen.route,
            enterTransition = {
                isSearchPush = true
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(durationMillis = 500, delayMillis = 100)
                ) + expandHorizontally()
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(durationMillis = 500, delayMillis = 100)
                )
            }
        ){
            SearchScreen(navigator = navigator, apiViewModel = apiViewModel)
        }
    }
}