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

package xyz.tcbuildmc.pluginloom.bukkit

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import xyz.tcbuildmc.pluginloom.bukkit.extension.PluginMetadata
import xyz.tcbuildmc.pluginloom.bukkit.task.metadata.GenerateMetadataTask
import xyz.tcbuildmc.pluginloom.common.util.Constants

class PluginLoomBukkit {
    static void apply(Project project, PluginMetadata ext) {
        def bukkitLibrary = project.configurations.create("bukkitLibrary")

        def generatePluginMetadataTask = project.tasks.register("generatePluginMetadata", GenerateMetadataTask) { tsk ->
            tsk.group = Constants.TASK_GROUP
            tsk.description = "Generates the Plugin Metadata."

            tsk.configuration = ext
            tsk.sourceSet = ext.metadata.sourceSet
        }

        def processResources = project.tasks.named(JavaPlugin.PROCESS_RESOURCES_TASK_NAME).get()
        processResources.dependsOn(generatePluginMetadataTask)
    }
}
