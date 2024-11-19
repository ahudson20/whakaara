pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.6.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:structure:testClasses"))

rootProject.name = "whakaara"
include(
    ":app",
    ":core:common",
    ":core:database",
    ":core:data",
    ":core:model",
    ":core:test",
    ":feature:alarm",
    ":feature:stopwatch",
    ":feature:timer",
    ":feature:onboarding"
)
include(":core:designsystem")
