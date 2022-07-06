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

package com.github.kawaiifoxx.inlinecalculator.services

import com.github.kawaiifoxx.inlinecalculator.utils.ICBundle
import com.github.kawaiifoxx.inlinecalculator.domain.ICExpression
import com.github.kawaiifoxx.inlinecalculator.settings.ICSettingsStateService
import com.github.kawaiifoxx.inlinecalculator.settings.customfunctions.ICCustomFunctionStateService
import com.github.kawaiifoxx.inlinecalculator.utils.Constants
import com.udojava.evalex.Expression

/**
 * A simple service to evaluate mathematical expressions.
 *
 * @author shreyansh
 */
class ExpressionEvaluatorService(
    private val functionService: ICCustomFunctionStateService = ICCustomFunctionStateService.instance
) {

    /**
     * Evaluates the given expression.
     *
     * @param expressionStr The expression to be evaluated.
     * @return The result of the evaluation.
     */
    fun evaluate(expressionStr: String): String {
        val expressions = convertToExpressions(expressionStr)

        return expressions.joinToString(separator = getSplitter()) { convertToString(it) }
    }

    private fun convertToString(it: ICExpression) =
        (if (it.startsWithNewLine) getLineSeparator() else "") + it.eval().toPlainString()

    private fun getSplitter(): String = ICSettingsStateService.instance.splitter

    private fun getRoundingMode() = ICSettingsStateService.instance.roundingMode

    private fun getPrecision() = ICSettingsStateService.instance.precision

    /**
     * Takes in string with possibly multiple expressions and converts it to a list of expressions.
     *
     * e.g
     * <code>
     *     1 + 2;
     *     3 + 4;
     * </code>
     *  -> [ICExpression(1 + 2), ICExpression(3 + 4)]
     *
     *  @param expressionStr The expression string to be converted.
     *  @return The list of expressions.
     */
    @Suppress("INACCESSIBLE_TYPE")
    private fun convertToExpressions(expressionStr: String): List<ICExpression> {
        return expressionStr.split(getSplitter())
            .filter { it.isNotBlank() }
            .map { expStr ->
                val expression = Expression(expStr)
                    .setPrecision(getPrecision())
                    .setRoundingMode(getRoundingMode())

                expression.expressionTokenizer.asSequence()
                    .filter { it.type.toString() == Constants.EXPRESSION_EVALUATOR.FUNCTION }
                    .map { functionService.getCompiledFunctionWithName(it.surface) }
                    .filterNotNull()
                    .forEach {
                        if (it.isStringFunction)
                            expression.addLazyFunction(it)
                        else
                            expression.addFunction(it)
                    }

                ICExpression(expression, expStr.substring(0, 2).contains(getLineSeparator()))
            }
    }

    private fun getLineSeparator() = ICBundle.message("ic.line.separator")
}
