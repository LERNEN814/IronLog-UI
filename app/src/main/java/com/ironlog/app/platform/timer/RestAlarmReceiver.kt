package com.ironlog.app.platform.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ironlog.app.data.timer.RestTimerRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/** REST_TIMER section 2: alarm fired -> repository decides between in-app alert and notification. */
@AndroidEntryPoint
class RestAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var restTimerRepository: RestTimerRepository

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            try {
                restTimerRepository.onAlarmFired()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
