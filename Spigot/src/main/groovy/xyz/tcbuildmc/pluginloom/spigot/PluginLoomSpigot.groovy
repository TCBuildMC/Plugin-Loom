/*
 * PluginLoom
 * Copyright (c) 2024 Tube Craft Server
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.tcbuildmc.pluginloom.spigot

import org.gradle.api.plugins.JavaPlugin
import xyz.tcbuildmc.pluginloom.bukkit.task.metadata.GenerateMetadataTask
import xyz.tcbuildmc.pluginloom.common.task.runtime.RunServerTask
import xyz.tcbuildmc.pluginloom.common.util.Constants
import xyz.tcbuildmc.pluginloom.spigot.task.buildtools.DownloadBuildToolsTask
import xyz.tcbuildmc.pluginloom.spigot.task.buildtools.ReRunBuildToolsTask
import xyz.tcbuildmc.pluginloom.spigot.task.remap.RemapJarTask
import xyz.tcbuildmc.pluginloom.spigot.task.runtime.CopyServerJarTask
import xyz.tcbuildmc.pluginloom.spigot.task.runtime.RunBuildToolsForServerTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import xyz.tcbuildmc.pluginloom.bukkit.PluginLoomBukkit
import xyz.tcbuildmc.pluginloom.common.PluginLoomCommon
import xyz.tcbuildmc.pluginloom.common.util.ConditionUtils
import xyz.tcbuildmc.pluginloom.common.util.NIOUtils
import xyz.tcbuildmc.pluginloom.spigot.task.nms.CopyArtifactsTask
import xyz.tcbuildmc.pluginloom.spigot.task.buildtools.RunBuildToolsForNMSTask

class PluginLoomSpigot implements Plugin<Project> {
    static final String VERSION = ConditionUtils.requiresNonNullOrElse(PluginLoomSpigot.class.getPackage().getImplementationVersion(), "0.0-unknown")

    @Override
    void apply(Project project) {
        def loomCache = "${project.projectDir.canonicalPath}/.gradle/pluginloom"
        declareRepositories(project.repositories, new File("${loomCache}/repo/"))

        project.logger.lifecycle("Plugin Loom: ${VERSION} on Spigot platform")

        PluginLoomCommon.apply(project)
        PluginLoomBukkit.apply(project)

        def ext = project.extensions.create("pluginloom", PluginLoomSpigotExtension, project, loomCache)

        def buildToolsDir = "${loomCache}/buildTools"

        def reRunBuildToolsTask = project.tasks.register("reRunBuildTools", ReRunBuildToolsTask) { tsk ->
            tsk.group = Constants.TASK_GROUP
            tsk.description = "Re-runs `BuildTools.jar`."

            tsk.buildToolsFile = new File(buildToolsDir, "BuildTools.jar")
            tsk.mcVersion = ConditionUtils.requiresNonNullOrEmpty(ext.base.mcVersion)
            tsk.workDir = new File(buildToolsDir)
        }

        def remapMojmapToObfTask = project.tasks.register("remapMojmapToObf", RemapJarTask) { tsk ->
            tsk.dependsOn(ext.nmsRemap.inputJarTask)
            tsk.group = Constants.TASK_GROUP
            tsk.description = "Remaps the built project Mojmap jar to obf mappings."

            tsk.inputJar = ext.nmsRemap.inputJarTask.archiveFile.get().asFile

            // ...
            tsk.outputJar = ext.nmsRemap.inputJarTask.destinationDirectory.file(getRemappedJarName(ext, ext.nmsRemap.obfJarClassifier)).get().asFile

            tsk.mappingsFile = new File("${loomCache}/repo/org/spigotmc/minecraft-server/${ConditionUtils.requiresNonNullOrEmpty(ext.nmsRemap.spigotApiVersion)}/minecraft-server-${ConditionUtils.requiresNonNullOrEmpty(ext.nmsRemap.spigotApiVersion)}-maps-mojang.txt")
            tsk.reverse = true
        }

        def remapObfToSpigotTask = project.tasks.register("remapObfToSpigot", RemapJarTask) { tsk ->
            tsk.group = Constants.TASK_GROUP
            tsk.description = "Remaps the built project obf jar to Spigot mappings."
            tsk.dependsOn(ext.nmsRemap.inputJarTask)
            tsk.dependsOn(remapMojmapToObfTask.get())

            tsk.inputJar = remapMojmapToObfTask.get().outputJar
            tsk.outputJar = ext.nmsRemap.inputJarTask.destinationDirectory.file(getRemappedJarName(ext, ext.nmsRemap.spigotMappingsJarClassifier)).get().asFile

            tsk.mappingsFile = new File("${loomCache}/repo/org/spigotmc/minecraft-server/${ConditionUtils.requiresNonNullOrEmpty(ext.nmsRemap.spigotApiVersion)}/minecraft-server-${ConditionUtils.requiresNonNullOrEmpty(ext.nmsRemap.spigotApiVersion)}-maps-spigot.csrg")
        }

        def assemble = project.tasks.named("assemble").get()
        assemble.dependsOn(remapObfToSpigotTask)

        def generatePluginMetadataTask = project.tasks.register("generatePluginMetadata", GenerateMetadataTask) { tsk ->
            tsk.group = Constants.TASK_GROUP
            tsk.description = "Generates the Plugin Metadata."

            tsk.configuration = ext.metadata
            tsk.sourceSet = ext.metadata.sourceSet
        }

        def processResources = project.tasks.named(JavaPlugin.PROCESS_RESOURCES_TASK_NAME).get()
        processResources.dependsOn(generatePluginMetadataTask)

        def workDir = "${loomCache}/working/spigot"

        def runServerTask = project.tasks.register("runServer", RunServerTask) { tsk ->
            tsk.group = Constants.TASK_GROUP
            tsk.description = "Runs development environment test Spigot Sever."

            tsk.serverJar = new File(workDir, "spigot-${ConditionUtils.requiresNonNullOrEmpty(ext.base.mcVersion)}.jar")
            tsk.maxMemory = ConditionUtils.requiresNonNullOrEmpty(ext.runServer.maxMemory)
        }

        def runServerWithPluginTask = project.tasks.register("runServerWithPlugin", RunServerTask) { tsk ->
            tsk.group = Constants.TASK_GROUP
            tsk.description = "Runs development environment test Spigot Sever with generated Plugin(s)."
            tsk.dependsOn(ext.runServer.inputJarTask)

            tsk.serverJar = new File(workDir, "spigot-${ConditionUtils.requiresNonNullOrEmpty(ext.base.mcVersion)}.jar")
            tsk.maxMemory = ConditionUtils.requiresNonNullOrEmpty(ext.runServer.maxMemory)
            tsk.pluginJar = ext.runServer.inputJarTask.archiveFile.get().asFile
        }

        project.afterEvaluate {
            prepareNMS(project, ext, loomCache)

            prepareRunServer(project, ext, loomCache)
        }
    }

    void declareRepositories(final RepositoryHandler repositories, final File loomRepository) {
        repositories.maven {
            name = "sonatype"
            url = "https://oss.sonatype.org/content/repositories/snapshots/"
        }

        repositories.maven {
            name = "jitpack"
            url = "https://jitpack.io/"
        }

        repositories.maven {
            name = "minecraft"
            url = "https://libraries.minecraft.net/"
        }

        repositories.maven {
            name = "spigotmc"
            url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/"
        }

        repositories.maven {
            name = "PluginLoom Local Cache Repository"
            url = loomRepository

            metadataSources {
                mavenPom()
                artifact()
            }
        }

        repositories.maven {
            name = "aliyun"
            url = "https://maven.aliyun.com/repository/public/"
        }

        repositories.mavenCentral()
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

    void prepareRunServer(final Project project, final PluginLoomSpigotExtension ext, final String loomCache) {
        project.logger.lifecycle("> :executing 3 steps to prepare Spigot RunServer")

        def buildToolsDir = "${loomCache}/buildTools"

        project.logger.lifecycle("> :step 1 Download BuildTools")
        def task1 = new DownloadBuildToolsTask(project)
        task1.buildToolsDir = buildToolsDir
        task1.timeout = ext.base.timeout
        task1.run()

        project.logger.lifecycle("> :step 2 Run BuildTools")
        def task2 = new RunBuildToolsForServerTask(project)
        task2.workDir = new File(buildToolsDir)
        task2.mcVersion = ConditionUtils.requiresNonNullOrEmpty(ext.base.mcVersion)
        task2.buildToolsFile = new File(buildToolsDir, "BuildTools.jar")
        task2.run()

        def workDir = "${loomCache}/working/spigot"

        project.logger.lifecycle("> :step 3 Copy Spigot Server jar")
        def task3 = new CopyServerJarTask()
        task3.workSpigotJar = task2.workSpigotJar
        task3.workDir = workDir
        task3.run()

        project.logger.lifecycle("> :prepare Spigot RunServer done")
    }

    private String getRemappedJarName(final PluginLoomSpigotExtension ext, final String archivesClassifier) {
        return new StringBuilder()
                .append(ext.nmsRemap.inputJarTask.archiveBaseName.getOrElse(""))
                .append("-")
//                .append(ext.nmsRemap.inputJarTask.archiveAppendix.getOrElse(""))
//                .append("-")
                .append(ext.nmsRemap.inputJarTask.archiveVersion.getOrElse(""))
                .append("-")
                .append(ConditionUtils.requiresNonNullOrEmpty(archivesClassifier))
                .append(".")
                .append(ext.nmsRemap.inputJarTask.archiveExtension.getOrElse("jar"))
                .toString()
    }
}
