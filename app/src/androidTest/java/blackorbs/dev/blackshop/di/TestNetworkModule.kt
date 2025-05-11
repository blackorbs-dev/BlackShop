package blackorbs.dev.blackshop.di

import blackorbs.dev.blackshop.util.getMockApiResponse
import blackorbs.dev.blackshop.util.provideTestHttpClient
import com.google.firebase.auth.FirebaseAuth
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.mockk.mockk
import org.koin.dsl.module

val testNetworkModule = module {
    single<FirebaseAuth> { mockk(relaxed = true) }

    single { MutableMockHandler() }
    single {
        provideTestHttpClient(get<MutableMockHandler>())
    }
}

class MutableMockHandler {
    var handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData =
        getMockApiResponse(false)
}


