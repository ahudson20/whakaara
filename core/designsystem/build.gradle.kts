import com.whakaara.structure.Modules
import com.whakaara.structure.modules

plugins {
    alias(libs.plugins.whakaara.android.library)
    alias(libs.plugins.whakaara.library.compose)
}

android {
    namespace = "com.whakaara.core.designsystem"
}

dependencies {
    modules(Modules.coreCommon, Modules.coreModel)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.test.junit.ext)
    androidTestImplementation(libs.androidx.espresso.core)
}
