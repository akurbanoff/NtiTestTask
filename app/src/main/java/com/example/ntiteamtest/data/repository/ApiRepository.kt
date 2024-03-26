package com.example.ntiteamtest.data.repository

import android.content.res.Resources
import android.nfc.Tag
import com.example.ntiteamtest.R
import com.example.ntiteamtest.data.models.Categories
import com.example.ntiteamtest.data.models.Products
import com.example.ntiteamtest.data.models.Tags
import com.example.ntiteamtest.data.remote.Api
import com.example.ntiteamtest.ui.states.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Call
import retrofit2.Response
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject

class ApiRepository @Inject constructor(
    private val api: Api,
    private val server: MockWebServer,
    private val resources: Resources
): Repository {
    override suspend fun getProducts(categoryId: Int): Flow<UiState> {
        return try {
            val inputStream = resources.openRawResource(R.raw.products)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val lines = bufferedReader.readText()

//            server.enqueue(MockResponse().setBody(lines))
//            val call: Call<List<Products>> = api.getProducts()
//            val response: Response<List<Products>> = call.execute()

            val data: List<Products> = Json.decodeFromString(lines)

            if(data.isNotEmpty()) {
                flowOf(UiState.Success(data.filter { it.categoryId == categoryId }))
            } else {
                throw Exception("Нет данных")
            }
        } catch (e: Exception){
            flowOf(UiState.Error(e))
        }
    }

    override suspend fun getProduct(id: Int): Flow<UiState>{
        return try {
            val inputStream = resources.openRawResource(R.raw.products)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val lines = bufferedReader.readText()

//            server.enqueue(MockResponse().setBody(lines))
//            val call: Call<Products> = api.getProduct(id)
//            val response: Response<Products> = call.execute()

            val data: List<Products> = Json.decodeFromString(lines)

            val product: Products? = data.firstOrNull { it.id == id }

            if(product != null){
                flowOf(UiState.Success(product))
            } else {
                flowOf(UiState.Error(Exception("Такого продукта нет")))
            }
        } catch (e: Exception){
            flowOf(UiState.Error(e))
        }
    }

    override suspend fun getCategories(): Flow<UiState>{
        return try {
            val inputStream = resources.openRawResource(R.raw.categories)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val lines = bufferedReader.readText()

//            server.enqueue(MockResponse().setBody(lines))
//            val call: Call<List<Categories>> = api.getCategories()
//            val response: Response<List<Categories>> = call.execute()

            val data: List<Categories> = Json.decodeFromString(lines)

            if(data.isNotEmpty()) {
                flowOf(UiState.Success(data))
            } else {
                throw Exception("Нет данных")
            }
        } catch (e: Exception){
            flowOf(UiState.Error(e))
        }
    }

    override suspend fun getTags(): Flow<List<Tags>>{
        val inputStream = resources.openRawResource(R.raw.tags)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val lines = bufferedReader.readText()

//        server.enqueue(MockResponse().setBody(lines))
//        val call: Call<List<Tags>> = api.getTags()
//        val response: Response<List<Tags>> = call.execute()

        val data: List<Tags> = Json.decodeFromString(lines)

        return flowOf(data)
    }

    override suspend fun filterProducts(tags: List<Int>): Flow<List<Products>>{
        val inputStream = resources.openRawResource(R.raw.products)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val lines = bufferedReader.readText()

//        server.enqueue(MockResponse().setBody(lines))
//        val call: Call<List<Products>> = api.getProducts()
//        val response: Response<List<Products>> = call.execute()

        val data: List<Products> = Json.decodeFromString(lines)

        return flow {
            emit(data.filter { product ->
                product.tagIds.any { it in tags }
            })
        }
    }

    override suspend fun searchProducts(query: String): Flow<UiState>{
        val inputStream = resources.openRawResource(R.raw.products)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val lines = bufferedReader.readText()

//        server.enqueue(MockResponse().setBody(lines))
//        val call: Call<List<Products>> = api.searchProducts(query)
//        val response: Response<List<Products>> = call.execute()

        val data: List<Products> = Json.decodeFromString(lines)

        val searchedProducts = data.filter { product ->
                product.name.contains(query, ignoreCase = true) ||
                        product.description.contains(query, ignoreCase = true)
        }

        return if(searchedProducts.isNotEmpty()){
            flowOf(UiState.Success(searchedProducts))
        } else {
            flowOf(UiState.Error(Exception("Ничего не нашлось :(")))
        }
    }
}