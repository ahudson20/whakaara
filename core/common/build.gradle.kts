plugins {
    alias(libs.plugins.whakaara.android.library)
    alias(libs.plugins.whakaara.library.compose)
    alias(libs.plugins.whakaara.hilt)
    alias(libs.plugins.whakaara.lint)
}

android {
    namespace = "com.whakaara.core.common"

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.material3)
    implementation(libs.gson)

    // For AppWidgets support
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)

    testImplementation(libs.junit)
}
