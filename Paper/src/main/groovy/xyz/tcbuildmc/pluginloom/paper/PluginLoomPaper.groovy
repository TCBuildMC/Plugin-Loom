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

package xyz.tcbuildmc.pluginloom.paper

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import xyz.tcbuildmc.pluginloom.bukkit.PluginLoomBukkit
import xyz.tcbuildmc.pluginloom.common.PluginLoomCommon
import xyz.tcbuildmc.pluginloom.common.util.ConditionUtils

class PluginLoomPaper implements Plugin<Project> {
    static final String VERSION = ConditionUtils.requiresNonNullOrElse(PluginLoomPaper.class.getPackage().getImplementationVersion(), "0.0-unknown")

    @Override
    void apply(Project project) {
        def loomCache = "${project.projectDir.toPath().toAbsolutePath().toString()}/.gradle/pluginloom"
        declareRepositories(project.repositories, new File("${loomCache}/repo/"))

        project.logger.lifecycle("Plugin Loom: ${VERSION} on Paper platform")

        def ext = project.extensions.create("pluginloom", PluginLoomPaperExtension, project)

        PluginLoomCommon.apply(project)
        PluginLoomBukkit.apply(project, ext.metadata)
    }

    void declareRepositories(final RepositoryHandler repositories, final File loomRepository) {
        repositories.maven {
            name = "sonatype"
            url = "https://oss.sonatype.org/content/repositories/snapshots/"
        }

        repositories.maven {
            name = "jitpack"
            url = "https://jitpack.io/"
        }

        repositories.maven {
            name = "minecraft"
            url = "https://libraries.minecraft.net/"
        }

        repositories.maven {
            name = "papermc"
            url = "https://repo.papermc.io/maven-public/"
        }

        repositories.maven {
            name = "PluginLoom Local Cache Repository"
            url = loomRepository

            metadataSources {
                mavenPom()
                artifact()
            }
        }

        repositories.maven {
            name = "aliyun"
            url = "https://maven.aliyun.com/repository/public/"
        }

        repositories.mavenCentral()
    }
}
