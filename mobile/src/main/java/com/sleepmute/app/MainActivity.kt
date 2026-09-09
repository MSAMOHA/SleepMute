package com.sleepmute.app

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

// Petit "panneau d'affichage" partagé avec le service
object SleepStatus {
    @Volatile
    var message: String = "En attente de la montre..."
}

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.statusText).text = SleepStatus.message
    }
}
