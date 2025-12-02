plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "dev.esan.sla_app"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.esan.sla_app"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    // --- Dependencias existentes ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ink.strokes)
    // Asegúrate de tener estas (o versiones más recientes como 2.0.0-alpha.X)
    implementation("com.patrykandpatrick.vico:compose:1.14.0")
    implementation("com.patrykandpatrick.vico:compose-m3:1.14.0")
    implementation("com.patrykandpatrick.vico:core:1.14.0") // <-- Esta es vital para ComposedChart
    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)




    // ============================================================
    // 🔥 RETROFIT + OKHTTP
    // ============================================================
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")



    // ============================================================
    // 🔥 COROUTINES
    // ============================================================
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")



    // ============================================================
    // 🔥 DATASTORE
    // ============================================================
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // ============================================================
    // 🔥 JWT DECODE
    // ============================================================
    implementation("com.auth0.android:jwtdecode:2.0.2")


    // ============================================================
    // 🔥 NAVIGATION COMPOSE
    // ============================================================
    implementation("androidx.navigation:navigation-compose:2.8.0")



    // ============================================================
    // 🔥 LIFECYCLE + VIEWMODEL COMPOSE
    // ============================================================
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.0")



    // ============================================================
    // 🔥 MATERIAL ICONS (opcional recomendable)
    // ============================================================
    implementation("androidx.compose.material:material-icons-extended:1.6.0")



    // ============================================================
    // 🔥 ACTIVITY KTX
    // ============================================================
    implementation("androidx.activity:activity-ktx:1.9.0")


    // ============================================================
    // VICO CHARTS
    // ============================================================
    implementation("com.patrykandpatrick.vico:compose-m3:1.14.0")


}