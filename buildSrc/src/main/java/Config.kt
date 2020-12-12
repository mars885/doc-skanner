/*
 * Copyright 2020 Paul Rybitskyi, paul.rybitskyi.work@gmail.com
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

@file:Suppress("ClassName")

import org.gradle.api.JavaVersion


object appConfig {

    const val compileSdkVersion = 29
    const val targetSdkVersion = 29
    const val minSdkVersion = 21
    const val buildToolsVersion = "29.0.2"
    const val applicationId = "com.paulrybitskyi.docskanner"
    const val versionCode = 1
    const val versionName = "1.0.0"

    val javaCompatibilityVersion = JavaVersion.VERSION_1_8
    val kotlinCompatibilityVersion = JavaVersion.VERSION_1_8

}


object versions {

    const val kotlin = "1.4.21" // also in buildSrc build.gradle.kts file
    const val navigationVersion = "2.3.2"
    const val daggerHilt = "2.28.3-alpha"
    const val gradleVersionsPlugin = "0.36.0"

}


object deps {

    object plugins {

        private const val gradlePluginVersion = "4.1.1" // also in buildSrc build.gradle.kts file

        const val androidGradle = "com.android.tools.build:gradle:${gradlePluginVersion}"
        const val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${versions.kotlin}"
        const val navSafeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:${versions.navigationVersion}"
        const val daggerHiltGradle = "com.google.dagger:hilt-android-gradle-plugin:${versions.daggerHilt}"
        const val gradleVersions = "com.github.ben-manes:gradle-versions-plugin:${versions.gradleVersionsPlugin}"

    }

    object local {

        const val domain = ":domain"
        const val data = ":data"
        const val core = ":core"
        const val imageProcessing = ":image-processing"
        const val openCv = ":openCVLibrary341"

    }

    object kotlin {

        private const val kotlinCoroutinesCoreVersion = "1.4.2"

        const val stdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${versions.kotlin}"
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${kotlinCoroutinesCoreVersion}"

    }

    object androidX {

        private const val appCompatVersion = "1.2.0"
        private const val constraintLayoutVersion = "2.0.2"
        private const val recyclerViewVersion = "1.1.0"
        private const val lifecycleVersion = "2.2.0"
        private const val coreKtxVersion = "1.3.1"
        private const val fragmentKtxVersion = "1.2.5"
        private const val liveDataKtxVersion = "2.2.0"
        private const val daggerHiltAssistedInjectionVersion = "1.0.0-alpha02"

        const val appCompat = "androidx.appcompat:appcompat:${appCompatVersion}"
        const val navFragmentKtx = "androidx.navigation:navigation-fragment-ktx:${versions.navigationVersion}"
        const val navUiKtx = "androidx.navigation:navigation-ui-ktx:${versions.navigationVersion}"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:${constraintLayoutVersion}"
        const val recyclerView = "androidx.recyclerview:recyclerview:${recyclerViewVersion}"
        const val lifecycleCommonJava8 = "androidx.lifecycle:lifecycle-common-java8:${lifecycleVersion}"
        const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel:${lifecycleVersion}"
        const val coreKtx = "androidx.core:core-ktx:${coreKtxVersion}"
        const val fragmentKtx = "androidx.fragment:fragment-ktx:${fragmentKtxVersion}"
        const val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:${liveDataKtxVersion}"
        const val daggerHiltAssistedInjection = "androidx.hilt:hilt-lifecycle-viewmodel:${daggerHiltAssistedInjectionVersion}"
        const val daggerHiltAssistedInjectionCompiler = "androidx.hilt:hilt-compiler:${daggerHiltAssistedInjectionVersion}"

    }

    object google {

        private const val materialComponentsVersion = "1.3.0-alpha02"

        const val daggerHilt = "com.google.dagger:hilt-android:${versions.daggerHilt}"
        const val daggerHiltCompiler = "com.google.dagger:hilt-android-compiler:${versions.daggerHilt}"
        const val materialComponents = "com.google.android.material:material:${materialComponentsVersion}"

    }

    object square {

        private const val picassoVersion = "2.71828"

        const val picasso = "com.squareup.picasso:picasso:${picassoVersion}"

    }

    object commons {

        private const val commonsCoreVersion = "1.0.0"
        private const val commonsKtxVersion = "1.0.0"
        private const val commonsWidgetsVersion = "1.0.0"
        private const val commonsRecyclerViewVersion = "1.0.0"
        private const val commonsWindowAnimsVersion = "1.0.0"

        const val commonsCore = "com.paulrybitskyi.commons:commons-core:${commonsCoreVersion}"
        const val commonsKtx = "com.paulrybitskyi.commons:commons-ktx:${commonsKtxVersion}"
        const val commonsWidgets = "com.paulrybitskyi.commons:commons-widgets:${commonsWidgetsVersion}"
        const val commonsRecyclerView = "com.paulrybitskyi.commons:commons-recyclerview:${commonsRecyclerViewVersion}"
        const val commonsWindowAnims = "com.paulrybitskyi.commons:commons-window-anims:${commonsWindowAnimsVersion}"

    }

    object misc {

        private const val dexterVersion = "6.2.2"
        private const val materialDialogsVersion = "0.9.6.0"
        private const val pdfViewerVersion = "2.8.2"

        const val dexter = "com.karumi:dexter:${dexterVersion}"
        const val materialDialogs = "com.afollestad.material-dialogs:core:${materialDialogsVersion}"
        const val pdfViewer = "com.github.barteksc:android-pdf-viewer:${pdfViewerVersion}"

    }

    object testing {

        private const val jUnitVersion = "4.13.1"
        private const val jUnitExtVersion = "1.1.1"

        const val jUnit = "junit:junit:${jUnitVersion}"
        const val jUnitExt = "androidx.test.ext:junit:${jUnitExtVersion}"

    }

}