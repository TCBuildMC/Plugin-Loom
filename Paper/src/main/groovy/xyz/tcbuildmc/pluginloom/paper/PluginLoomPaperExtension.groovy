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

package xyz.tcbuildmc.pluginloom.paper

import org.gradle.api.Action
import org.gradle.api.Project
import xyz.tcbuildmc.pluginloom.bukkit.extension.PluginMetadata
import xyz.tcbuildmc.pluginloom.common.BaseExtension
import xyz.tcbuildmc.pluginloom.common.annotation.Todo
import xyz.tcbuildmc.pluginloom.common.extension.RunServerExtension

class PluginLoomPaperExtension {
    private final Project project

    PluginLoomPaperExtension(Project project) {
        this.project = project
    }

    BaseExtension base = new BaseExtension()
    @Todo
    PluginMetadata metadata = new PluginMetadata(project)
    @Todo
    RunServerExtension runServer = new RunServerExtension(project)

    def base(Action<? super BaseExtension> action) {
        action.execute(base)
    }

    def metadata(Action<? super PluginMetadata> action) {
        action.execute(metadata)
    }

    def runServer(Action<? super RunServerExtension> action) {
        action.execute(runServer)
    }
}
