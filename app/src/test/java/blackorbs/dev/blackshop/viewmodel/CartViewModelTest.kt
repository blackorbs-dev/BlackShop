package blackorbs.dev.blackshop.viewmodel

import androidx.test.filters.SmallTest
import blackorbs.dev.blackshop.helpers.fakes.FakeRepository
import blackorbs.dev.blackshop.helpers.util.MainCoroutineRule
import blackorbs.dev.blackshop.helpers.util.TestUtil.testProduct
import blackorbs.dev.blackshop.ui.UiState
import blackorbs.dev.blackshop.ui.cart.CartViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
@SmallTest
class CartViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var repository: FakeRepository
    private lateinit var viewModel: CartViewModel

    @Before
    fun setup() {
        repository = FakeRepository()
        repository.setFakeProducts(
            (1..3).map {
                testProduct(it, isInCart = it%2 == 0) // Add product 2 to cart
            }
        )
        viewModel = CartViewModel(repository)
    }

    @Test
    fun `productList returns only cart items`() = runTest {
        val products = viewModel.productList.drop(1).first()
        assertThat(products.size).isEqualTo(1)
        assertThat(products.first().value.id).isEqualTo(2)
    }

    @Test
    fun `updateCart removes item from cart`() = runTest {
        viewModel.updateCart(2, false) // remove product 2 from cart
        advanceUntilIdle()

        val products = viewModel.productList.first()
        assertThat(products).isEmpty()
    }

    @Test
    fun `updateCart emits error event on failure`() = runTest {
        repository.setShouldThrowError(true)

        val errorEmitted = async {
            viewModel.cartErrorState.first()
        }

        viewModel.updateCart(2, false)
        advanceUntilIdle()

        assertThat(errorEmitted.await()).isEqualTo(Unit)
    }

    @Test
    fun `checkout emits success when no error`() = runTest {
        viewModel.checkout()
        advanceUntilIdle()

        val state = viewModel.loadState.value
        assertThat(state).isEqualTo(UiState.Success)
    }

    @Test
    fun `checkout emits error when repository throws`() = runTest {
        repository.setShouldThrowError(true)

        viewModel.checkout()
        advanceUntilIdle()

        val state = viewModel.loadState.value
        assertThat(state is UiState.Error).isTrue()
    }

    @Test
    fun `loading state tracks updated items`() = runTest {
        val itemId = 2
        val before = viewModel.productList.drop(1).first()
            .find { it.value.id == itemId }?.isLoading
        assertThat(before).isFalse()

        viewModel.updateCart(itemId, false)
        val during = viewModel.productList.drop(1).first()
            .find { it.value.id == itemId }?.isLoading
        assertThat(during).isTrue()

        val after = viewModel.productList.drop(1).first()
            .find { it.value.id == itemId }
        assertThat(after).isNull() // Removed from cart
    }
}
