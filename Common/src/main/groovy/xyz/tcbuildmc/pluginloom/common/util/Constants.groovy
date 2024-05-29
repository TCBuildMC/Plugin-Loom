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

package xyz.tcbuildmc.pluginloom.common.util

class Constants {
    static final boolean windows = System.getProperty("os.name").startsWith("Windows")
    static final String arch = System.getProperty("os.arch").endsWith("64") ? "64" : "32"

    static final String buildToolsURL = "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar"
    static final String getBukkitURL = "https://download.getbukkit.org/spigot" // >= 1.17
    static final String getBukkitLegacyURL = "https://cdn.getbukkit.org/spigot" // -SNAPSHOT-latest.jar (<= 1.11) < 1.17

    static final String TASK_GROUP = "PluginLoom"
}
