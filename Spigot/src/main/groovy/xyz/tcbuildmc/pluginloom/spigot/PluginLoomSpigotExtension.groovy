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

import xyz.tcbuildmc.pluginloom.spigot.extension.NMSGenerator
import xyz.tcbuildmc.pluginloom.spigot.task.buildtools.ReRunBuildToolsTask
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.plugins.JavaPlugin
import xyz.tcbuildmc.pluginloom.bukkit.task.metadata.GenerateMetadataTask
import xyz.tcbuildmc.pluginloom.common.BaseExtension
import xyz.tcbuildmc.pluginloom.common.annotation.Todo
import xyz.tcbuildmc.pluginloom.common.extension.RunServerExtension
import xyz.tcbuildmc.pluginloom.common.task.runtime.RunServerTask
import xyz.tcbuildmc.pluginloom.common.util.ConditionUtils
import xyz.tcbuildmc.pluginloom.common.util.Constants
import xyz.tcbuildmc.pluginloom.common.util.GradleUtils
import xyz.tcbuildmc.pluginloom.spigot.extension.NMSRemapper
import xyz.tcbuildmc.pluginloom.bukkit.extension.PluginMetadata
import xyz.tcbuildmc.pluginloom.spigot.task.remap.RemapJarTask

@SuppressWarnings("unused")
class PluginLoomSpigotExtension {
    private final Project project
    private final String loomCache
    private final PluginLoomSpigotExtension ext

    PluginLoomSpigotExtension(Project project, String loomCache) {
        this.project = project
        this.loomCache = loomCache
        this.ext = project.extensions.getByType(PluginLoomSpigotExtension)
    }

    BaseExtension base = new BaseExtension()
    NMSGenerator nmsGen = new NMSGenerator(project)
    NMSRemapper nmsRemap = new NMSRemapper(project)
    @Todo
    PluginMetadata metadata = new PluginMetadata(project)
    @Todo
    RunServerExtension runServer = new RunServerExtension(project)

    def base(Action<? super BaseExtension> action) {
        action.execute(base)
    }

    def nmsGen(Action<? super NMSGenerator> action) {
        action.execute(nmsGen)

        GradleUtils.submitAfterProjectEvaluate(project) { p ->
            PluginLoomSpigot.prepareNMS(p, p.extensions.getByType(PluginLoomSpigotExtension), loomCache)
        }

        def buildToolsDir = "${loomCache}/buildTools"

        def reRunBuildToolsTask = project.tasks.register("reRunBuildTools", ReRunBuildToolsTask) { tsk ->
            tsk.group = Constants.TASK_GROUP
            tsk.description = "Re-runs `BuildTools.jar`."

            tsk.buildToolsFile = new File(buildToolsDir, "BuildTools.jar")
            tsk.mcVersion = ConditionUtils.requiresNonNullOrEmpty(ext.base.mcVersion)
            tsk.workDir = new File(buildToolsDir)
        }
    }

    def nmsRemap(Action<? super NMSRemapper> action) {
        action.execute(nmsRemap)

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
    }

    def metadata(Action<? super PluginMetadata> action) {
        action.execute(metadata)

        def generatePluginMetadataTask = project.tasks.register("generatePluginMetadata", GenerateMetadataTask) { tsk ->
            tsk.group = Constants.TASK_GROUP
            tsk.description = "Generates the Plugin Metadata."

            tsk.configuration = ext.metadata
            tsk.sourceSet = ext.metadata.sourceSet
        }

        def processResources = project.tasks.named(JavaPlugin.PROCESS_RESOURCES_TASK_NAME).get()
        processResources.dependsOn(generatePluginMetadataTask)
    }

    def runServer(Action<? super RunServerExtension> action) {
        action.execute(runServer)

        GradleUtils.submitAfterProjectEvaluate(project) { p ->
            PluginLoomSpigot.prepareRunServer(p, p.extensions.getByType(PluginLoomSpigotExtension), loomCache)
        }

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
    }

    Dependency spigotApi(String version) {
        return project.dependencies.create("org.spigotmc:spigot-api:${version}")
    }

    Dependency spigotNMS(String version) {
        return project.dependencies.create("org.spigotmc:spigot:${version}")
    }

    Dependency spigotNMS(String version, String mapping) {
        return project.dependencies.create("org.spigotmc:spigot:${version}:remapped-${mapping}")
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
