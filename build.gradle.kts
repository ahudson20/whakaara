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
    if (gitHooksDir.exists()) {
        from(File(rootProject.rootDir, "scripts/pre-commit"))
        into(gitHooksDir)
    }
    fileMode = "0777".toInt()
}
tasks.getByPath(":app:preBuild").dependsOn(tasks.getByName("installGitHook"))