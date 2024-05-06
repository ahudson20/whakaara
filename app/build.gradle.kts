import com.whakaara.structure.Modules
import com.whakaara.structure.modules

plugins {
    alias(libs.plugins.whakaara.android.application)
    alias(libs.plugins.whakaara.application.compose)
    alias(libs.plugins.whakaara.hilt)
    alias(libs.plugins.whakaara.lint)
}

android {
    namespace = "com.app.whakaara"

    defaultConfig {
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
            excludes.add("/META-INF/LICENSE.md")
            excludes.add("/META-INF/LICENSE-notice.md")
            excludes.add("META-INF/LICENSE")
            excludes.add("META-INF/*.properties")
            excludes.add("META-INF/AL2.0")
            excludes.add("META-INF/LGPL2.1")
        }
    }

    android.sourceSets.configureEach {
        kotlin.srcDir("src/$name/kotlin")
    }
}

dependencies {
    modules(Modules.coreCommon, Modules.coreData)
    testImplementation(project(":core-test"))

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Material
    implementation(libs.material)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.material)

    // Timepicker Material3
    implementation(libs.compose.material3.datetime.pickers)

    // Accompanist permissions
    implementation(libs.accompanist.permissions)

    // Kotlin + coroutines
    implementation(libs.androidx.work.runtime.ktx)

    // Not a processor, but forces Dagger to use newer metadata lib
    implementation(libs.jetbrains.kotlinx.metadata)

    implementation(libs.androidx.arch.core.common)
    implementation(libs.androidx.arch.core.runtime)
    implementation(libs.androidx.arch.core.testing)

    implementation(libs.jetbrains.kotlinx.coroutines.android)
    implementation(libs.jetbrains.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.jetbrains.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.junit.ext)
    androidTestImplementation(libs.androidx.compose.ui.test.junit)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.javax.inject)

    // https://github.com/dokar3/sheets
    implementation(libs.dokar3.bottomsheet)

    // https://github.com/ChargeMap/Compose-NumberPicker
    implementation(libs.chargemap.numberpicker)

    // https://github.com/alorma/Compose-Settings
    implementation(libs.compose.settings.m3)

    // https://github.com/cashapp/turbine
    testImplementation(libs.turbine)
    androidTestImplementation(libs.turbine)

    // Mockk
    testImplementation(libs.mockk)

    // Google
    implementation(libs.gson)

    // Hilt
    implementation(libs.androidx.hilt.nav.compose)
    androidTestImplementation(libs.dagger.hilt.testing.android)
    kspTest(libs.dagger.hilt.compiler.android)

    // Androidx Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.lifecycle.service)
    implementation(libs.androidx.lifecycle.process)
    testImplementation(libs.androidx.lifecycle.runtime.testing)

    // For AppWidgets support
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)

    // Splashscreen
    implementation(libs.androidx.core.splashscreen)

    // Lottie animation
    implementation(libs.lottie)
}
