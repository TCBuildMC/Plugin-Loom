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

package xyz.tcbuildmc.pluginloom.spigot.nms.task.remap

import net.md_5.specialsource.AccessMap
import net.md_5.specialsource.Jar
import net.md_5.specialsource.JarMapping
import net.md_5.specialsource.JarRemapper
import net.md_5.specialsource.RemapperProcessor
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class RemapJarTask extends DefaultTask {
    @InputFile
    File inputJar

    @OutputFile
    File outputJar

    @InputFile
    File mappingsFile

    @Input
    @Optional
    Boolean reverse = false

    @Input
    @Optional
    Boolean numericSrgNames = false

    @Input
    @Optional
    String inShadeRelocation = null

    @Input
    @Optional
    String outShadeRelocation = null

    @Input
    @Optional
    List<String> excludePackages = new ArrayList<>()

    @InputFile
    @Optional
    File ATFile = null

    @TaskAction
    void remap() {
        def mappings = new JarMapping()
        excludePackages.forEach { p ->
            mappings.addExcludedPackage(p)
        }

        def ATMap = new AccessMap()
        def ATProcessor = null
        if (ATFile != null) {
            ATMap.loadAccessTransformer(ATFile)
            ATProcessor = new RemapperProcessor(null, mappings, ATMap)
        }

        mappings.loadMappings(mappingsFile.absolutePath, reverse, numericSrgNames, inShadeRelocation, outShadeRelocation)

        def remapper = new JarRemapper(null, mappings, ATProcessor)
        def jar = Jar.init(inputJar)
        remapper.remapJar(jar, outputJar)
    }
}
