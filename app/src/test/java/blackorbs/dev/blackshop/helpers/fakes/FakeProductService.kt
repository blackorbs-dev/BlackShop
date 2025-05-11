package blackorbs.dev.blackshop.helpers.fakes

import blackorbs.dev.blackshop.data.entity.Product
import blackorbs.dev.blackshop.data.remote.ProductService
import blackorbs.dev.blackshop.helpers.util.TestUtil.testProduct
import java.io.IOException

class FakeProductService(
    private var shouldThrowError: Boolean = false
) : ProductService {

    private val products = (1..6).map {
        testProduct()
    }

    override suspend fun getAllProducts(): List<Product> {
        if (shouldThrowError) throw IOException("Network error while fetching products")
        return products
    }

    override suspend fun addToCart(id: Int) {
        if (shouldThrowError) throw IOException("Failed to add to cart")
    }

    override suspend fun removeFromCart(id: Int) {
        if (shouldThrowError) throw IOException("Failed to remove from cart")
    }

    override suspend fun checkout(ids: List<Int>) {
        if (shouldThrowError) throw IOException("Checkout failed")
    }

    fun setShouldThrowError(value: Boolean) {
        shouldThrowError = value
    }

}
