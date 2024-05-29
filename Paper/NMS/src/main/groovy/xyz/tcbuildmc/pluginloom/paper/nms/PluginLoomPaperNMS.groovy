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

package xyz.tcbuildmc.pluginloom.paper.nms

import org.gradle.api.Plugin
import org.gradle.api.Project
import xyz.tcbuildmc.pluginloom.paper.PluginLoomPaper
import xyz.tcbuildmc.pluginloom.paper.PluginLoomPaperExtension

class PluginLoomPaperNMS implements Plugin<Project> {
    @Override
    void apply(Project project) {
        if (!project.plugins.hasPlugin(PluginLoomPaper)) {
            throw new IllegalArgumentException()
        }

        def loomCache = "${project.projectDir.canonicalPath}/.gradle/pluginloom"

        def baseExt = project.extensions.getByType(PluginLoomPaperExtension)
        def ext = project.extensions.create("pluginloomNMS", PluginLoomPaperNMSExtension, project)

        project.afterEvaluate {
            prepareNMS(project, baseExt, loomCache)
        }
    }

    void prepareNMS(final Project project, final PluginLoomPaperExtension ext, final String loomCache) {

    }
}
