import com.android.build.gradle.LibraryExtension
import com.whakaara.structure.DefaultConfig
import com.whakaara.structure.configureAndroidCompose
import com.whakaara.structure.configureKotlinAndroid
import com.whakaara.structure.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            dependencies {
                add("implementation", project(":core:data"))
                add("implementation", libs.findLibrary("androidx.lifecycle.viewmodel.compose").get())
            }

            extensions.configure<LibraryExtension> {
                configureAndroidCompose(this)
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = DefaultConfig.compileSdk
            }
        }
    }
}
