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

package com.paulrybitskyi.docscanner.ui.dashboard

import androidx.fragment.app.viewModels
import com.paulrybitskyi.docscanner.R
import com.paulrybitskyi.docscanner.databinding.FragmentDashboardBinding
import com.paulrybitskyi.docscanner.ui.base.BaseFragment
import com.paulrybitskyi.docscanner.utils.viewBinding

internal class DashboardFragment : BaseFragment<
    FragmentDashboardBinding,
    DashboardViewModel
>(R.layout.fragment_dashboard) {


    override val viewBinding by viewBinding(FragmentDashboardBinding::bind)
    override val viewModel by viewModels<DashboardViewModel>()


    override fun onInit() {
        super.onInit()
    }


}