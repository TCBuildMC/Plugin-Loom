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

import xyz.tcbuildmc.pluginloom.common.task.runtime.RunServerTask
import xyz.tcbuildmc.pluginloom.common.util.Constants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import xyz.tcbuildmc.pluginloom.bukkit.PluginLoomBukkit
import xyz.tcbuildmc.pluginloom.common.PluginLoomCommon
import xyz.tcbuildmc.pluginloom.common.util.ConditionUtils
import xyz.tcbuildmc.pluginloom.spigot.task.runtime.DownloadServerJarTask

class PluginLoomSpigot implements Plugin<Project> {
    static final String VERSION = ConditionUtils.requiresNonNullOrElse(PluginLoomSpigot.class.getPackage().getImplementationVersion(), "0.0-unknown")

    @Override
    void apply(Project project) {
        def loomCache = "${project.projectDir.canonicalPath}/.gradle/pluginloom"
        declareRepositories(project.repositories, new File("${loomCache}/repo/"))

        project.logger.lifecycle("Plugin Loom: ${VERSION} on Spigot platform")

        def ext = project.extensions.create("pluginloom", PluginLoomSpigotExtension, project)

        PluginLoomCommon.apply(project)
        PluginLoomBukkit.apply(project, ext.metadata)

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

        if (project.plugins.hasPlugin("xyz.tcbuildmc.pluginloom.spigot.nms")) {
            runServerWithPluginTask.get().dependsOn(project.tasks.named("remapObfToSpigot"))
        }

        project.afterEvaluate {
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

    void prepareRunServer(final Project project, final PluginLoomSpigotExtension ext, final String loomCache) {
        project.logger.lifecycle("> :executing 1 steps to prepare Spigot RunServer")

        def workDir = "${loomCache}/working/spigot"

        project.logger.lifecycle("> :step 1 Download Spigot Server jar")
        def task1 = new DownloadServerJarTask(ext)
        task1.workingDir = workDir
        task1.run()

        project.logger.lifecycle("> :prepare Spigot RunServer done")
    }
}
