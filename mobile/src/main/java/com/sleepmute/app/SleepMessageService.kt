package com.sleepmute.app

import android.media.AudioManager
import android.util.Log
import android.view.KeyEvent
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Reçoit le message "asleep" de la montre et met en pause
 * n'importe quel média en cours de lecture sur le téléphone
 * (YouTube, Spotify, Netflix, lecteur local...).
 */
class SleepMessageService : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path != "/sleep") return

        val state = String(messageEvent.data)
        Log.d(TAG, "Message reçu de la montre : $state")

        if (state == "asleep") {
            pauseMedia()
            val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            SleepStatus.message = "😴 Dormeur détecté à $time — lecture mise en pause."
        }
    }

    /** Simule l'appui sur le bouton "pause" des écouteurs : fonctionne avec presque toutes les apps. */
    private fun pauseMedia() {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager.dispatchMediaKeyEvent(
            KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE)
        )
        audioManager.dispatchMediaKeyEvent(
            KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE)
        )
        Log.d(TAG, "Commande PAUSE envoyée")
    }

    companion object {
        private const val TAG = "SleepMessageService"
    }
}
