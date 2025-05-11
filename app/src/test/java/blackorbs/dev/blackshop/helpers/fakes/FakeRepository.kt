package blackorbs.dev.blackshop.helpers.fakes

import blackorbs.dev.blackshop.data.entity.Product
import blackorbs.dev.blackshop.data.repository.ProductRepository
import blackorbs.dev.blackshop.ui.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException

class FakeRepository : ProductRepository {

    private val _productList = MutableStateFlow<List<Product>>(emptyList())
    override val productList: Flow<List<Product>> = _productList

    override val cartItems: Flow<List<Product>> = _productList.map {
        it.filter { p -> p.isInCart }
    }

    override fun getProduct(productId: Int): Flow<Product?> =
        _productList.map { list -> list.find { it.id == productId } }

    private var throwError = false

    // Optional: to simulate network failure in tests
    fun setShouldThrowError(value: Boolean) {
        throwError = value
    }

    // To populate initial fake products
    fun setFakeProducts(products: List<Product>) {
        _productList.value = products
    }

    override suspend fun updateCart(productId: Int, isInCart: Boolean): UiState {
        delay(800) // simulate API or DB delay
        return try {
            if (throwError) throw IOException("Simulated error")
            val updated = _productList.value.map {
                if (it.id == productId) it.copy(isInCart = isInCart) else it
            }
            _productList.value = updated
            UiState.Success
        } catch (e: Exception) {
            UiState.Error(e)
        }
    }

    override fun checkout(ids: List<Int>): Flow<UiState> = flow {
        emit(UiState.Loading)
        delay(800)
        if (throwError) {
            emit(UiState.Error(IOException("Checkout failed")))
        } else {
            _productList.value = _productList.value.map {
                if (ids.contains(it.id)) it.copy(isInCart = false) else it
            }
            emit(UiState.Success)
        }
    }

    override val refreshFlow: Flow<UiState>
        get() = flow {
            emit(UiState.Loading)
            val hasData = _productList.value.isNotEmpty()
            if(hasData){
                emit(UiState.Success)
            }
            delay(800)
            if(hasData.not()) {
                emit(
                    if (throwError)
                        UiState.Error(IOException("Checkout failed"))
                    else UiState.Success
                )
            }
        }
}
