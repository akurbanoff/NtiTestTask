package com.example.ntiteamtest.ui.view_models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ntiteamtest.data.models.Products
import com.example.ntiteamtest.data.models.Tags
import com.example.ntiteamtest.data.remote.Api
import com.example.ntiteamtest.data.repository.ApiRepository
import com.example.ntiteamtest.data.repository.CartRepository
import com.example.ntiteamtest.data.repository.Repository
import com.example.ntiteamtest.ui.states.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

@HiltViewModel
class ApiViewModel @Inject constructor(
    private val repository: Repository,
    private val cartRepository: CartRepository
): ViewModel() {

//    init {
//        updateCart()
//    }

    private val _productsState = MutableStateFlow<UiState>(UiState.Loading())
    val productsState = _productsState.asStateFlow()

    private val _categoriesState = MutableStateFlow<UiState>(UiState.Loading())
    val categoriesState = _categoriesState.asStateFlow()

    private val _singleProductState = MutableStateFlow<UiState>(UiState.Loading())
    val singleProductState = _singleProductState.asStateFlow()

    val cartProducts = cartRepository.cartProducts

    private val _tags = MutableStateFlow(emptyList<Tags>())
    val tags = _tags.asStateFlow()

    private val _filteredProducts = MutableStateFlow(emptyList<Products>())
    val filteredProducts = _filteredProducts.asStateFlow()

    private val _searchedProducts = MutableStateFlow<UiState>(UiState.Loading())
    val searchedProducts = _searchedProducts.asStateFlow()


    fun updateProducts(categoryId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            _productsState.value = UiState.Loading()
            repository.getProducts(categoryId).collect{ result ->
                _productsState.value = result
            }
        }
    }

    fun updateCategories(){
        viewModelScope.launch(Dispatchers.IO) {
            _categoriesState.value = UiState.Loading()
            repository.getCategories().collect{ result ->
                _categoriesState.value = result
            }
        }
    }

    fun getProduct(id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            _singleProductState.value = UiState.Loading()
            repository.getProduct(id).collect{ result ->
                _singleProductState.value = result
            }
        }
    }

    fun updateTags(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getTags().collect{ result ->
                _tags.value = result
            }
        }
    }

    fun filterProducts(tags: List<Int>){
        viewModelScope.launch(Dispatchers.IO) {
            if(tags.isEmpty()) {
                _filteredProducts.value = emptyList()
            } else {
                repository.filterProducts(tags).collect { result ->
                    _filteredProducts.value = result
                }
            }
        }
    }

    fun searchProducts(query: String){
        viewModelScope.launch(Dispatchers.IO) {
            _searchedProducts.value = UiState.Loading()
            repository.searchProducts(query).collect{ result ->
                _searchedProducts.value = result
            }
        }
    }

    fun increaseProduct(product: Products){
        cartRepository.increaseProductQuantity(product)
    }

    fun decreaseProduct(product: Products){
        cartRepository.decreaseProductQuantity(product)
    }

    fun getTotalPrice(): Int{
        return cartRepository.totalPrice()
    }

    fun getTotalProductsAmount(): Int{
        return cartRepository.getTotalProductsAmount()
    }

    fun addProduct(product: Products){
        cartRepository.addProductToCart(product)
    }

    fun isContains(product: Products): Boolean{
        return cartRepository.isContains(product)
    }

    fun getProductAmount(product: Products): Int {
        return cartRepository.getProductAmount(product)
    }
}