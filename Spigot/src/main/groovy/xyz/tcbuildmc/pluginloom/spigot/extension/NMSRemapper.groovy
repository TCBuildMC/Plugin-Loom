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

package xyz.tcbuildmc.pluginloom.spigot.extension

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.AbstractArchiveTask

class NMSRemapper {
    private final Project project

    NMSRemapper(Project project) {
        this.project = project
    }

    AbstractArchiveTask inputJarTask = project.tasks.named(JavaPlugin.JAR_TASK_NAME, AbstractArchiveTask).get()

    String obfJarClassifier = "remapped-obf"

    String spigotMappingsJarClassifier = "remapped-spigot"

    String spigotApiVersion = ""
}
