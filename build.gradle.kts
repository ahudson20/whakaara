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

allprojects {
    tasks.register("copyGitHooks", Copy::class) {
        description = "Copies the git hooks from /git-hooks to the .git folder."
        group = "git hooks"
        from("$rootDir/scripts/pre-commit")
        into("$rootDir/.git/hooks/")
//        fileMode = 0b111101101
    }

    tasks.register("installGitHooks", Exec::class) {
        description = "Installs the pre-commit git hooks from /git-hooks."
        group = "git hooks"
        workingDir = rootDir
        commandLine("chmod", "-R", "+x", ".git/hooks/")
        dependsOn("copyGitHooks")
        doLast {
            logger.info("Git hook installed successfully.")
        }
    }
    afterEvaluate {
        tasks.findByName("preBuild")?.dependsOn("installGitHooks")
    }
}