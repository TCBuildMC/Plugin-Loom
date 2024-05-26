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

class ConditionUtils {
    static <T> T requiresNonNull(T value) {
        if (value == null) {
            throw new NullPointerException()
        }

        return value
    }

    static <T> T requiresNonNullOrElse(T value, T orElse) {
        if (value == null) {
            if (orElse == null) {
                throw new NullPointerException()
            }

            return orElse
        }

        return value
    }

    static <T extends CharSequence> T requiresNonNullOrEmpty(T value) {
        requiresNonNull(value)

        if (value.length() == 0) {
            throw new IllegalArgumentException()
        }

        return value
    }
}
