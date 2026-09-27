package com.ironlog.app.ui.feature.timer

import android.content.Context
import android.media.RingtoneManager
import android.os.VibrationEffect
import android.os.Vibrator

/** REST_TIMER section 6: in-app alert when the timer finishes while the app is visible. */
fun playRestFinishedAlert(context: Context) {
    val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    RingtoneManager.getRingtone(context, uri)?.play()
    val vibrator = context.getSystemService(Vibrator::class.java)
    vibrator?.vibrate(VibrationEffect.createOneShot(300L, VibrationEffect.DEFAULT_AMPLITUDE))
}
