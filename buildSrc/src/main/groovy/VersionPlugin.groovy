import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.jvm.tasks.Jar

/**
 * provides a "generateVersion" task to a create a simple Version.java class:
 *
 * <pre>{@code
 * package io.openapiprocessor.core;
 *
 * public class Version {
 *     public static final String version = "${project.version}";
 * }
 * }</pre>
 *
 *
 * The io/openapiprocessor/core/Version.java file is generated to:
 *
 * $(project.buildDir}/main/java
 *
 * Add it as a source directory to include it in compilation.
 */
class VersionPlugin implements Plugin<Project> {

    void apply (Project project) {
        def generateVersion = project.tasks.register (
            'generateVersion',
            VersionTask,
            new Action<VersionTask> () {
                @Override
                void execute (VersionTask task) {
                    task.targetDir = project.buildDir
                    task.version = project.version
                }
            })

        project.plugins.withType (JavaPlugin) {
            def sourceSets = project.extensions.getByType (SourceSetContainer)
            def main = sourceSets.getByName (SourceSet.MAIN_SOURCE_SET_NAME)
            main.java.setSrcDirs (["${project.buildDir}/version"])
        }

        project.tasks.withType (AbstractCompile).configureEach {
            dependsOn (generateVersion)
        }

        // sourcesJar
        project.tasks.withType (Jar).configureEach {
            dependsOn (generateVersion)
        }
    }
}
