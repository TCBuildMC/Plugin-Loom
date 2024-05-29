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

package xyz.tcbuildmc.pluginloom.spigot.nms.task.buildtools

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

class ReRunBuildToolsTask extends DefaultTask {
    @InputFile
    File buildToolsFile
    @Input
    String mcVersion
    @InputFile
    File workDir

    @TaskAction
    void run() {
        project.exec {
            workingDir = workDir
            commandLine = [
                    "java", "-jar", buildToolsFile.canonicalPath, "--rev", mcVersion, "--remapped"
            ]
            standardOutput = System.out
        }
    }
}
