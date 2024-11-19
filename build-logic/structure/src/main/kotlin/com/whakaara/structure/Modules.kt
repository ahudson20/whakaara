package com.whakaara.structure

import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.project

object Modules {
    const val app = ":app"
    const val coreCommon = ":core:common"
    const val coreData = ":core:data"
    const val coreDatabase = ":core:database"
    const val coreModel = ":core:model"
    const val coreTest = ":core:test"
    const val coreDesignSystem = ":core:designsystem"
    const val featureAlarm = ":feature:alarm"
    const val featureStopwatch = ":feature:stopwatch"
    const val featureTimer = ":feature:timer"
    const val featureOnboarding = ":feature:onboarding"
}

fun DependencyHandlerScope.modules(vararg module: String) {
    val modules = module.toList()
    modules.forEach {
        add("implementation", project(it))
    }
}
