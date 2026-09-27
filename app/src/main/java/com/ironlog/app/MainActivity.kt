package com.ironlog.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.ironlog.app.domain.timer.RestTimer
import com.ironlog.app.platform.timer.RestNotifications
import com.ironlog.app.ui.MainViewModel
import com.ironlog.app.ui.navigation.IronLogNavHost
import com.ironlog.app.ui.theme.IronLogTheme
import com.ironlog.app.ui.theme.ThemeMode
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /** REST_TIMER section 7: restoring on every foreground entry is cheap and idempotent. */
    @Inject
    lateinit var restTimer: RestTimer

    /** Session requested by a notification tap; consumed by the nav host. */
    private val openSessionId = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Only the launch intent should navigate; a config change must not re-open the session.
        openSessionId.value = if (savedInstanceState == null) sessionIdFrom(intent) else null
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val darkTheme = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            IronLogTheme(darkTheme = darkTheme) {
                IronLogNavHost(
                    openSessionId = openSessionId.value,
                    onSessionOpened = { openSessionId.value = null },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        openSessionId.value = sessionIdFrom(intent)
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch { restTimer.restore() }
    }

    private fun sessionIdFrom(intent: Intent?): String? =
        intent?.getStringExtra(RestNotifications.EXTRA_SESSION_ID)
}
