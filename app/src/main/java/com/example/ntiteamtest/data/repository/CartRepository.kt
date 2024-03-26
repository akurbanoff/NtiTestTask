package com.example.ntiteamtest.data.repository

import com.example.ntiteamtest.data.models.Products
import com.example.ntiteamtest.ui.states.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf

class CartRepository {
    //private val products = mutableListOf<Pair<Products, Int>>()

    private val _cartProducts = MutableStateFlow<List<Pair<Products, Int>>>(emptyList())
    val cartProducts: StateFlow<List<Pair<Products, Int>>> = _cartProducts

    fun addProductToCart(product: Products) {
        val updatedList = _cartProducts.value.toMutableList()
        if(isContains(product)) increaseProductQuantity(product) else updatedList.add(product to 1)
        _cartProducts.value = updatedList
    }

    fun increaseProductQuantity(product: Products) {
        val updatedList = _cartProducts.value.toMutableList()
        val index = updatedList.indexOfFirst { it.first == product }
        if (index != -1) {
            val currentCount = updatedList[index].second
            updatedList[index] = product to (currentCount + 1)
            _cartProducts.value = updatedList
        }
    }

    fun decreaseProductQuantity(product: Products){
        val updatedList = _cartProducts.value.toMutableList()
        val index = updatedList.indexOfFirst { it.first == product }
        if (index != -1) {
            val currentCount = updatedList[index].second
            if (currentCount > 1) {
                updatedList[index] = product to (currentCount - 1)
                _cartProducts.value = updatedList
            } else {
                updatedList.removeAt(index)
                _cartProducts.value = updatedList
            }
        }
    }

    fun totalPrice(): Int{
        val updatedList = _cartProducts.value.toMutableList()
        var total: Int = 0
        updatedList.forEach {
            val price = it.first.priceCurrent.toString().take(3).toInt()
            total += price * it.second
        }
        return if(total < 0){
            0
        } else {
            total
        }
    }

    fun getTotalProductsAmount(): Int{
        val updatedList = _cartProducts.value.toMutableList()
        var total = 0
        updatedList.forEach {
            total += 1
        }
        return total
    }

    fun isContains(product: Products): Boolean {
        return _cartProducts.value.any { it.first.id == product.id }
    }

    fun getProductAmount(product: Products): Int {
        return _cartProducts.value.firstOrNull { it.first.id == product.id }?.second ?: 1
    }

}