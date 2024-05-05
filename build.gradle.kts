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

val gitHooksDir = File(rootProject.rootDir, ".git/hooks")
tasks.register("installGitHook", Copy::class) {
    from(File(rootProject.rootDir, "scripts/pre-commit"))
    into(File(rootProject.rootDir, ".git/hooks"))
    eachFile {
        fileMode = 0b111101101
    }
}
tasks.getByPath(":app:preBuild").dependsOn(tasks.getByName("installGitHook"))