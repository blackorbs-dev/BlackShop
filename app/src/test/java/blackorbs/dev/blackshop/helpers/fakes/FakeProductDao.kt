package blackorbs.dev.blackshop.helpers.fakes

import blackorbs.dev.blackshop.data.entity.Product
import blackorbs.dev.blackshop.data.local.ProductDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeProductDao : ProductDao {

    private val productList = mutableListOf<Product>()
    private val productFlow = MutableStateFlow<List<Product>>(emptyList())

    override suspend fun addAll(products: List<Product>) {
        // Replace products with same ID, else add new
        val map = productList.associateBy { it.id }.toMutableMap()
        for (product in products) {
            map[product.id] = product
        }
        productList.clear()
        productList.addAll(map.values)
        emitAll()
    }

    override suspend fun updateCartItem(id: Int, isInCart: Boolean) {
        productList.replaceAll { if (it.id == id) it.copy(isInCart = isInCart) else it }
        emitAll()
    }

    override suspend fun checkoutItems(ids: List<Int>) {
        productList.replaceAll {
            if (ids.contains(it.id)) it.copy(isInCart = false) else it
        }
        emitAll()
    }

    override fun getItem(id: Int): Flow<Product?> = productFlow.map {
        it.find { p -> p.id == id }
    }

    override fun getAll(): Flow<List<Product>> = productFlow

    override suspend fun hasData(): Boolean = productList.isNotEmpty()

    override suspend fun getCartItemIds(): List<Int> {
        return productList.filter { it.isInCart }.map { it.id }
    }

    override fun getCartItems(): Flow<List<Product>> = productFlow.map {
        it.filter { p -> p.isInCart }
    }

    private fun emitAll() {
        productFlow.value = productList.toList()
    }
}
