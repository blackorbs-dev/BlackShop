package blackorbs.dev.blackshop.data.remote

import blackorbs.dev.blackshop.data.entity.Product
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post

class ProductServiceImpl(
    private val client: HttpClient
) : ProductService {

    override suspend fun getAllProducts(): List<Product> =
        client.get(Api.PRODUCTS).body()

    override suspend fun addToCart(id: Int): Unit =
        client.post(Api.ADD_TO_CART).body()

    override suspend fun removeFromCart(id: Int): Unit =
        client.post(Api.REMOVE_FROM_CART).body()

    override suspend fun checkout(ids: List<Int>): Unit =
        client.post(Api.CHECKOUT).body()
}