buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.ktlint.gradle)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ktlint) apply false
}
