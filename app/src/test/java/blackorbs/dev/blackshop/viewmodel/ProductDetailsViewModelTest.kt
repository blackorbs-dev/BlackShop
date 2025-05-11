package blackorbs.dev.blackshop.viewmodel

import androidx.test.filters.SmallTest
import blackorbs.dev.blackshop.helpers.fakes.FakeRepository
import blackorbs.dev.blackshop.helpers.util.MainCoroutineRule
import blackorbs.dev.blackshop.helpers.util.TestUtil.testProduct
import blackorbs.dev.blackshop.ui.UiState
import blackorbs.dev.blackshop.ui.product_details.ProductDetailsViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
@SmallTest
class ProductDetailsViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var repository: FakeRepository
    private lateinit var viewModel: ProductDetailsViewModel

    @Before
    fun setup() {
        repository = FakeRepository()
        repository.setFakeProducts(
            (1..3).map { testProduct(it) }
        )
        viewModel = ProductDetailsViewModel(repository)
    }

    @Test
    fun `initial product is null`() = runTest {
        assertThat(viewModel.product.value).isNull()
    }

    @Test
    fun `getProduct sets correct product`() = runTest {
        viewModel.getProduct(1)
        advanceUntilIdle()

        val product = viewModel.product.value
        assertThat(product).isNotNull()
        assertThat(product?.id).isEqualTo(1)
    }

    @Test
    fun `updateCart emits success when no error`() = runTest {
        viewModel.getProduct(1)
        advanceUntilIdle()

        viewModel.updateCart()
        advanceUntilIdle()

        val state = viewModel.loadState.value
        assertThat(state).isEqualTo(UiState.Success)
    }

    @Test
    fun `updateCart emits error when repository throws`() = runTest {
        repository.setShouldThrowError(true)

        viewModel.getProduct(1)
        advanceUntilIdle()

        viewModel.updateCart()
        advanceUntilIdle()

        val state = viewModel.loadState.value
        assertThat(state is UiState.Error).isTrue()
    }

    @Test
    fun `updateCart sets isInCart to true`() = runTest {
        viewModel.getProduct(1)
        advanceUntilIdle()

        viewModel.updateCart()
        advanceUntilIdle()

        val updated = viewModel.product.value
        assertThat(updated?.isInCart).isTrue()
    }
}
