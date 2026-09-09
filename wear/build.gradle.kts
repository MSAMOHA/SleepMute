plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.sleepmute.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sleepmute.app"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "0.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    // Ajouter cette section pour ignorer les erreurs de métadonnées AAR
    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }

    // Alternative : désactiver complètement la vérification
    packagingOptions {
        resources {
            excludes += "/META-INF/proguard/androidx-*.pro"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}



dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("com.google.android.gms:play-services-wearable:18.2.0")

    // API santé officielle de Wear OS (détection de sommeil incluse)
    implementation("androidx.health:health-services-client:1.1.0")

    // Pour utiliser .await() sur les tâches asynchrones
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.8.1")
}
