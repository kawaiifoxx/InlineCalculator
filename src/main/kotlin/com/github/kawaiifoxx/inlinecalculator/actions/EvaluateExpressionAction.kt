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

import com.github.kawaiifoxx.inlinecalculator.services.ExpressionEvaluatorService
import com.github.kawaiifoxx.inlinecalculator.utility.Constants
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction

/**
 * This class implements action for evaluating the expression.
 * This action is main entry point for inline calculator.
 *
 * @author shreyansh
 */
class EvaluateExpressionAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val editor = e.getData(PlatformDataKeys.EDITOR)
        val selectedText = editor?.selectionModel?.selectedText ?: return
        val evaluatorService = ApplicationManager.getApplication().getService(ExpressionEvaluatorService::class.java)

        val evaluationResult = try {
            evaluatorService.evaluate(selectedText)
        } catch (exception: Exception) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup(Constants.UI.ERROR_NOTIFICATION_GROUP_ID)
                .createNotification(exception.message ?: "Unknown error occurred", NotificationType.ERROR)
                .notify(project)
            return
        }

        replaceSelectedText(e, evaluationResult)

        editor.caretModel.primaryCaret.removeSelection()
    }

    /**
     * This method replaces the selected text with the given value.
     *
     * @param e AnActionEvent instance
     * @param value Value to replace the selected text
     */
    private fun replaceSelectedText(e: AnActionEvent, value: String) {
        val editor = e.getData(PlatformDataKeys.EDITOR) ?: return
        val primaryCaret = editor.caretModel.primaryCaret
        val start = primaryCaret.selectionStart
        val end = primaryCaret.selectionEnd


        WriteCommandAction.runWriteCommandAction(e.project) {
            editor.document.replaceString(start, end, value)
        }
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
    }
}
