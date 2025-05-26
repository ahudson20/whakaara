import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.kotlin.ktlint)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.kover.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("lintPlugin") {
            id = "whakaara.lint"
            implementationClass = "AndroidLintPlugin"
        }
        register("hiltPlugin") {
            id = "whakaara.hilt"
            implementationClass = "AndroidHiltPlugin"
        }
        register("hiltWorkPlugin") {
            id = "whakaara.hilt.work"
            implementationClass = "AndroidHiltWorkerPlugin"
        }
        register("roomPlugin") {
            id = "whakaara.room"
            implementationClass = "AndroidRoomPlugin"
        }
        register("androidApplicationPlugin") {
            id = "whakaara.android.application"
            implementationClass = "AndroidApplicationPlugin"
        }
        register("androidLibraryPlugin") {
            id = "whakaara.android.library"
            implementationClass = "AndroidLibraryPlugin"
        }
        register("androidLibraryCompose") {
            id = "whakaara.library.compose"
            implementationClass = "AndroidLibraryComposePlugin"
        }
        register("androidApplicationCompose") {
            id = "whakaara.application.compose"
            implementationClass = "AndroidApplicationComposePlugin"
        }
        register("androidApplicationKover") {
            id = "whakaara.application.kover"
            implementationClass = "AndroidApplicationKoverPlugin"
        }
        register("androidFeaturePlugin") {
            id = "whakaara.android.feature"
            implementationClass = "AndroidFeaturePlugin"
        }
    }
}
