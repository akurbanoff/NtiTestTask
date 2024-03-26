package com.example.ntiteamtest.data.repository

import com.example.ntiteamtest.data.models.Products
import com.example.ntiteamtest.data.models.Tags
import com.example.ntiteamtest.ui.states.UiState
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun getProducts(categoryId: Int): Flow<UiState>
    suspend fun getCategories(): Flow<UiState>
    suspend fun getProduct(id: Int): Flow<UiState>
    suspend fun getTags(): Flow<List<Tags>>
    suspend fun filterProducts(tags: List<Int>): Flow<List<Products>>
    suspend fun searchProducts(query: String): Flow<UiState>
}