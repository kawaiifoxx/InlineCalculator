/**
 * Copyright 2022  Shreyansh Gupta
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

package com.github.kawaiifoxx.inlinecalculator.settings

import com.github.kawaiifoxx.inlinecalculator.utility.Constants
import com.github.kawaiifoxx.inlinecalculator.utility.Constants.UI.DISPLAY_NAME
import com.intellij.codeInsight.template.postfix.settings.PostfixTemplatesCheckboxTree
import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.layout.panel

class ICSettingsConfigurable : BoundSearchableConfigurable(
    displayName = DISPLAY_NAME, helpTopic = DISPLAY_NAME, _id = Constants.UI.SETTINGS_ID
) {
    override fun createPanel(): DialogPanel = panel {
        row(Constants.UI.EXPRESSION_SPLITTER_LABEL) {
            textField(getter = { ICSettingsState.instance.splitter },
                setter = { ICSettingsState.instance.splitter = it })
        }
        titledRow("Functions") {}
        row {
        }.scrollPane(
            ToolbarDecorator.createDecorator(PostfixTemplatesCheckboxTree())
                .setAddAction {
                    
                }
                .createPanel()
        )
    }
}
