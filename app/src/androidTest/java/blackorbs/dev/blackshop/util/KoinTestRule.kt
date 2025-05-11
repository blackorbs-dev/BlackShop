package blackorbs.dev.blackshop.util

import androidx.test.platform.app.InstrumentationRegistry
import blackorbs.dev.blackshop.di.appModule
import blackorbs.dev.blackshop.di.networkModule
import blackorbs.dev.blackshop.di.testAppModule
import blackorbs.dev.blackshop.di.testNetworkModule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.getKoinApplicationOrNull
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.unloadKoinModules
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module

class KoinTestRule(
    private val modules: List<Module> = listOf(
        appModule, networkModule,
        testAppModule, testNetworkModule
    )
) : TestWatcher() {
    override fun starting(description: Description) {

        if (getKoinApplicationOrNull() == null) {
            startKoin {
                androidContext(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext)
                modules(modules)
            }
        } else {
            loadKoinModules(modules)
        }
    }

    override fun finished(description: Description) {
        unloadKoinModules(modules)
    }
}