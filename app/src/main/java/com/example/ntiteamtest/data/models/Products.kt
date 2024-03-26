package com.example.ntiteamtest.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Products(
    @SerialName("id") val id: Int,
    @SerialName("category_id") val categoryId: Int,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("image") val image: String,
    @SerialName("price_current") val priceCurrent: Int,
    @SerialName("price_old") val priceOld: Int?,
    @SerialName("measure") val measure: Int,
    @SerialName("measure_unit") val measureUnit: String,
    @SerialName("energy_per_100_grams") val energyPer100Grams: Double,
    @SerialName("proteins_per_100_grams") val proteinsPer100Grams: Double,
    @SerialName("fats_per_100_grams") val fatsPer100Grams: Double,
    @SerialName("carbohydrates_per_100_grams") val carbohydratesPer100Grams: Double,
    @SerialName("tag_ids") val tagIds: List<Int>
)
