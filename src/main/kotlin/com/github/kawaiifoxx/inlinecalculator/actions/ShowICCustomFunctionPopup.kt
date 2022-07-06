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

package com.github.kawaiifoxx.inlinecalculator.actions

import com.github.kawaiifoxx.inlinecalculator.domain.ICCustomFunction
import com.github.kawaiifoxx.inlinecalculator.settings.customfunctions.ICCustomFunctionStateService
import com.github.kawaiifoxx.inlinecalculator.utils.ICBundle
import com.github.kawaiifoxx.inlinecalculator.utils.Utils.capitalize
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.PopupStep
import com.intellij.openapi.ui.popup.SpeedSearchFilter
import com.intellij.openapi.ui.popup.util.BaseListPopupStep
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.Icon
import javax.swing.JComponent

/**
 * This action is used to display a popup menu to insert a custom function in the editor quickly.
 *
 * @author shreyansh
 */
@Suppress("UnstableApiUsage")
class ShowICCustomFunctionPopup : AnAction(), SpeedSearchFilter<ICCustomFunction> {
    override fun getIndexedString(value: ICCustomFunction?): String = value?.name ?: ""

    private val functionStateService = ICCustomFunctionStateService.instance

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT) ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return

        val popup = JBPopupFactory.getInstance().createListPopup(
            MyListPopupStep(project, editor, functionStateService.customFunctionsMap.values.toList())
        )

        popup.showInBestPositionFor(e.dataContext)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.getData(PlatformDataKeys.PROJECT) != null &&
                e.getData(CommonDataKeys.EDITOR) != null
    }

    @Suppress("DialogTitleCapitalization")
    inner class MyListPopupStep(
        private val project: Project,
        private val editor: Editor,
        items: List<ICCustomFunction>
    ) :
        BaseListPopupStep<ICCustomFunction>(ICBundle.message("ic.custom.function.popup.title"), items) {

        override fun getTextFor(value: ICCustomFunction?) = getTextFor(value, mutableMapOf())

        private fun getTextFor(value: ICCustomFunction?, paramMap: Map<String, String> = mutableMapOf()): String {
            val myParamMap = if (value?.isStringFunction == true)
                paramMap.mapValues { "\"${it.value}\"" }
            else paramMap


            return "${value?.name ?: ""}${
                generateSequence(1) { it + 1 }
                    .take(value?.parameterCount ?: 0)
                    .map { myParamMap["p$it"] ?: "p$it" }
                    .joinToString(", ", "(", ")")
            }"
        }

        override fun getIconFor(value: ICCustomFunction?): Icon = AllIcons.Nodes.Function

        override fun isSpeedSearchEnabled(): Boolean = true

        override fun getSpeedSearchFilter(): SpeedSearchFilter<ICCustomFunction> = this


        override fun onChosen(selectedValue: ICCustomFunction?, finalChoice: Boolean): PopupStep<*>? {
            val document = editor.document
            val paramMap = mutableMapOf<String, String>()

            val dialog = createDialogWrapper(selectedValue ?: return FINAL_CHOICE, paramMap)

            dialog.window.addWindowListener(object : WindowAdapter() {
                override fun windowClosed(e: WindowEvent?) {
                    if (dialog.exitCode == DialogWrapper.OK_EXIT_CODE)
                        insertTextInEditor(selectedValue, paramMap, document)
                }
            })


            doFinalStep {
                if (selectedValue.parameterCount > 0)
                    dialog.show()
                else
                    insertTextInEditor(selectedValue, paramMap, document)
            }

            return FINAL_CHOICE
        }

        private fun insertTextInEditor(
            selectedValue: ICCustomFunction?,
            paramMap: MutableMap<String, String>,
            document: Document
        ) {
            val textToBeInserted = getTextFor(selectedValue, paramMap)

            editor.caretModel.allCarets
                .filterNotNull()
                .forEach {
                    WriteCommandAction.runWriteCommandAction(project) {
                        document.insertString(it.offset, textToBeInserted)
                    }
                }
        }

        private fun createDialogWrapper(selectedValue: ICCustomFunction, paramMap: MutableMap<String, String>) =
            object : DialogWrapper(true) {
                init {
                    title = ICBundle.message("ic.custom.function.popup.dialog.title",
                        selectedValue.name.capitalize())
                    init()
                }

                private var focusedField: JComponent? = null

                override fun getPreferredFocusedComponent(): JComponent? = focusedField

                override fun createCenterPanel(): JComponent = panel {
                    row {
                        label(ICBundle.message("ic.custom.function.popup.dialog.label"))
                        contextHelp(
                            ICBundle.message("ic.custom.function.popup.dialog.note.text"),
                            ICBundle.message("ic.custom.function.popup.dialog.note.title")
                        )
                    }.visible(selectedValue.isStringFunction)

                    generateSequence(1) { it + 1 }
                        .take(maxOf(selectedValue.parameterCount, 0))
                        .forEach { paramNo ->
                            row {
                                label(ICBundle.message("ic.custom.function.popup.param.label", paramNo))
                                textField()
                                    .bindText(
                                        getter = { paramMap["p$paramNo"] ?: "" },
                                        setter = { paramMap["p$paramNo"] = it }
                                    ).apply { if (focusedField == null) focusedField = component }
                            }
                        }
                }
            }
    }
}
