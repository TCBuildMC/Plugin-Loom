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

import org.junit.jupiter.api.Test

class ExecTest {
    @Test
    void execute() {
        Process proc = new ProcessBuilder("cmd.exe", "/C").start()

        BufferedReader r = new BufferedReader(new InputStreamReader(proc.getInputStream()))

        String line = null
        while ((line = r.readLine()) != null) {
            println line
        }

        proc.destroy()
        r.close()

        def list = Arrays.asList("cmd.exe", "/C")

        println list
    }
}
