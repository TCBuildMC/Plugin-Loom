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

    PluginLoomSpigotExtension(Project project, String loomCache) {
        this.project = project
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
    }

    def nmsRemap(Action<? super NMSRemapper> action) {
        action.execute(nmsRemap)
    }

    def metadata(Action<? super PluginMetadata> action) {
        action.execute(metadata)
    }

    def runServer(Action<? super RunServerExtension> action) {
        action.execute(runServer)
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
}
