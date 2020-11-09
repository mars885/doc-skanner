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

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(deps.kotlin.stdLib)

    implementation(deps.androidX.appCompat)
    implementation(deps.androidX.navFragmentKtx)
    implementation(deps.androidX.navUiKtx)
    implementation(deps.androidX.constraintLayout)
    implementation(deps.androidX.recyclerView)
    implementation(deps.androidX.lifecycleCommonJava8)
    implementation(deps.androidX.lifecycleViewModel)
    implementation(deps.androidX.coreKtx)
    implementation(deps.androidX.fragmentKtx)

    implementation(deps.google.materialComponents)

    implementation(deps.commons.commonsWindowAnims)

    implementation(deps.google.daggerHilt)
    kapt(deps.google.daggerHiltCompiler)

    implementation(deps.androidX.daggerHiltAssistedInjection)
    kapt(deps.androidX.daggerHiltAssistedInjectionCompiler)

    testImplementation(deps.testing.jUnit)
    androidTestImplementation(deps.testing.jUnitExt)
}