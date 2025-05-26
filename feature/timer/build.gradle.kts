import com.whakaara.structure.Modules
import com.whakaara.structure.modules

plugins {
    alias(libs.plugins.whakaara.android.feature)
    alias(libs.plugins.whakaara.hilt)
}

android {
    namespace = "com.whakaara.feature.timer"
}

dependencies {
    // Modules
    modules(Modules.coreCommon, Modules.coreDesignSystem)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.service)
    implementation(libs.androidx.adaptive.android)
    implementation(libs.androidx.adaptive.layout)
    implementation(libs.accompanist.permissions)
    implementation(libs.gson)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.test.junit.ext)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit)

    debugImplementation(libs.androidx.ui.test.manifest)
}
