# DocSkanner
An Android application that makes it possible to automatically scan and digitize documents from photos.

![Min API](https://img.shields.io/badge/API-23%2B-orange.svg?style=flat)
[![Platform](https://img.shields.io/badge/platform-Android-green.svg)](http://developer.android.com/index.html)
[![Build](https://github.com/mars885/doc-skanner/workflows/Build/badge.svg?branch=master)](https://github.com/mars885/doc-skanner/actions)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Screenshots
<p align="center">
<img src="/media/screenshot1.png" width="24%"/>
<img src="/media/screenshot2.png" width="24%"/>
<img src="/media/screenshot3.png" width="24%"/>
<img src="/media/screenshot4.png" width="24%"/>
</p>

## Tech Stack & Open-Source Libraries
* [Kotlin](https://kotlinlang.org) language
* [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) and [Flow](https://kotlinlang.org/docs/reference/coroutines/flow.html#asynchronous-flow) for asynchronous programming
* Architecture:
  * MVVM and sprinkles of MVI (View <--> ViewModel <--> UseCase)
  * Cleanish architecture project structure ([domain](https://github.com/mars885/doc-skanner/tree/master/domain/src/main/java/com/paulrybitskyi/docskanner/domain), [data](https://github.com/mars885/doc-skanner/tree/master/data/src/main/java/com/paulrybitskyi/docskanner/data) and [presentation](https://github.com/mars885/doc-skanner/tree/master/app/src/main/java/com/paulrybitskyi/docskanner) modules)
* [JetPack](https://developer.android.com/jetpack) libraries:
  * [Navigation](https://developer.android.com/jetpack/androidx/releases/navigation)
  * [Fragment](https://developer.android.com/jetpack/androidx/releases/fragment)
  * [Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle)
  * [Dagger Hilt](https://developer.android.com/jetpack/androidx/releases/hilt) with [ViewModel integration](https://developer.android.com/training/dependency-injection/hilt-jetpack#viewmodels)
* [OpenCV](https://opencv.org) for image processing
* [PdfDocument](https://developer.android.com/reference/android/graphics/pdf/PdfDocument) for generating PDF documents
* [AndroidPdfViewer](https://github.com/barteksc/AndroidPdfViewer) for displaying PDF documents
* [Picasso](https://github.com/square/picasso) for image loading and caching
* [Material Components](https://github.com/material-components/material-components-android) for common UI components
* [Material Dialogs](https://github.com/afollestad/material-dialogs) for displaying dialogs
* [Dexter](https://github.com/Karumi/Dexter) for requesting runtime permissions
* [Kotlin Gradle DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html) for build scipts
* [buildSrc](https://docs.gradle.org/current/userguide/organizing_gradle_projects.html#sec:build_sources) module for dependency management
* [Gradle Versions Plugin](https://github.com/ben-manes/gradle-versions-plugin) for updating dependencies

For more information about used dependencies, see [this](https://github.com/mars885/doc-skanner/blob/master/buildSrc/src/main/java/Config.kt) file.

## Download
This project contains OpenCV native libraries for every mobile architecture (arm, mips, x86 and so on), which means that the APK size in raw form is enormous (~203 MB). If you want to take a quick look at the app and the size does not bother you, go ahead and download the latest APK from the [releases](https://github.com/mars885/doc-skanner/releases). If you want a smaller-sized APK, you can always clone the project and install it using Android Studio, which is smart enough to bundle only the native libraries for your phone's architecture and exclude others, resulting in a much smaller APK size (~30 MB).

## Resources
1. [Canny Edge Detection](https://docs.opencv.org/master/da/d22/tutorial_py_canny.html). This tutorial explains the Canny edge detection algorithm, which plays a big role in identifying a document in the photo.
2. [Fundementals of Image Contours](https://evergreenllc2020.medium.com/fundamentals-of-image-contours-3598a9bcc595). This article explains image countours, what they are, and why they are useful for image processing.
3. [Contour Features](https://docs.opencv.org/master/dd/d49/tutorial_py_contour_features.html). This tutorial explains the features of contours, like area, perimeter, centroid, bounding box.
4. [Perspective Morphing](https://www.pyimagesearch.com/2014/08/25/4-point-opencv-getperspective-transform-example/). This article goes into the details of what perspective morphing is, where it is applicable, how it works, etc.
5. [Application of Perspective Morphing](https://www.pyimagesearch.com/2014/05/05/building-pokedex-python-opencv-perspective-warping-step-5-6/). This article shows an example of applying perspective morhping to cut out and collect images of pokemons.

## License

DocSkanner is licensed under the [Apache 2.0 License](LICENSE).