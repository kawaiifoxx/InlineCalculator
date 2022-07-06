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

import com.github.kawaiifoxx.inlinecalculator.utils.ICBundle
import com.github.kawaiifoxx.inlinecalculator.domain.ICCustomFunction
import com.github.kawaiifoxx.inlinecalculator.utils.Constants
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.*
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.ComponentPredicate
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JComponent
import javax.swing.JTextField
import javax.swing.tree.DefaultTreeModel

@Suppress("UnstableApiUsage")
class ICCustomFunctionMasterDetailsConfigurable : MasterDetailsComponent() {

    private val customFunctionStateService = ICCustomFunctionStateService.instance

    override fun getDisplayName() = ICBundle.message("ic.custom.function.manager.display.name")

    init {
        initTree()
    }

    override fun initTree() {
        super.initTree()
        myTree.showsRootHandles = false
        myTree.emptyText.text = ICBundle.message("ic.custom.function.manager.empty.text")
    }

    override fun wasObjectStored(editableObject: Any?) = true

    override fun reset() {
        myRoot.removeAllChildren()
        loadCustomFunctions()

        super.reset()
    }

    override fun createActions(fromPopup: Boolean): ArrayList<AnAction> =
        arrayListOf(MyAddAction(), MyDeleteAction(forAll {
            if (it !is MyNode)
                return@forAll false

            it.configurable?.editableObject is ICCustomFunction
        }))

    override fun onItemDeleted(item: Any?) {
        if (item !is ICCustomFunction) return

        customFunctionStateService.removeFunction(item)
    }


    private fun loadCustomFunctions() {
        customFunctionStateService.customFunctionsMap.values.forEach {
            myRoot.add(MyNode(ICCustomFunctionConfigurable(it, TREE_UPDATER)))
        }
    }

    private fun createCustomFunction(title: String) {
        var name = ""
        var isStringFunction = false

        val dialog = object : DialogWrapper(myTree, false) {
            init {
                this.title = title
                init()
            }

            private var focusedField: JComponent? = null
            private var nameField: JTextField? = null

            override fun getPreferredFocusedComponent(): JComponent? = focusedField

            @Suppress("DialogTitleCapitalization")
            override fun createCenterPanel(): JComponent = panel {
                row(ICBundle.message("ic.custom.function.manager.create.name.label")) {
                    nameField = textField().bindText(getter = { name }, setter = { name = it }).component
                    contextHelp(
                        ICBundle.message("ic.custom.function.manager.create.name.help"),
                        ICBundle.message("ic.custom.function.manager.create.name.help.title")
                    )
                }

                row {
                    val checkBox = checkBox(ICBundle.message("ic.custom.function.manager.checkbox.name.label"))
                        .bindSelected(getter = { isStringFunction }, setter = { isStringFunction = it })

                    contextHelp(
                        ICBundle.message("ic.custom.function.manager.checkbox.name.comment"),
                        ICBundle.message("ic.custom.function.manager.checkbox.name.comment.title")
                    ).visibleIf(object : ComponentPredicate() {
                        override fun addListener(listener: (Boolean) -> Unit) {
                            checkBox.component.addItemListener { listener(invoke()) }
                        }

                        override fun invoke(): Boolean = checkBox.component.isSelected
                    })
                }
            }

            override fun doValidateAll(): MutableList<ValidationInfo> {
                if (nameField?.text.isNullOrBlank())
                    return mutableListOf(ValidationInfo(ICBundle.message("ic.validation.custom.function.name.empty")))

                if (nameField?.text?.matches(Constants.UI.FUNCTION_NAME_REGEX) == false)
                    return mutableListOf(ValidationInfo(ICBundle.message("ic.validation.custom.function.name.invalid")))

                if (customFunctionStateService.checkIfExists(
                        nameField?.text?.lowercase() ?: return super.doValidateAll()
                    )
                )
                    return mutableListOf(ValidationInfo(ICBundle.message("ic.validation.custom.function.already.exists")))

                return super.doValidateAll()
            }
        }

        dialog.window.addWindowListener(object : WindowAdapter() {
            override fun windowClosed(e: WindowEvent?) {
                if (dialog.exitCode != DialogWrapper.OK_EXIT_CODE) return

                addNewCustomFunction(customFunctionStateService.addFunction(name.lowercase(), isStringFunction))
            }
        })

        dialog.show()
    }

    private fun addNewCustomFunction(function: ICCustomFunction) {
        val nodeToAdd = MyNode(ICCustomFunctionConfigurable(function, TREE_UPDATER))
        myRoot.add(nodeToAdd)
        ((myTree.model) as DefaultTreeModel).reload(myRoot)
        selectNodeInTree(nodeToAdd)
    }


    override fun getEmptySelectionString(): String = ICBundle.message("ic.custom.function.manager.empty.selection.text")

    inner class MyAddAction : DumbAwareAction(
        ICBundle.message("ic.custom.function.manager.add.text"),
        ICBundle.message("ic.custom.function.manager.add.description"),
        Constants.UI.IC_CUSTOM_FUNCTIONS_ADD_ICON
    ) {
        override fun actionPerformed(e: AnActionEvent) {
            createCustomFunction(ICBundle.message("ic.custom.function.manager.add.description"))
        }
    }
}
