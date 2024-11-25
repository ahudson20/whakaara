import com.whakaara.structure.Modules
import com.whakaara.structure.modules

plugins {
    alias(libs.plugins.whakaara.android.feature)
    alias(libs.plugins.whakaara.hilt)
}

android {
    namespace = "com.whakaara.feature.alarm"
}

dependencies {
    // Modules
    modules(Modules.coreCommon, Modules.coreDesignSystem)

    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.accompanist.permissions)
    implementation(libs.compose.material3.datetime.pickers)
    implementation(libs.dokar3.bottomsheet)
    implementation(libs.chargemap.numberpicker)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.test.junit.ext)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit)
}
