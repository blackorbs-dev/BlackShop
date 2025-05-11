package blackorbs.dev.blackshop.data.repository

import blackorbs.dev.blackshop.data.entity.Product
import blackorbs.dev.blackshop.ui.UiState
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    val productList: Flow<List<Product>>
    suspend fun updateCart(productId: Int, isInCart: Boolean): UiState
    val refreshFlow: Flow<UiState>
    fun getProduct(productId: Int): Flow<Product?>
    val cartItems: Flow<List<Product>>
    fun checkout(ids: List<Int>): Flow<UiState>
}