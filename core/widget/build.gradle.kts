import com.whakaara.structure.Modules
import com.whakaara.structure.modules

plugins {
    alias(libs.plugins.whakaara.android.library)
    alias(libs.plugins.whakaara.library.compose)
    alias(libs.plugins.whakaara.hilt)
    alias(libs.plugins.whakaara.lint)
}

android {
    namespace = "com.whakaara.core.widget"
}

dependencies {
    // Modules
    modules(Modules.coreDesignSystem, Modules.coreCommon)
    api(project(Modules.coreData))

    // For AppWidgets support
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)

    // Google
    implementation(libs.gson)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.test.junit.ext)
    androidTestImplementation(libs.androidx.espresso.core)
}
