package com.example.ikeacopy.Data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val password: String
)

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Double,
    val category: String,
    @SerializedName("image_url") val imageUrl: String? = null,
    var isFavorite: Boolean = false
)

data class CartItem(
    val product: Product,
    var quantity: Int
)