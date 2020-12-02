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

package com.paulrybitskyi.docskanner.ui.dashboard.fragment

import com.paulrybitskyi.docskanner.ui.dashboard.fragment.mapping.DocModelFactory
import com.paulrybitskyi.docskanner.ui.dashboard.fragment.mapping.DocModelFactoryImpl
import com.paulrybitskyi.docskanner.ui.dashboard.fragment.mapping.DocsUiStateFactory
import com.paulrybitskyi.docskanner.ui.dashboard.fragment.mapping.DocsUiStateFactoryImpl
import com.paulrybitskyi.docskanner.utils.DocDetailsBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
internal object DashboardModule {


    @Provides
    fun provideDocsUiStateFactory(
        docModelFactory: DocModelFactory
    ): DocsUiStateFactory {
        return DocsUiStateFactoryImpl(docModelFactory)
    }


    @Provides
    fun provideDocModelFactory(docDetailsBuilder: DocDetailsBuilder): DocModelFactory {
        return DocModelFactoryImpl(docDetailsBuilder)
    }


}