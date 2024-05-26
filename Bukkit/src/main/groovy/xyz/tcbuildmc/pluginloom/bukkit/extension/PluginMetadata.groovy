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

package xyz.tcbuildmc.pluginloom.bukkit.extension

import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginExtension
import xyz.tcbuildmc.pluginloom.common.BaseExtension
import xyz.tcbuildmc.pluginloom.common.annotation.LoomTest

class PluginMetadata {
    private final Project project

    PluginMetadata(Project project) {
        this.project = project
    }

    String sourceSet = "main"

    Map<String, ?> metadata = [
            "name": project.extensions.getByType(BasePluginExtension).archivesName.getOrNull(),
            "version": project.version
    ]

    def name(String id) {
        metadata.put("name", id)
    }

    def version(String ver) {
        metadata.put("version", ver)
    }

    def main(String mainClass) {
        metadata.put("main", mainClass)
    }

    @LoomTest
    void test() {
        BaseExtension.submitTest { b ->
            if (b) {
                println metadata
            }
        }
    }

    boolean check() {
        def name = (metadata.containsKey("name") &&
                metadata.get("name") != null &&
                !((String) metadata.get("name")).isEmpty())

        def version = (metadata.containsKey("version") &&
                metadata.get("version") != null &&
                !((String) metadata.get("version")).isEmpty())
        def main = (metadata.containsKey("main") &&
                metadata.get("main") != null &&
                !((String) metadata.get("main")).isEmpty())

        return !(name && version && main)
    }
}
