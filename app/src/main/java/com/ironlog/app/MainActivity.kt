package com.ironlog.app

import android.content.Intent
import android.os.Bundle
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .systemBarsPadding()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = getString(R.string.settings_android_version_notice),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                } else {
                    IronLogNavHost(
                        openSessionId = openSessionId.value,
                        onSessionOpened = { openSessionId.value = null },
                    )
                }
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
