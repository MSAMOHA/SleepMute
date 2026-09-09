package com.sleepmute.app

import android.util.Log
import androidx.health.services.client.PassiveListenerService
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.PassiveMonitoringEvent
import androidx.health.services.client.data.SleepStageType
import com.google.android.gms.wearable.Wearable

/**
 * Wear OS appelle ce service automatiquement dès que la phase
 * de sommeil change (grâce aux capteurs de la montre).
 * Quand tu t'endors, on prévient le téléphone.
 */
class SleepPassiveService : PassiveListenerService() {

    private var wasAsleep = false

    override fun onNewDataPointsReceived(
        dataPoints: List<PassiveMonitoringEvent.DataPointContainer>
    ) {
        for (container in dataPoints) {
            if (container.dataType != DataType.SLEEP_STAGE) continue

            val stage = container.getValue(DataType.SLEEP_STAGE)
            Log.d(TAG, "Phase de sommeil : $stage")

            val isAsleep = stage != SleepStageType.AWAKE &&
                    stage != SleepStageType.UNKNOWN &&
                    stage != SleepStageType.OUT_OF_SLEEP

            // On n'envoie le message qu'une seule fois, au moment de s'endormir
            if (isAsleep && !wasAsleep) {
                wasAsleep = true
                sendToPhone("asleep")
            } else if (!isAsleep && wasAsleep) {
                wasAsleep = false
            }
        }
    }

    private fun sendToPhone(state: String) {
        Wearable.getNodeClient(this).connectedNodes
            .addOnSuccessListener { nodes ->
                val node = nodes.firstOrNull() ?: return@addOnSuccessListener
                Wearable.getMessageClient(this)
                    .sendMessage(node.id, PATH, state.toByteArray())
                    .addOnSuccessListener {
                        Log.d(TAG, "Message envoyé au téléphone : $state")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Échec de l'envoi", e)
                    }
            }
    }

    companion object {
        private const val TAG = "SleepPassiveService"
        private const val PATH = "/sleep"
    }
}
