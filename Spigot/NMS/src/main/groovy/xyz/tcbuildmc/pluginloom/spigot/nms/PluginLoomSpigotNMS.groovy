package xyz.tcbuildmc.pluginloom.spigot.nms

import org.gradle.api.Plugin
import org.gradle.api.Project
import xyz.tcbuildmc.pluginloom.common.util.ConditionUtils
import xyz.tcbuildmc.pluginloom.common.util.Constants
import xyz.tcbuildmc.pluginloom.common.util.NIOUtils
import xyz.tcbuildmc.pluginloom.spigot.PluginLoomSpigot
import xyz.tcbuildmc.pluginloom.spigot.PluginLoomSpigotExtension
import xyz.tcbuildmc.pluginloom.spigot.nms.task.buildtools.RunBuildToolsForNMSTask
import xyz.tcbuildmc.pluginloom.spigot.nms.task.nms.CopyArtifactsTask
import xyz.tcbuildmc.pluginloom.spigot.nms.task.remap.RemapJarTask
import xyz.tcbuildmc.pluginloom.spigot.task.buildtools.DownloadBuildToolsTask

class PluginLoomSpigotNMS implements Plugin<Project> {
    @Override
    void apply(Project project) {
        if (!project.plugins.hasPlugin(PluginLoomSpigot)) {
            throw new IllegalArgumentException()
        }

        def loomCache = "${project.projectDir.canonicalPath}/.gradle/pluginloom"

        def baseExt = project.extensions.getByType(PluginLoomSpigotExtension)
        def ext = project.extensions.create("pluginloomNMS", PluginLoomSpigotNMSExtension, project)

        def remapMojmapToObfTask = project.tasks.register("remapMojmapToObf", RemapJarTask) { tsk ->
            tsk.dependsOn(ext.inputJarTask)
            tsk.group = Constants.TASK_GROUP
            tsk.description = "Remaps the built project Mojmap jar to obf mappings."

            tsk.inputJar = ext.inputJarTask.archiveFile.get().asFile

            // ...
            tsk.outputJar = ext.inputJarTask.destinationDirectory.file(getRemappedJarName(ext, ext.obfJarClassifier)).get().asFile

            tsk.mappingsFile = new File("${loomCache}/repo/org/spigotmc/minecraft-server/${ConditionUtils.requiresNonNullOrEmpty(ext.spigotApiVersion)}/minecraft-server-${ConditionUtils.requiresNonNullOrEmpty(ext.spigotApiVersion)}-maps-mojang.txt")
            tsk.reverse = true
        }

        def remapObfToSpigotTask = project.tasks.register("remapObfToSpigot", RemapJarTask) { tsk ->
            tsk.group = Constants.TASK_GROUP
            tsk.description = "Remaps the built project obf jar to Spigot mappings."
            tsk.dependsOn(ext.inputJarTask)
            tsk.dependsOn(remapMojmapToObfTask.get())

            tsk.inputJar = remapMojmapToObfTask.get().outputJar
            tsk.outputJar = ext.inputJarTask.destinationDirectory.file(getRemappedJarName(ext, ext.spigotMappingsJarClassifier)).get().asFile

            tsk.mappingsFile = new File("${loomCache}/repo/org/spigotmc/minecraft-server/${ConditionUtils.requiresNonNullOrEmpty(ext.spigotApiVersion)}/minecraft-server-${ConditionUtils.requiresNonNullOrEmpty(ext.spigotApiVersion)}-maps-spigot.csrg")
        }

        def assemble = project.tasks.named("assemble").get()
        assemble.dependsOn(remapObfToSpigotTask)

        project.afterEvaluate {
            prepareNMS(project, baseExt, loomCache)
        }
    }

    void prepareNMS(final Project project, final PluginLoomSpigotExtension ext, final String loomCache) {
        project.logger.lifecycle("> :executing 3 steps to prepare Spigot NMS")

        def buildToolsDir = "${loomCache}/buildTools"

        project.logger.lifecycle("> :step 1 Download BuildTools")
        def task1 = new DownloadBuildToolsTask(project)
        task1.buildToolsDir = buildToolsDir
        task1.timeout = ext.base.timeout
        task1.run()

        def mavenLocalDir = "${NIOUtils.getMavenLocalDir()}/repository/org/spigotmc"

        project.logger.lifecycle("> :step 2 Run BuildTools")
        def task2 = new RunBuildToolsForNMSTask(project)
        task2.workDir = new File(buildToolsDir)
        task2.mcVersion = ConditionUtils.requiresNonNullOrEmpty(ext.base.mcVersion)
        task2.buildToolsFile = new File(buildToolsDir, "BuildTools.jar")
        task2.mavenDir = new File(mavenLocalDir)
        task2.run()

        def mavenCache = "${loomCache}/repo/org"

        project.logger.lifecycle("> :step 3 Copy Artifacts")
        def task3 = new CopyArtifactsTask()
        task3.mavenDir = new File(mavenLocalDir)
        task3.outputDir = new File(mavenCache)
        task3.run()

        project.logger.lifecycle("> :prepare Spigot NMS done")
    }

    private String getRemappedJarName(final PluginLoomSpigotNMSExtension ext, final String archivesClassifier) {
        return new StringBuilder()
                .append(ext.inputJarTask.archiveBaseName.getOrElse(""))
                .append("-")
//                .append(ext.inputJarTask.archiveAppendix.getOrElse(""))
//                .append("-")
                .append(ext.inputJarTask.archiveVersion.getOrElse(""))
                .append("-")
                .append(ConditionUtils.requiresNonNullOrEmpty(archivesClassifier))
                .append(".")
                .append(ext.inputJarTask.archiveExtension.getOrElse("jar"))
                .toString()
    }
}
