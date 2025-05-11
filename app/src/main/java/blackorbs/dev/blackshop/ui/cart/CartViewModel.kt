package blackorbs.dev.blackshop.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blackorbs.dev.blackshop.data.entity.ProductState
import blackorbs.dev.blackshop.data.repository.ProductRepository
import blackorbs.dev.blackshop.ui.UiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(
    private val repository: ProductRepository
): ViewModel() {

    private val loadingItems = MutableStateFlow<Set<Int>>(emptySet())
    private val _loadState = MutableStateFlow<UiState>(UiState.Idle)
    val loadState = _loadState.asStateFlow()
    private val _cartErrorState = MutableSharedFlow<Unit>()
    val cartErrorState = _cartErrorState.asSharedFlow()

    val productList = combine(
        repository.cartItems,
        loadingItems
    ) { products, loadingIds ->
        products.map {
            ProductState(it, loadingIds.contains(it.id))
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList()
    )

    fun checkout(){
        viewModelScope.launch {
            _loadState.emitAll(
                repository.checkout(
                    productList.value.map { it.value.id }
                )
            )
        }
    }

    fun updateCart(productId: Int, isInCart: Boolean){
        loadingItems.update { it + productId }
        viewModelScope.launch {
            val state = repository.updateCart(productId, isInCart)
            if(state is UiState.Error){
                _cartErrorState.emit(Unit)
            }
            loadingItems.update { it - productId }
        }
    }

}