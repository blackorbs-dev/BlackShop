package blackorbs.dev.blackshop.di

import androidx.test.filters.SmallTest
import blackorbs.dev.blackshop.data.local.ProductDao
import blackorbs.dev.blackshop.data.remote.ProductService
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.inject.Provider
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.KoinTest
import org.koin.test.verify.verifyAll
import java.util.concurrent.Executor
import java.util.concurrent.ScheduledExecutorService
import kotlin.test.Test

@SmallTest
class ModuleTest: KoinTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `verify dependencies injected`(){
        val modules = listOf(appModule, networkModule)
        modules.verifyAll(
            extraTypes = listOf(
                ProductDao::class,
                ProductService::class,
                HttpClientEngine::class,
                HttpClientConfig::class,
                FirebaseAuth::class,
                FirebaseApp::class,
                Provider::class,
                Executor::class,
                ScheduledExecutorService::class
            )
        )
    }
}