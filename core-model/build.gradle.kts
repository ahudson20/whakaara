import com.whakaara.structure.Modules
import com.whakaara.structure.modules

plugins {
    alias(libs.plugins.whakaara.android.library)
    alias(libs.plugins.whakaara.lint)
}

android {
    namespace = "com.whakaara.model"

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
    modules(Modules.coreCommon)
}
