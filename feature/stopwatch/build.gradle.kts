import com.whakaara.structure.Modules
import com.whakaara.structure.modules

plugins {
    alias(libs.plugins.whakaara.android.feature)
    alias(libs.plugins.whakaara.hilt)
}

android {
    namespace = "net.vbuild.verwoodpages.stopwatch"
}

dependencies {
    // Modules
    modules(Modules.coreCommon)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.test.junit.ext)
    androidTestImplementation(libs.androidx.espresso.core)
}
