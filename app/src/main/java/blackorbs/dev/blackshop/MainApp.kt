package blackorbs.dev.blackshop

import android.app.Application
import blackorbs.dev.blackshop.di.appModule
import blackorbs.dev.blackshop.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class MainApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MainApp)
            modules(appModule, networkModule)
        }

        //Setup logging for debug builds
        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }

}