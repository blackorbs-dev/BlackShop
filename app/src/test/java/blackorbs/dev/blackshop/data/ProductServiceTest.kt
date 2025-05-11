package blackorbs.dev.blackshop.data

import androidx.test.filters.SmallTest
import blackorbs.dev.blackshop.data.entity.Rating
import blackorbs.dev.blackshop.data.remote.ProductService
import blackorbs.dev.blackshop.data.remote.ProductServiceImpl
import blackorbs.dev.blackshop.helpers.util.TestExtensions
import blackorbs.dev.blackshop.helpers.util.TestUtil.testProduct
import com.google.common.truth.Truth.assertThat
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertFailsWith

@SmallTest
class ProductServiceTest {
    private lateinit var service: ProductService
    private lateinit var mockEngine: MockEngine
    private lateinit var httpClient: HttpClient

    private fun setupMockEngine(respond: MockRequestHandleScope.(HttpRequestData) -> HttpResponseData) {
        mockEngine = MockEngine(respond)

        httpClient = HttpClient(mockEngine) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        service = ProductServiceImpl(httpClient)
    }

    @Test
    fun `getAllProducts returns product list`() = runTest {
        setupMockEngine {
            respond(
                content = TestExtensions.loadJsonFromResources("products.json"),
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val result = service.getAllProducts()
        assertThat(result).hasSize(1)
        assertThat(result).isEqualTo(
            listOf(
                testProduct(
                    id = 1,
                    title = "Fjallraven - Foldsack No. 1 Backpack, Fits 15 Laptops",
                    price = 109.95,
                    description = "Your perfect pack for everyday use and walks in the forest. Stash your laptop (up to 15 inches) in the padded sleeve, your everyday",
                    category = "men's clothing",
                    image = "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg",
                    rating = Rating(
                        rate = 3.9f,
                        count = 120
                    )
                )
            )
        )
    }

    @Test
    fun `addToCart completes without error`() = runTest {
        setupMockEngine {
            respond("")
        }
        service.addToCart(1) // Should not throw
    }

    @Test
    fun `removeFromCart completes without error`() = runTest {
        setupMockEngine {
            respond("")
        }
        service.removeFromCart(2) // Should not throw
    }

    @Test
    fun `checkout sends request and succeeds`() = runTest {
        setupMockEngine {
            respond("")
        }
        service.checkout(listOf(1, 2))
    }

    @Test
    fun `getAllProducts throws on error`() = runTest {
        setupMockEngine {
            respond("Not Found", HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        assertFailsWith<ClientRequestException> {
            service.addToCart(1)
        }
    }
}