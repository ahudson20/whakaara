import com.android.build.api.variant.LibraryAndroidComponentsExtension
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
            }

            extensions.configure<com.android.build.api.dsl.LibraryExtension> {
                configureKotlinAndroid(this)
                testOptions.targetSdk = DefaultConfig.compileSdk
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
