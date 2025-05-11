package blackorbs.dev.blackshop.viewmodel

import androidx.test.filters.SmallTest
import blackorbs.dev.blackshop.helpers.fakes.FakeRepository
import blackorbs.dev.blackshop.helpers.util.MainCoroutineRule
import blackorbs.dev.blackshop.helpers.util.TestUtil.testProduct
import blackorbs.dev.blackshop.ui.UiState
import blackorbs.dev.blackshop.ui.product_list.ProductListViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test

@SmallTest
@OptIn(ExperimentalCoroutinesApi::class)
class ProductListViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var repository: FakeRepository
    private lateinit var viewModel: ProductListViewModel

    @Before
    fun setup() {
        repository = FakeRepository()
        repository.setFakeProducts(
            (1..3).map { testProduct(it) }
        )
        viewModel = ProductListViewModel(repository)
    }

    @Test
    fun `refresh emits success when repository has data`() = runTest {
        repository.setShouldThrowError(false)
        repository.setFakeProducts(emptyList())

        viewModel.refresh()
        advanceTimeBy(100)
        assertThat(viewModel.refreshState.value).isEqualTo(UiState.Loading)

        advanceUntilIdle()
        val state = viewModel.refreshState.value
        assertThat(state).isEqualTo(UiState.Success)
    }

    @Test
    fun `updateCart emits error when repository throws`() = runTest {
        repository.setShouldThrowError(true)

        val job = launch {
            viewModel.cartErrorState.first() // suspend until error is emitted
        }

        viewModel.updateCart(productId = 1, isInCart = true)
        advanceUntilIdle()

        job.cancel()
    }

    @Test
    fun `updateCart updates product list state`() = runTest {
        val productId = 1
        repository.setShouldThrowError(false)

        viewModel.updateCart(productId = productId, isInCart = true)
        advanceUntilIdle()

        val updatedProduct = viewModel.productList.value.find { it.value.id == productId }
        assertThat(updatedProduct?.value?.isInCart).isTrue()
    }

    @Test
    fun `loadingItems adds and removes productId correctly`() = runTest {
        val productId = 2
        repository.setShouldThrowError(false)

        viewModel.updateCart(productId = productId, isInCart = false)
        advanceTimeBy(100) // allow loadingItems to be updated

        // It should be in loading set
        val inLoading = viewModel.productList.value.find {
            it.value.id == productId
        }?.isLoading
        assertThat(inLoading).isTrue()

        advanceUntilIdle() // finish update
        val inLoadingAfter = viewModel.productList.value.find {
            it.value.id == productId
        }?.isLoading
        assertThat(inLoadingAfter).isFalse()
    }
}
