import com.whakaara.structure.getLibrary
import com.whakaara.structure.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidHiltWorkerPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("whakaara.hilt")
            }

            dependencies {
                add("implementation", libs.getLibrary("androidx.work.runtime.ktx"))
            }
        }
    }
}
