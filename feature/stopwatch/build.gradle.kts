import com.whakaara.structure.Modules
import com.whakaara.structure.modules

plugins {
    alias(libs.plugins.whakaara.android.feature)
    alias(libs.plugins.whakaara.hilt)
}

android {
    namespace = "com.whakaara.feature.stopwatch"
}

dependencies {
    // Modules
    modules(Modules.coreCommon, Modules.coreDesignSystem)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material3.windowSizeClass)
    implementation(libs.androidx.adaptive.android)
    implementation(libs.androidx.adaptive.layout)


    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.test.junit.ext)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit)

    debugImplementation(libs.androidx.ui.test.manifest)
}
