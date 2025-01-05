import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

class AndroidLintPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jlleitschuh.gradle.ktlint")
            }

            extensions.configure<KtlintExtension> {
                android.set(true)
                ignoreFailures.set(true)
                debug.set(true)
                outputToConsole.set(true)
                reporters {
                    reporter(ReporterType.HTML)
                    reporter(ReporterType.PLAIN)
                    reporter(ReporterType.CHECKSTYLE)
                }
                filter {
                    exclude("**/generated/**")
                    include("**/kotlin/**")

                    exclude("**/app/src/test/java/com/app/whakaara/logic/**")
                }
            }
        }
    }
}
