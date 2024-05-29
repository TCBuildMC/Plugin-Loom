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
import org.gradle.api.tasks.OutputFile
import xyz.tcbuildmc.pluginloom.common.util.ConditionUtils
import xyz.tcbuildmc.pluginloom.common.util.Constants
import xyz.tcbuildmc.pluginloom.spigot.PluginLoomSpigotExtension

class DownloadServerJarTask implements Runnable {
    private final PluginLoomSpigotExtension ext

    DownloadServerJarTask(PluginLoomSpigotExtension ext) {
        this.ext = ext
    }

    @Input
    private String workingDir
    @OutputFile
    private File spigotServerJar

    String getWorkingDir() {
        return workingDir
    }

    void setWorkingDir(String workingDir) {
        this.workingDir = workingDir
    }

    File getSpigotServerJar() {
        return spigotServerJar
    }

    void setSpigotServerJar(File spigotServerJar) {
        this.spigotServerJar = spigotServerJar
    }

    @Override
    void run() {
        spigotServerJar = new File(workingDir, "spigot-${ConditionUtils.requiresNonNullOrEmpty(ext.base.mcVersion)}.jar")

        if (spigotServerJar.exists()) {
            return
        }

//        try {
//
//
//        } catch (Exception e1) {
//            try {
//                FileUtils.copyURLToFile(new URI("${Constants.getBukkitLegacyURL}/spigot-${ConditionUtils.requiresNonNullOrEmpty(ext.base.mcVersion)}.jar").toURL(),
//                        spigotServerJar,
//                        ext.base.timeout,
//                        ext.base.timeout)
//
//            } catch (Exception e2) {
//                try {
//                    FileUtils.copyURLToFile(new URI("${Constants.getBukkitLegacyURL}/spigot-${ConditionUtils.requiresNonNullOrEmpty(ext.base.spigotApiVersion)}-latest.jar").toURL(),
//                            spigotServerJar,
//                            ext.base.timeout,
//                            ext.base.timeout)
//
//                } catch (Exception e3) {
//                    try {
//                        FileUtils.copyURLToFile(new URI("${Constants.getBukkitLegacyURL}/spigot-${ConditionUtils.requiresNonNullOrEmpty(ext.base.spigotApiVersion)}-b${ConditionUtils.requiresNonNullOrEmpty(ext.base.buildId)}.jar").toURL(),
//                                spigotServerJar,
//                                ext.base.timeout,
//                                ext.base.timeout)
//                    } catch (Exception e4) {
//                        throw new IllegalArgumentException("Failed to download.")
//                    }
//                }
//            }
//        }

        FileUtils.copyURLToFile(new URI("${Constants.getBukkitURL}/spigot-${ConditionUtils.requiresNonNullOrEmpty(ext.base.mcVersion)}.jar").toURL(),
                spigotServerJar,
                ext.base.timeout,
                ext.base.timeout)
    }
}
