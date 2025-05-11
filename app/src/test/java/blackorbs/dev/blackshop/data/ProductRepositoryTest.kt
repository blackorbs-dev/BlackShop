package blackorbs.dev.blackshop.data

import androidx.test.filters.SmallTest
import blackorbs.dev.blackshop.data.repository.ProductRepository
import blackorbs.dev.blackshop.data.repository.ProductRepositoryImpl
import blackorbs.dev.blackshop.helpers.fakes.FakeProductDao
import blackorbs.dev.blackshop.helpers.fakes.FakeProductService
import blackorbs.dev.blackshop.helpers.util.TestUtil.testProduct
import blackorbs.dev.blackshop.ui.UiState
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test

@SmallTest
class ProductRepositoryImplTest {

    private lateinit var dao: FakeProductDao
    private lateinit var service: FakeProductService
    private lateinit var repository: ProductRepository

    @Before
    fun setup() {
        dao = FakeProductDao()
        service = FakeProductService()
        repository = ProductRepositoryImpl(dao, service)
    }

    @Test
    fun `refreshFlow emits Success and updates DAO`() = runTest {
        assertThat(dao.getAll().first()).isEmpty()

        val emissions = mutableListOf<UiState>()
        repository.refreshFlow.take(2).toList(emissions)

        assertThat(UiState.Loading).isEqualTo(emissions[0])
        assertThat(UiState.Success).isEqualTo(emissions[1])
        assertThat(dao.getAll().first()).isNotEmpty()
    }

    @Test
    fun `refreshFlow emits Error on failure`() = runTest {
        service.setShouldThrowError(true)
        val emissions = mutableListOf<UiState>()
        repository.refreshFlow.take(2).toList(emissions)
        assertThat(UiState.Loading).isEqualTo(emissions[0])
        assertThat(UiState.Error(null)).isEqualTo(emissions[1])
    }

    @Test
    fun `updateCart returns Success and DAO reflects change`() = runTest {
        dao.addAll(listOf(testProduct().copy(id = 1)))
        val result = repository.updateCart(1, true)
        assertThat(UiState.Success).isEqualTo(result)
        assertThat(dao.getItem(1).first()!!.isInCart).isTrue()
    }

    @Test
    fun `updateCart returns Error when service fails`() = runTest {
        dao.addAll(listOf(testProduct().copy(id = 2)))
        service.setShouldThrowError(true)
        val result = repository.updateCart(2, true)
        assertThat(UiState.Error(null)).isEqualTo(result)
    }

    @Test
    fun `checkout emits Success and clears cart items`() = runTest {
        dao.addAll(listOf(
            testProduct().copy(id = 1, isInCart = true),
            testProduct().copy(id = 2, isInCart = true)
        ))
        val states = repository.checkout(listOf(1, 2)).toList()
        assertThat(UiState.Loading).isEqualTo(states[0])
        assertThat(UiState.Success).isEqualTo(states[1])
        assertThat(dao.getCartItems().first()).isEmpty()
    }

    @Test
    fun `checkout emits Error on failure`() = runTest {
        service.setShouldThrowError(true)
        dao.addAll(listOf(testProduct().copy(id = 3, isInCart = true)))
        val states = repository.checkout(listOf(3)).toList()
        assertThat(UiState.Loading).isEqualTo(states[0])
        assertThat(UiState.Error(null)).isEqualTo(states[1])
        assertThat(dao.getCartItems().first()).isNotEmpty()
    }
}
