package com.whakaara.structure

import com.android.build.api.variant.AndroidComponentsExtension
import kotlinx.kover.gradle.plugin.dsl.KoverReportExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

private val coverageClassExclusions = listOf(
    "*_Provide*Factory",
    "*_Provide*Factory\$*",
    "*\$InstanceHolder",
    "*Activity",
    "*Activity\$*",
    "*.BuildConfig",
    "dagger.hilt.*",
    "hilt_aggregated_deps.*",
    "*.Hilt_*",
    "*_HiltModules_*",
    "*_Factory",
    "*_Factory_Impl",
    "*_Factory$*",
    "*_Module",
    "*_Module$*",
    "*Module_Provides*",
    "Dagger*Component*",
    "*ComposableSingletons$*",
    "*_AssistedFactory_Impl*",
    "*BuildConfig",
    "*_Impl",
    "*_Impl\$*"
)

private val coverageAnnotationExclusions = listOf(
    "*Generated*",
    "*HomeNavGraph*",
    "*Destination*",
    "*Composable*",
    "*Preview*",
    "dagger.hilt.android.AndroidEntryPoint",
    "dagger.hilt.android.HiltAndroidApp",
    "dagger.internal.DaggerGenerated",
    "dagger.Binds",
    "dagger.Module",
    "dagger.Provides",
    "javax.annotation.processing.Generated",
    "androidx.room.Database"
)

private val coveragePackageExclusions = listOf(
    "hilt_aggregated_deps",
    "dagger.hilt.internal.aggregatedroot.codegen",
    "com.app.whakaara.ui.theme"
)

internal fun Project.configureKover(
    androidComponentsExtension: AndroidComponentsExtension<*, *, *>
) {
    androidComponentsExtension.onVariants { variant ->
        configure<KoverReportExtension> {
            filters {
                androidReports(variant.name) {
                    filters {
                        excludes {
                            classes(coverageClassExclusions)
                            packages(coveragePackageExclusions)
                            annotatedBy(*coverageAnnotationExclusions.toTypedArray())
                        }
                    }
                }
            }
        }
    }
}
