
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import com.whakaara.structure.DefaultConfig
import com.whakaara.structure.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = DefaultConfig.compileSdk
            }

            extensions.configure<LibraryAndroidComponentsExtension> {
                beforeVariants {
                    it.enableAndroidTest = it.enableAndroidTest &&
                        project.projectDir.resolve("src/androidTest").exists()
                }
            }
        }
    }
}
