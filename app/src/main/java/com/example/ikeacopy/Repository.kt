package com.example.ikeacopy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ikeacopy.Data.CartItem
import com.example.ikeacopy.Data.Product
import com.example.ikeacopy.Data.ProductDao
import com.example.ikeacopy.Data.User
import com.example.ikeacopy.Data.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(private val userDao: UserDao, private val productDao: ProductDao) {
    private val _currentUser = MutableLiveData<User?>(null)
    val currentUser: LiveData<User?> = _currentUser

    private val _cartItems = MutableLiveData<List<CartItem>>(emptyList())
    val cartItems: LiveData<List<CartItem>> = _cartItems

    private val _products = MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>> = _products

    private val _favoriteProducts = MutableLiveData<List<Product>>(emptyList())
    val favoriteProducts: LiveData<List<Product>> = _favoriteProducts

    suspend fun signIn(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            val user = userDao.getUser(email, password)
            _currentUser.postValue(user)
            user != null
        }
    }

    suspend fun signUp(name: String, email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            if (userDao.getUserByEmail(email) != null) {
                false
            } else {
                val user = User(name = name, email = email, password = password)
                userDao.insertUser(user)
                _currentUser.postValue(user)
                true
            }
        }
    }

    suspend fun logout() {
        _currentUser.postValue(null)
    }

    suspend fun loadProducts() {
        withContext(Dispatchers.IO) {
            var products = productDao.getAllProducts()
            if (products.isEmpty()) {
                val demoProducts = listOf(
                    Product(name = "Black Simple Lamp", price = 12.0, category = "Lighting", imageUrl = "lamp_url"),
                    Product(name = "Minimal Stand", price = 25.0, category = "Furniture", imageUrl = "stand_url"),
                    Product(name = "Coffee Chair", price = 20.0, category = "Chairs", imageUrl = "chair_url"),
                    Product(name = "Simple Desk", price = 50.0, category = "Tables", imageUrl = "desk_url"),
                    Product(name = "Coffee Table", price = 50.0, category = "Tables", imageUrl = "table_url"),
                    Product(name = "Minimal Desk", price = 50.0, category = "Tables", imageUrl = "minimal_desk_url"),
                    Product(name = "Minimal Lamp", price = 12.0, category = "Lighting", imageUrl = "minimal_lamp_url")
                )
                demoProducts.forEach { productDao.insertProduct(it) }
                products = productDao.getAllProducts()
            }
            _products.postValue(products)
            _favoriteProducts.postValue(productDao.getFavoriteProducts())
        }
    }

    suspend fun toggleFavorite(productId: Int) {
        withContext(Dispatchers.IO) {
            val product = productDao.getProductById(productId)
            product?.let {
                it.isFavorite = !it.isFavorite
                productDao.updateProduct(it)
                _favoriteProducts.postValue(productDao.getFavoriteProducts())
            }
        }
    }

    fun addToCart(product: Product) {
        val currentItems = _cartItems.value?.toMutableList() ?: mutableListOf()
        val existingItem = currentItems.find { it.product.id == product.id }

        if (existingItem != null) {
            existingItem.quantity += 1
        } else {
            currentItems.add(CartItem(product, 1))
        }

        _cartItems.postValue(currentItems)
    }

    fun updateCartItemQuantity(productId: Int, newQuantity: Int) {
        val currentItems = _cartItems.value?.toMutableList() ?: mutableListOf()
        val itemIndex = currentItems.indexOfFirst { it.product.id == productId }

        if (itemIndex != -1) {
            if (newQuantity > 0) {
                currentItems[itemIndex] = currentItems[itemIndex].copy(quantity = newQuantity)
            } else {
                currentItems.removeAt(itemIndex)
            }
            _cartItems.postValue(currentItems)
        }
    }

    fun calculateTotal(): Double {
        return _cartItems.value?.sumOf { it.product.price * it.quantity } ?: 0.0
    }

    suspend fun getProductById(productId: Int): Product? {
        return withContext(Dispatchers.IO) {
            productDao.getProductById(productId)
        }
    }

    fun clearCart() {
        _cartItems.postValue(emptyList())
    }
}