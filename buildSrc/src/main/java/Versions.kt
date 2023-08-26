import org.gradle.api.JavaVersion

/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

object Versions {
    const val COMPILE_SDK = 33
    const val WEAR_COMPILE_SDK = 33
    const val MIN_SDK = 26
    const val JVM_TARGET = "1.8"
    const val COMPOSE_COMPILER = "1.4.3"
    const val JACOCO_TOOL_VERSION = "0.8.10"
    val JAVA_VERSION = JavaVersion.VERSION_1_8
}