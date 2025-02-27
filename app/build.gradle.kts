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

plugins {
    androidApplication()
    kotlinAndroid()
    kotlinKapt()
    navSafeArgsKotlin()
    daggerHiltAndroid()
}

android {
    compileSdkVersion(appConfig.compileSdkVersion)
    buildToolsVersion(appConfig.buildToolsVersion)

    defaultConfig {
        minSdkVersion(appConfig.minSdkVersion)
        targetSdkVersion(appConfig.targetSdkVersion)
        versionCode = appConfig.versionCode
        versionName = appConfig.versionName
        applicationId = appConfig.applicationId

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
            isUniversalApk = false
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = appConfig.javaCompatibilityVersion
        targetCompatibility = appConfig.javaCompatibilityVersion
    }
}

dependencies {
    implementation(project(deps.local.domain))
    implementation(project(deps.local.data))
    implementation(project(deps.local.core))
    implementation(project(deps.local.imageLoading))
    implementation(project(deps.local.imageProcessing))

    implementation(deps.kotlin.stdLib)
    implementation(deps.kotlin.coroutinesCore)

    implementation(deps.androidX.appCompat)
    implementation(deps.androidX.navFragmentKtx)
    implementation(deps.androidX.navUiKtx)
    implementation(deps.androidX.constraintLayout)
    implementation(deps.androidX.recyclerView)
    implementation(deps.androidX.lifecycleCommonJava8)
    implementation(deps.androidX.lifecycleViewModel)
    implementation(deps.androidX.coreKtx)
    implementation(deps.androidX.fragmentKtx)
    implementation(deps.androidX.liveDataKtx)

    implementation(deps.google.materialComponents)

    implementation(deps.square.picasso)

    implementation(deps.commons.commonsCore)
    implementation(deps.commons.commonsKtx)
    implementation(deps.commons.commonsNavigation)
    implementation(deps.commons.commonsWidgets)
    implementation(deps.commons.commonsWindowAnims)
    implementation(deps.commons.commonsRecyclerView)

    implementation(deps.misc.dexter)
    implementation(deps.misc.pdfViewer)
    implementation(deps.misc.materialDialogsCore)
    implementation(deps.misc.materialDialogsInput)

    implementation(deps.google.daggerHilt)
    kapt(deps.google.daggerHiltCompiler)

    implementation(deps.misc.hiltBinder)
    kapt(deps.misc.hiltBinderCompiler)

    testImplementation(deps.testing.jUnit)
    androidTestImplementation(deps.testing.jUnitExt)
}