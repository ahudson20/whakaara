import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.whakaara.structure.configureKover
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationKoverPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlinx.kover")
            }

            extensions.configure<ApplicationAndroidComponentsExtension> {
                configureKover(this)
            }
        }
    }
}
