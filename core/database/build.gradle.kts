import com.whakaara.structure.Modules
import com.whakaara.structure.modules

plugins {
    alias(libs.plugins.whakaara.android.library)
    alias(libs.plugins.whakaara.room)
    alias(libs.plugins.whakaara.hilt)
    alias(libs.plugins.whakaara.lint)
    alias(libs.plugins.kotlinx.kover)
}

android {
    namespace = "com.whakaara.core.database"

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
    modules(Modules.coreCommon, Modules.coreModel)

    implementation(libs.gson)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.ui.graphics.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.arch.core.testing)
    androidTestImplementation(libs.jetbrains.kotlinx.coroutines.test)
    androidTestImplementation(libs.turbine)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.test.junit.ext)
}
