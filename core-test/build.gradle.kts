import com.whakaara.structure.Modules
import com.whakaara.structure.modules

plugins {
    alias(libs.plugins.whakaara.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.whakaara.lint)
}

android {
    namespace = "com.whakaara.test"
}

dependencies {
    modules(Modules.coreModel)

    implementation(libs.jetbrains.kotlinx.coroutines.android)
    implementation(libs.jetbrains.kotlinx.coroutines.test)
    implementation(libs.junit)
}
