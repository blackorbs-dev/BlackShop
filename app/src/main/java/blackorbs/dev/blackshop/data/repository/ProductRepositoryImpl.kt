package blackorbs.dev.blackshop.data.repository

import blackorbs.dev.blackshop.data.entity.Response
import blackorbs.dev.blackshop.data.local.ProductDao
import blackorbs.dev.blackshop.data.remote.ProductService
import blackorbs.dev.blackshop.ui.UiState
import kotlinx.coroutines.flow.flow

class ProductRepositoryImpl(
    private val productDao: ProductDao,
    private val productService: ProductService
) : ProductRepository {

    override val productList get() = productDao.getAll()

    override fun getProduct(productId: Int) =
        productDao.getItem(productId)

    override val cartItems get() = productDao.getCartItems()

    override suspend fun updateCart(
        productId: Int,
        isInCart: Boolean
    ): UiState{
        val response = RequestHandler.execute {
            if(isInCart) productService.addToCart(productId)
            else productService.removeFromCart(productId)
        }
        return if(response is Response.Success){
            productDao.updateCartItem(productId, isInCart)
            UiState.Success
        } else UiState.Error(null)
    }

    override fun checkout(ids: List<Int>) = flow {
        emit(UiState.Loading)
        val response = RequestHandler.execute {
            productService.checkout(ids)
        }
        emit(
            if(response is Response.Success){
                productDao.checkoutItems(ids)
                UiState.Success
            } else UiState.Error(null)
        )
    }

    override val refreshFlow get() = flow {
        emit(UiState.Loading)
        val hasData = productDao.hasData()
        if(hasData){
            emit(UiState.Success)
        }
        val cartItemsIds = productDao.getCartItemIds()
        val response = RequestHandler.execute {
            productService.getAllProducts().apply {
                productDao.addAll(
                    map {
                        it.copy(
                            isInCart = cartItemsIds.contains(it.id)
                        )
                    }
                )
            }
        }
        if(hasData.not()) {
            emit(
                if (response is Response.Success) UiState.Success
                else UiState.Error(null)
            )
        }
    }

}