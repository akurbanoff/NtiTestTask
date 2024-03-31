package com.example.ntiteamtest.ui.navigation

sealed class NavRoutes(val route: String) {
    object CatalogScreen: NavRoutes("catalog")
    object ProductDetailScreen: NavRoutes("product")
    object CartScreen: NavRoutes("cart")
    object SearchScreen: NavRoutes("search")

    fun withArgs(vararg args: String): String{
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }

    fun withArgs(vararg args: Int): String{
        return buildString {
            append(route)
            args.forEach {arg ->
                append("/$arg")
            }
        }
    }
}