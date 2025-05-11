package blackorbs.dev.blackshop.data.remote

import blackorbs.dev.blackshop.data.entity.Product

interface ProductService {
    suspend fun getAllProducts(): List<Product>
    suspend fun addToCart(id: Int)
    suspend fun removeFromCart(id: Int)
    suspend fun checkout(ids: List<Int>)
}