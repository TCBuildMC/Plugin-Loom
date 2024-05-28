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

package xyz.tcbuildmc.pluginloom.spigot.nms.task.nms

import org.apache.commons.io.FileUtils
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile

class CopyArtifactsTask implements Runnable {
    @Input
    private File mavenDir

    @Input
    @OutputFile
    private File outputDir

    File getMavenDir() {
        return mavenDir
    }

    void setMavenDir(File mavenDir) {
        this.mavenDir = mavenDir
    }

    File getOutputDir() {
        return outputDir
    }

    void setOutputDir(File outputDir) {
        this.outputDir = outputDir
    }

    @Override
    void run() {
        FileUtils.copyDirectoryToDirectory(mavenDir, outputDir)
    }
}
