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

package xyz.tcbuildmc.pluginloom.bukkit.task.metadata

import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import xyz.tcbuildmc.pluginloom.bukkit.extension.PluginMetadata
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

class GenerateMetadataTask extends DefaultTask {
    @Input
    PluginMetadata configuration

    @Input
    @Optional
    String sourceSet

    @TaskAction
    void parse() {
        if (configuration.check()) {
            throw new IllegalArgumentException()
        }

        def pluginYML = new File("${project.projectDir.canonicalPath}/src/${sourceSet}/resources", "plugin.yml")
        def metadata = configuration.metadata

        List<String> libraries = new ArrayList<>()

        project.configurations.getByName("bukkitLibrary").dependencies.forEach { d -> // allDependencies
            libraries.add(d.group + ":" + d.name + ":" + d.version)
        }

        if (!libraries.isEmpty()) {
            metadata.put("libraries", libraries)
        }

        if (!pluginYML.getParentFile().exists()) {
            pluginYML.getParentFile().mkdirs()
        }

        if (!pluginYML.exists()) {
            pluginYML.createNewFile()
        }

        def mapper = YAMLMapper.builder()
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                .build()
        mapper.writer().withDefaultPrettyPrinter().writeValue(pluginYML, metadata)
    }
}
