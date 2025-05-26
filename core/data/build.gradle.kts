import com.whakaara.structure.Modules

plugins {
    alias(libs.plugins.whakaara.android.library)
    alias(libs.plugins.whakaara.hilt)
    alias(libs.plugins.whakaara.lint)
    alias(libs.plugins.kotlinx.kover)
}

android {
    namespace = "com.whakaara.core.data"

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    api(project(Modules.coreDatabase))
    api(project(Modules.coreModel))

    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.junit)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
    testImplementation(libs.jetbrains.kotlinx.coroutines.test)
}
