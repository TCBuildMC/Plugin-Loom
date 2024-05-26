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

package xyz.tcbuildmc.pluginloom.common.task.runtime

import org.gradle.api.DefaultTask
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import xyz.tcbuildmc.pluginloom.common.annotation.Todo

@Todo
class RunServerTask extends DefaultTask {
    @InputFile
    File serverJar

    @Input
    String maxMemory

    @InputFile
    @Optional
    File pluginJar = null

    @Input
    @Optional
    List<String> jvmArgs = new ArrayList<>()

    @Input
    @Optional
    List<String> classpath = new ArrayList<>()

    @InputFile
    @Optional
    @Deprecated
    File javaAgent = null

    @Input
    @Optional
    List<String> serverArgs = new ArrayList<>()

    @Input
    @Optional
    Boolean cleanPlugins = false

    @Input
    @Optional
    @Deprecated
    Boolean noGui = false

    @TaskAction
    void run() {
        List<String> cmd = new ArrayList<>()

        cmd.addAll("java", "-Xmx" + maxMemory)

        if (!jvmArgs.isEmpty()) {
            jvmArgs.forEach { arg ->
                cmd.addAll(arg)
            }
        }

        if (!classpath.isEmpty()) {
            cmd.add("-cp")

            def builder = new StringBuilder()
            classpath.forEach { cp ->
                builder.append(cp)
                builder.append(";")
            }

            def s = builder.toString()
            cmd.add(s.substring(0, s.length() - 1))
        }

        cmd.addAll("-jar", serverJar.absolutePath)

        if (!serverArgs.isEmpty()) {
            serverArgs.forEach { arg ->
                cmd.addAll(arg)
            }
        }

        if (cleanPlugins) {
            project.delete("${serverJar.getParentFile().absolutePath}/plugins")
        }

        if (pluginJar != null) {
            project.copy {
                from(pluginJar.absolutePath)
                into("${serverJar.getParentFile().absolutePath}/plugins")
                duplicatesStrategy DuplicatesStrategy.EXCLUDE
            }
        }

        project.configurations.getByName("serverRuntime").resolve().forEach { f ->
            if (f.name.endsWith(".jar")) {
                project.copy {
                    from(f.absolutePath)
                    into("${serverJar.getParentFile().absolutePath}/plugins")
                    duplicatesStrategy DuplicatesStrategy.EXCLUDE
                }
            }
        }

        project.exec {
            workingDir = serverJar.getParentFile()
            commandLine = cmd
            standardOutput = System.out
        }
    }
}
