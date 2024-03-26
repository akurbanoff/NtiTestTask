package com.example.ntiteamtest.data.remote

import com.example.ntiteamtest.data.models.Categories
import com.example.ntiteamtest.data.models.Products
import com.example.ntiteamtest.data.models.Tags
import retrofit2.Call
import retrofit2.http.GET


public interface Api {
    @GET("raw/categories.json")
    fun getCategories(): Call<List<Categories>>

    @GET("products.json")
    fun getProducts(): Call<List<Products>>

    @GET("raw/tags.json")
    fun getTags(): Call<List<Tags>>

    @GET("get_product/{id}")
    fun getProduct(id: Int): Call<Products>

    @GET("search/{query}")
    fun searchProducts(query: String): Call<List<Products>>
}