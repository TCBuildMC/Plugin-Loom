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

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils

class NIOUtils {
    static void unzipFile(String zip, String dest) {
        unzipFile(new File(zip), dest)
    }

    static void unzipFile(File zip, String dest) {
        unzipFile(zip, new File(dest))
    }

    static void unzipFile(File zip, File dest) {
        if (!checkFileEndsWith(zip, "zip")) {
            throw new IllegalArgumentException()
        }

        ZipArchiveEntry entry
        ZipArchiveInputStream zis = new ZipArchiveInputStream(zip.newInputStream())
        while ((entry = zis.getNextEntry()) != null) {
            File file = new File(dest, entry.getName())

            if (entry.isDirectory()) {
                file.mkdirs()
            } else {
                BufferedOutputStream bfos = new BufferedOutputStream(FileUtils.openOutputStream(file))
                IOUtils.copy(zis, bfos, 8192)

                IOUtils.closeQuietly(bfos)
            }
        }

        IOUtils.closeQuietly(zis)
    }

    static boolean checkFileEndsWith(File file, String endSuffix) {
        return file.getName().endsWith(".${endSuffix}")
    }

    static String getMavenLocalDir() {
        return (System.getProperty("pluginloom.mavenLocalPath") != null) ?: "${FileUtils.getUserDirectoryPath()}/.m2"
    }

    static File getMavenLocalDirFile() {
        return new File(getMavenLocalDir())
    }
}
