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

package com.github.kawaiifoxx.inlinecalculator.settings.customfunctions

import com.github.kawaiifoxx.inlinecalculator.domain.ICCustomFunction
import com.github.kawaiifoxx.inlinecalculator.utils.ICBundle
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.EditorKind
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.ui.NamedConfigurable
import com.intellij.openapi.util.Comparing
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.bindIntText
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import org.apache.commons.lang.ObjectUtils
import javax.swing.JComponent

/**
 * This class used for displaying custom function manager settings.
 *
 * @author shreyansh
 */
@Suppress("UnstableApiUsage", "DialogTitleCapitalization")
class ICCustomFunctionConfigurable(private var myFunction: ICCustomFunction, updateTree: Runnable) :
    NamedConfigurable<ICCustomFunction>(false, updateTree) {

    private val editorFactory = EditorFactory.getInstance()
    private val myDocument = editorFactory.createDocument(myFunction.code)
    private val myEditor = editorFactory.createEditor(myDocument, null, EditorKind.MAIN_EDITOR) as EditorImpl
    private val myOldFunction = myFunction.copy()

    private val functionStateService = ICCustomFunctionStateService.instance

    /**
     * Kotlin UI DSL builder for creating a custom function manager settings.
     */
    private val myPanel = panel {
        row(ICBundle.message("ic.custom.functions.param.count.label")) {
            intTextField()
                .bindIntText(getter = { myFunction.parameterCount }, setter = { myFunction.parameterCount = it })
            contextHelp(
                ICBundle.message("ic.custom.functions.param.count.help.text"),
                ICBundle.message("ic.custom.functions.param.count.help.title")
            )
        }

        group(ICBundle.message("ic.custom.functions.code.label")) {
            row {
                cell(myEditor.scrollPane)
                    .focused()
                    .resizableColumn()
            }
        }
    }

    override fun isModified(): Boolean = myPanel.isModified()

    override fun apply() {
        myPanel.apply()
    }

    override fun getDisplayName(): String = myFunction.name

    override fun setDisplayName(name: String?) {
        if (name == null || Comparing.strEqual(myFunction.name, name)) return
        myFunction.name = name
    }


    override fun getEditableObject(): ICCustomFunction =
        ICCustomFunction(myFunction.name, myFunction.code, myFunction.parameterCount, myFunction.isStringFunction)

    override fun getBannerSlogan(): String = "Custom Function: ${myFunction.name}"

    override fun createOptionsPanel(): JComponent {
        val panel = JBScrollPane(myPanel)
        panel.border = JBUI.Borders.empty(0, 10, 10, 10)
        return panel
    }

    override fun disposeUIResources() {
        myFunction.code = myDocument.charsSequence.toString()

        if (!ObjectUtils.equals(myOldFunction, myFunction))
            functionStateService.updateFunction(myFunction)

        EditorFactory.getInstance().releaseEditor(myEditor)
    }

    override fun getIcon(expanded: Boolean) = AllIcons.Nodes.Function
}
