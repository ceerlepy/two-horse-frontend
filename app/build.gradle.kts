plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.twohorse.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.twohorse.app"
        minSdk = 26
        targetSdk = 35
        // CI passes its run number so every Play upload gets a higher
        // versionCode, which Play requires. Local builds stay at 1.
        versionCode =
            System.getenv("VERSION_CODE")?.toIntOrNull() ?: 1
        versionName = "1.0.0"

        // The OAuth "Web application" client ID is not a secret; it comes
        // from the GOOGLE_WEB_CLIENT_ID GitHub Actions variable so it can
        // be set without a code change.
        buildConfigField(
            "String",
            "GOOGLE_WEB_CLIENT_ID",
            "\"${System.getenv("GOOGLE_WEB_CLIENT_ID")?.trim().orEmpty()}\""
        )
    }

    /*
     * Upload key for Google Play (Play App Signing re-signs with Google's
     * own key). The keystore and passwords only exist as GitHub secrets
     * in the release workflow; without them the release build stays
     * unsigned and the workflow fails before uploading anything.
     */
    val uploadKeystore =
        System.getenv("UPLOAD_KEYSTORE_PATH")

    signingConfigs {
        if (uploadKeystore != null) {
            create("upload") {
                storeFile = file(uploadKeystore)
                storePassword = System.getenv("UPLOAD_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("UPLOAD_KEY_ALIAS")
                keyPassword = System.getenv("UPLOAD_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            if (uploadKeystore != null) {
                signingConfig =
                    signingConfigs.getByName("upload")
            }
        }
    }

    compileOptions {
        sourceCompatibility =
            JavaVersion.VERSION_17

        targetCompatibility =
            JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources.excludes +=
            "/META-INF/{AL2.0,LGPL2.1}"
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(
        platform(
            "androidx.compose:compose-bom:2024.12.01"
        )
    )

    implementation(
        "androidx.activity:activity-compose:1.10.0"
    )

    implementation(
        "androidx.lifecycle:lifecycle-runtime-ktx:2.8.7"
    )

    implementation("androidx.core:core-ktx:1.15.0")

    implementation(
        "androidx.compose.material3:material3"
    )

    implementation(
        "androidx.compose.material:material-icons-extended"
    )

    implementation(
        "androidx.compose.ui:ui"
    )

    implementation(
        "androidx.compose.ui:ui-tooling-preview"
    )

    debugImplementation(
        "androidx.compose.ui:ui-tooling"
    )

    implementation(
        "com.squareup.okhttp3:okhttp:4.12.0"
    )
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.credentials:credentials:1.5.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation("com.android.billingclient:billing-ktx:8.0.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
}
