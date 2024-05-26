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

package xyz.tcbuildmc.pluginloom.spigot.task.runtime

import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile

class RunBuildToolsForServerTask implements Runnable {
    private final Project project

    RunBuildToolsForServerTask(Project project) {
        this.project = project
    }

    @Input
    private File buildToolsFile
    @Input
    private String mcVersion
    @Input
    private File workDir

    @OutputFile
    private File workSpigotJar

    File getBuildToolsFile() {
        return buildToolsFile
    }

    void setBuildToolsFile(File buildToolsFile) {
        this.buildToolsFile = buildToolsFile
    }

    String getMcVersion() {
        return mcVersion
    }

    void setMcVersion(String mcVersion) {
        this.mcVersion = mcVersion
    }

    File getWorkDir() {
        return workDir
    }

    void setWorkDir(File workDir) {
        this.workDir = workDir
    }

    File getWorkSpigotJar() {
        return workSpigotJar
    }

    void setWorkSpigotJar(File workSpigotJar) {
        this.workSpigotJar = workSpigotJar
    }

    @Override
    void run() {
        workSpigotJar = new File(workDir, "spigot-${mcVersion}.jar")
        if (workSpigotJar.exists()) {
            return
        }

        project.exec {
            workingDir = workDir
            commandLine = [
                    "java", "-jar", buildToolsFile.canonicalPath, "--rev", mcVersion, "--remapped"
            ]
            standardOutput = System.out
        }
    }
}
