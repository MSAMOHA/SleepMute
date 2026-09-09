package com.sleepmute.app

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.health.services.client.HealthServices
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.PassiveMonitoringConfig
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch

class MainActivity : Activity() {

    // Client Health Services : la passerelle vers les capteurs de la montre
    private val passiveMonitoringClient by lazy {
        HealthServices.getClient(this).passiveMonitoringClient
    }

    private lateinit var statusText: TextView
    private lateinit var startButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        startButton = findViewById(R.id.startButton)

        startButton.setOnClickListener {
            if (!hasRequiredPermissions()) {
                requestRequiredPermissions()
                return@setOnClickListener
            }
            startSleepMonitoring()
        }
    }

    private fun startSleepMonitoring() {
        lifecycleScope.launch {
            try {
                // Vérifie que la montre sait détecter le sommeil
                val capabilities = passiveMonitoringClient.capabilities.await()
                if (DataType.SLEEP_STAGE !in capabilities.supportedDataTypesPassiveMonitoring) {
                    statusText.text = "Cette montre ne supporte pas la détection de sommeil."
                    return@launch
                }

                // Demande à Wear OS de nous prévenir à chaque changement de phase
                val config = PassiveMonitoringConfig.builder()
                    .setDataTypes(setOf(DataType.SLEEP_STAGE))
                    .setShouldUserActivityInfoBeRequested(false)
                    .build()

                passiveMonitoringClient
                    .setPassiveListenerServiceAsync(SleepPassiveService::class.java, config)
                    .await()

                statusText.text = "Surveillance activée ! Dors bien."
                startButton.isEnabled = false
                startButton.text = "Surveillance active"
            } catch (e: Exception) {
                statusText.text = "Erreur : ${e.message}"
            }
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
            != PackageManager.PERMISSION_GRANTED) return false
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) return false
        return true
    }

    private fun requestRequiredPermissions() {
        val permissions = mutableListOf(Manifest.permission.BODY_SENSORS)
        if (Build.VERSION.SDK_INT >= 33) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 1)
    }
}
