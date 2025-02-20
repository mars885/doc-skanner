/*
 * Copyright 2020 Paul Rybitskyi, oss@paulrybitskyi.com
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

import org.gradle.kotlin.dsl.version
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec


const val PLUGIN_GRADLE_VERSIONS = "com.github.ben-manes.versions"
const val PLUGIN_ANDROID_APPLICATION = "com.android.application"
const val PLUGIN_ANDROID_LIBRARY = "com.android.library"
const val PLUGIN_KOTLIN_ANDROID = "kotlin-android"
const val PLUGIN_KOTLIN_KAPT = "kotlin-kapt"
const val PLUGIN_NAV_SAFE_ARGS_KOTLIN = "androidx.navigation.safeargs.kotlin"
const val PLUGIN_DAGGER_HILT_ANDROID = "dagger.hilt.android.plugin"


fun PluginDependenciesSpec.gradleVersions(): PluginDependencySpec {
    return (id(PLUGIN_GRADLE_VERSIONS) version versions.gradleVersionsPlugin)
}


fun PluginDependenciesSpec.androidApplication(): PluginDependencySpec {
    return id(PLUGIN_ANDROID_APPLICATION)
}


fun PluginDependenciesSpec.androidLibrary(): PluginDependencySpec {
    return id(PLUGIN_ANDROID_LIBRARY)
}


fun PluginDependenciesSpec.kotlinAndroid(): PluginDependencySpec {
    return id(PLUGIN_KOTLIN_ANDROID)
}


fun PluginDependenciesSpec.kotlinKapt(): PluginDependencySpec {
    return id(PLUGIN_KOTLIN_KAPT)
}


fun PluginDependenciesSpec.navSafeArgsKotlin(): PluginDependencySpec {
    return id(PLUGIN_NAV_SAFE_ARGS_KOTLIN)
}


fun PluginDependenciesSpec.daggerHiltAndroid(): PluginDependencySpec {
    return id(PLUGIN_DAGGER_HILT_ANDROID)
}