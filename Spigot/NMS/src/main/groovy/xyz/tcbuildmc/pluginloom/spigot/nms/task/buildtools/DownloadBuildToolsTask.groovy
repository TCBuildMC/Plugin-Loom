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

import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import xyz.tcbuildmc.pluginloom.common.util.Constants

class DownloadBuildToolsTask implements Runnable {
    private final Project project

    DownloadBuildToolsTask(Project project) {
        this.project = project
    }

    @Input
    @OutputFile
    private String buildToolsDir
    @Input
    @Deprecated
    private String workingDir
    @Input
    private int timeout

    String getBuildToolsDir() {
        return buildToolsDir
    }

    void setBuildToolsDir(String buildToolsDir) {
        this.buildToolsDir = buildToolsDir
    }

    @Deprecated
    String getWorkingDir() {
        return workingDir
    }

    @Deprecated
    void setWorkingDir(String workingDir) {
        this.workingDir = workingDir
    }

    int getTimeout() {
        return timeout / 1000
    }

    void setTimeout(int timeout) {
        this.timeout = timeout * 1000
    }

    @Override
    void run() {
        def buildToolsFile = new File(buildToolsDir, "BuildTools.jar")

        if (buildToolsFile.exists()) {
            return
        }

        FileUtils.copyURLToFile(new URI(Constants.buildToolsURL).toURL(), buildToolsFile, timeout, timeout)

        // TODO Download Mirror
        // I am so lazy to do this :(
//        if (ext.mirror) {
//            if (Constants.windows) {
//                def workingGitFile = new File(workingDir, Constants.gitFileName)
//                def buildToolsGitFile = new File(buildToolsDir, Constants.gitFileName)
//
//                project.logger.lifecycle("Downloading Git... (Windows only)")
//                FileUtils.copyURLToFile(new URI(Constants.gitURL).toURL(), workingGitFile, timeout, timeout)
//
//                // Copy workingDir to buildToolsDir
//                FileUtils.copyFile(workingGitFile, buildToolsGitFile)
//
//                CommandLineUtils.execute(buildToolsGitFile.canonicalPath, "-y", "-gm2", "-nr")
//            }
//
//            def workingMavenFile = new File(workingDir, Constants.mavenFile)
//            def buildToolsMavenFile = new File(buildToolsDir, Constants.mavenFile)
//
//            project.logger.lifecycle("Downloading Maven...")
//            FileUtils.copyURLToFile(new URI(Constants.mavenURL).toURL(), workingMavenFile, timeout, timeout)
//
//            // Copy workingDir to buildToolsDir
//            FileUtils.copyFile(workingMavenFile, buildToolsMavenFile)
//
//            NIOUtils.unzipFile(buildToolsMavenFile, new File("${buildToolsDir}/${Constants.mavenDir}"))
//        }
    }
}
