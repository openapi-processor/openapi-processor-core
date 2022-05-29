import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

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
        project.tasks.register ('generateVersion', VersionTask, new Action<VersionTask> () {
            @Override
            void execute (VersionTask task) {
                task.targetDir = project.buildDir
                task.version = project.version
            }
        })
    }
}
