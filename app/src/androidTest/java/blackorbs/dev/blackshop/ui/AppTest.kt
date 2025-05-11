package blackorbs.dev.blackshop.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import blackorbs.dev.blackshop.R
import blackorbs.dev.blackshop.util.KoinTestRule
import blackorbs.dev.blackshop.util.TestUtil
import blackorbs.dev.blackshop.util.launchMainActivity
import blackorbs.dev.blackshop.util.mockFirebaseLogin
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.koin.core.component.get
import org.koin.test.KoinTest
import kotlin.test.Test

@MediumTest
@RunWith(AndroidJUnit4::class)
class AppTest: KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule()

    private lateinit var auth: FirebaseAuth

    @Before
    fun setUp(){
        auth = get()
    }

    @Test
    fun navigateToFroSignUp(){
        mockFirebaseLogin(auth)
        launchMainActivity {
            assertTextDisplayed(R.string.welcome_back)
            clickButton(R.id.gotoSignUp)
            assertTextNotDisplayed(R.string.welcome_back)
            assertTextDisplayed(R.string.create_account)
            clickButton(R.id.goToLogin)
            assertTextDisplayed(R.string.welcome_back)
        }
    }

    @Test
    fun successfulLogin_navigatesToProductList(){
        mockFirebaseLogin(auth)
        launchMainActivity {
            enterText(R.string.email, "test@example.com")
            enterText(R.string.password, "123456")
            clickButton(R.id.action_btn)
            assertTextDisplayed(R.string.products_listing)
        }
    }

    @Test
    fun productList_toFroDetails(){
        mockFirebaseLogin(auth, isLoggedIn = true)
        launchMainActivity {
            assertTextDisplayed(R.string.products_listing)
            assertTextInListItem(0, testProduct.title.substring(0, 10))
            clickListItemAtPosition(0)
            assertTextDisplayed(R.string.details)
            assertTextDisplayed(testProduct.title)
            assertTextDisplayed(testProduct.description)
            clickButtonWithContentDescription(
                androidx.appcompat.R.string.abc_action_bar_up_description
            )
            assertTextDisplayed(R.string.products_listing)
        }
    }

    @Test
    fun productDetails_toCheckout() {
        mockFirebaseLogin(auth, isLoggedIn = true)
        launchMainActivity {
            clickListItemAtPosition(0)
            assertTextDisplayed(R.string.add_cart)
            clickButton(R.id.action_btn)
            assertTextDisplayed(R.string.view_cart)
            clickButton(R.id.action_btn)
            assertTextDisplayed(R.string.total)
            assertTextDisplayed(testProduct.title)
            clickButton(R.id.action_btn)
            assertTextDisplayed(R.string.success)
            clickButton(R.id.okay_btn)
            assertTextDisplayed(R.string.no_cart_items)
        }
    }

    private val testProduct = TestUtil.testProduct(
        id = 1,
        title = "Fjallraven - Foldsack No. 1 Backpack, Fits 15 Laptops",
        price = 109.95,
        description = "Your perfect pack for everyday use and walks in the forest. Stash your laptop (up to 15 inches) in the padded sleeve, your everyday",
        category = "men's clothing",
        image = "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg",
        rating = blackorbs.dev.blackshop.data.entity.Rating(
            rate = 3.9f,
            count = 120
        )
    )

}