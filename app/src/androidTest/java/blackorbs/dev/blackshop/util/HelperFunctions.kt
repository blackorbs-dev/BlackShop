package blackorbs.dev.blackshop.util

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import blackorbs.dev.blackshop.R
import blackorbs.dev.blackshop.data.remote.Api
import blackorbs.dev.blackshop.ui.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.mockk.every
import io.mockk.mockk
import java.io.IOException

fun launchMainActivity(
    onComplete: Robot.() -> Unit
){
    val scenario = ActivityScenario.launch(MainActivity::class.java)
    onComplete(Robot())
    scenario.close()
}

fun launchFragmentWithNavController(
    fragmentClass: Fragment,
    themeResId: Int = R.style.Theme_BlackShop,
    navGraphId: Int = R.navigation.nav_graph,
    fragmentArgs: Bundle? = null
): FragmentScenario<Fragment> {
    val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    return launchFragmentInContainer(
        fragmentArgs = fragmentArgs,
        themeResId = themeResId
    ) {
        fragmentClass.also { fragment ->
            fragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                if (viewLifecycleOwner != null) {
                    navController.setGraph(navGraphId)
                    Navigation.setViewNavController(fragment.requireView(), navController)
                }
            }
        }
    }
}

fun getMockApiResponse(throwError: Boolean): suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = { request ->
    if(throwError) throw IOException()
    when (request.url.encodedPath) {
        Api.PRODUCTS -> respond(
            content = loadJsonFromResources("products.json"),
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
        Api.CHECKOUT -> respond("")
        Api.ADD_TO_CART -> respond("")
        Api.REMOVE_FROM_CART -> respond("")
        else -> respondError(HttpStatusCode.NotFound)
    }
}

fun mockFirebaseLogin(
    auth: FirebaseAuth,
    throwError: Boolean = false,
    isLoggedIn: Boolean = false
) {
    every { auth.currentUser } returns if(isLoggedIn) mockk(relaxed = true) else null

    val mockTask = mockk<Task<AuthResult>>(relaxed = true)

    every { auth.signInWithEmailAndPassword(any(), any()) } returns mockTask
    every { auth.createUserWithEmailAndPassword(any(), any()) } returns mockTask

    every { mockTask.addOnCompleteListener(any()) } answers {
        val listener = firstArg<OnCompleteListener<AuthResult>>()
        every { mockTask.isSuccessful } returns !throwError
        every { mockTask.exception } returns if (throwError) IOException("Login failed") else null
        listener.onComplete(mockTask)
        mockTask
    }
}