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

package xyz.tcbuildmc.pluginloom.common

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Delete
import xyz.tcbuildmc.pluginloom.common.util.Constants

class PluginLoomCommon {
    static void apply(Project project) {
        project.getPluginManager().apply(JavaPlugin)

        def serverRuntime = project.configurations.create("serverRuntime")

        def deleteCacheTask = project.tasks.register("deleteCache", Delete) { tsk ->
            tsk.group = Constants.TASK_GROUP
            tsk.description = "Delete PluginLoom caches."

            delete(project.file(".gradle/pluginloom"))
        }
    }
}
