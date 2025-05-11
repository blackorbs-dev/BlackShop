package blackorbs.dev.blackshop.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import blackorbs.dev.blackshop.data.local.Database
import blackorbs.dev.blackshop.data.local.ProductDao
import blackorbs.dev.blackshop.helpers.util.MainCoroutineRule
import blackorbs.dev.blackshop.helpers.util.TestUtil.testProduct
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@SmallTest
@ExperimentalCoroutinesApi
class ProductDaoTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var database: Database
    private lateinit var dao: ProductDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            Database::class.java
        ).allowMainThreadQueries().build()

        dao = database.productDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    private fun sampleProducts() =
        (1..9).map { testProduct(it) }

    @Test
    fun `addAll should Insert And Replace Data`() = runTest {
        dao.addAll(sampleProducts())
        val result = dao.getAll().first()
        assertThat(result).hasSize(9)
        assertThat(result.first().title).isEqualTo("ProductTest")

        // Replace item
        dao.addAll(listOf(testProduct(1,"Product1")))
        val updated = dao.getAll().first()
        assertThat(updated.first().title).isEqualTo("Product1")
    }

    @Test
    fun `updateCartItem should Toggle In Cart`() = runTest {
        dao.addAll(sampleProducts())
        assertThat(dao.getItem(2).first()?.isInCart).isFalse()
        dao.updateCartItem(2, true)
        val product = dao.getItem(2).first()
        assertThat(product?.isInCart).isTrue()
    }

    @Test
    fun `checkoutItems should Remove Items From Cart`() = runTest {
        dao.addAll(sampleProducts())
        dao.updateCartItem(1, true)
        assertThat(dao.getItem(1).first()?.isInCart).isTrue()
        dao.checkoutItems(listOf(1))
        val result = dao.getItem(1).first()
        assertThat(result?.isInCart).isFalse()
    }

    @Test
    fun `getCartItemIds should Return Only Cart Items`() = runTest {
        dao.addAll(sampleProducts())
        val cartIds = dao.getCartItemIds()
        assertThat(cartIds).isEmpty()
        (1..3).onEach { dao.updateCartItem(it, true) }
        assertThat(dao.getCartItemIds()).hasSize(3)
    }

    @Test
    fun `hasData should Return True When Data Exists`() = runTest {
        assertThat(dao.hasData()).isFalse()
        dao.addAll(sampleProducts())
        assertThat(dao.hasData()).isTrue()
    }
}
