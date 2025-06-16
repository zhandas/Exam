package com.example.ikeacopy.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ikeacopy.Data.Product
import com.example.ikeacopy.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppViewModel(private val repository: Repository) : ViewModel() {
    val currentUser = repository.currentUser
    val cartItems = repository.cartItems
    val products = repository.products
    val favoriteProducts = repository.favoriteProducts

    fun signIn(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            if (repository.signIn(email, password)) {
                loadProducts()
                onSuccess()
            } else {
                onError("Неверный email или пароль")
            }
        }
    }

    fun signUp(name: String, email: String, password: String, confirmPassword: String,
               onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            if (password != confirmPassword) {
                onError("Пароли не совпадают")
                return@launch
            }

            if (repository.signUp(name, email, password)) {
                loadProducts()
                onSuccess()
            } else {
                onError("Пользователь с таким email уже существует")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun loadProducts() {
        viewModelScope.launch {
            repository.loadProducts()
        }
    }

    fun toggleFavorite(productId: Int) {
        viewModelScope.launch {
            repository.toggleFavorite(productId)
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            repository.addToCart(product)
        }
    }

    fun updateCartItemQuantity(productId: Int, newQuantity: Int) {
        viewModelScope.launch {
            repository.updateCartItemQuantity(productId, newQuantity)
        }
    }

    fun calculateTotal(): Double {
        return repository.calculateTotal()
    }

    fun getProductById(productId: Int, onResult: (Product?) -> Unit) {
        viewModelScope.launch {
            val product = repository.getProductById(productId)
            onResult(product)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }
}