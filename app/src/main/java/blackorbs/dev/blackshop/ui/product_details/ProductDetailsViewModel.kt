package blackorbs.dev.blackshop.ui.product_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blackorbs.dev.blackshop.data.repository.ProductRepository
import blackorbs.dev.blackshop.ui.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductDetailsViewModel(
    private val repository: ProductRepository
): ViewModel() {

    private val _loadState = MutableStateFlow<UiState>(UiState.Idle)
    val loadState = _loadState.asStateFlow()

    private val productId = MutableStateFlow(-1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val product = productId.flatMapLatest {
        repository.getProduct(it)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun getProduct(value: Int) {
        viewModelScope.launch {
            productId.emit(value)
        }
    }

    fun updateCart(){
        _loadState.value = UiState.Loading
        viewModelScope.launch {
            _loadState.emit(
                repository.updateCart(
                    productId.value, true
                )
            )
        }
    }
}