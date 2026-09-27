package com.ironlog.app

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.ironlog.app.core.di.ApplicationScope
import com.ironlog.app.data.seed.SeedImporter
import com.ironlog.app.platform.timer.ForegroundTimerBridge
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@HiltAndroidApp
class IronLogApp : Application() {

    @Inject
    lateinit var seedImporter: SeedImporter

    @Inject
    lateinit var foregroundTimerBridge: ForegroundTimerBridge

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        // D5: foreground/background transitions drive the ongoing rest notification.
        ProcessLifecycleOwner.get().lifecycle.addObserver(foregroundTimerBridge)
        applicationScope.launch { seedImporter.importFromAssets() }
    }
}
