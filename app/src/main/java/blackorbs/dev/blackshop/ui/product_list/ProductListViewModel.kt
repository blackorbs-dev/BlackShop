package blackorbs.dev.blackshop.ui.product_list

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

class ProductListViewModel(
    private val repository: ProductRepository
): ViewModel() {

    private val loadingItems = MutableStateFlow<Set<Int>>(emptySet())
    private val _refreshState = MutableStateFlow<UiState>(UiState.Loading)
    val refreshState = _refreshState.asStateFlow()
    private val _cartErrorState = MutableSharedFlow<Unit>()
    val cartErrorState = _cartErrorState.asSharedFlow()

    val productList = combine(
        repository.productList,
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

    init {
        refresh()
    }

    fun refresh(){
        viewModelScope.launch {
            _refreshState.emitAll(repository.refreshFlow)
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