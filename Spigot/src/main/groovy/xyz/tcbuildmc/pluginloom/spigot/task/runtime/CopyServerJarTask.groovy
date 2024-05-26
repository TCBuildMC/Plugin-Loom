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

import org.apache.commons.io.FileUtils
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import xyz.tcbuildmc.pluginloom.common.annotation.Todo

@Todo
class CopyServerJarTask implements Runnable {
    @Input
    private File workSpigotJar
    @Input
    @OutputDirectory
    private String workDir

    File getWorkSpigotJar() {
        return workSpigotJar
    }

    void setWorkSpigotJar(File workSpigotJar) {
        this.workSpigotJar = workSpigotJar
    }

    String getWorkDir() {
        return workDir
    }

    void setWorkDir(String workDir) {
        this.workDir = workDir
    }

    @Override
    void run() {
        FileUtils.copyFile(workSpigotJar, new File(workDir, workSpigotJar.getName()))
    }
}
