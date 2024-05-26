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

class CommandLineUtils {
    static int execute(String filePath, String... args) {
        return execute((List<String>) args.collect())
    }

    static int execute(String... command) {
        return execute((List<String>) command.collect())
    }

    static int execute(List<String> command) {
        Process proc = new ProcessBuilder(command).start()

        final def r = new BufferedReader(new InputStreamReader(proc.inputStream))

        String line = null
        while ((line = r.readLine()) != null) {
            println line
        }

        r.close()
        return proc.exitValue()
    }
}
