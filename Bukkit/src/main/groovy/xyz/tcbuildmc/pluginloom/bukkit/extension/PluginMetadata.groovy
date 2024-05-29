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

    def supportFolia(boolean b) {
        metadata.put("folia-supported", b)
    }

    def description(String desc) {
        metadata.put("description", desc)
    }

    def author(String author) {
        metadata.put("author", author)
    }

    def authors(List<String> author) {
        metadata.put("authors", author)
    }

    def authors(String... author) {
        authors(author.collect().collect())
    }

    def contributors(List<String> contributor) {
        metadata.put("contributors", contributor)
    }

    def contributors(String... contributor) {
        contributors(contributor.collect().collect())
    }

    def website(String url) {
        metadata.put("website", url)
    }

    def website(URI url) {
        metadata.put("website", url.toURL().toString())
    }

    def website(URL url) {
        metadata.put("website", url.toString())
    }

    def apiVersion(String ver) {
        metadata.put("api-version", ver)
    }

    def load(LoadOrder order) {
        metadata.put("load", order.toString())
    }

    def prefix(String id) {
        metadata.put("prefix", id)
    }

    def defaultPermission(BasePermission permission) {
        metadata.put("default-permission", permission.toString())
    }

    def depend(List<String> pluginIds) {
        metadata.put("depend", pluginIds)
    }

    def depend(String... pluginIds) {
        depend(pluginIds.collect().collect())
    }

    def softDepend(List<String> pluginIds) {
        metadata.put("softdepend", pluginIds)
    }

    def softDepend(String... pluginIds) {
        softDepend(pluginIds.collect().collect())
    }

    def loadBefore(List<String> pluginIds) {
        metadata.put("loadbefore", pluginIds)
    }

    def loadBefore(String... pluginIds) {
        loadBefore(pluginIds.collect().collect())
    }

    def provided(List<String> pluginIds) {
        metadata.put("provides", pluginIds)
    }

    def provided(String... pluginIds) {
        provided(pluginIds.collect().collect())
    }

    // TODO Add permissions and commands

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
