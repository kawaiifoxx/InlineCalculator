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

package com.github.kawaiifoxx.inlinecalculator.adapters

import com.udojava.evalex.AbstractFunction
import com.udojava.evalex.Expression.*
import java.math.BigDecimal

class AbstractFunctionAdapter(
    name: String?,
    numParams: Int,
    val isStringFunction: Boolean,
    private val myFunctionClass: Class<*>
) : AbstractFunction(name, numParams) {
    private val myFunctionObject = myFunctionClass.getConstructor().newInstance()

    override fun eval(parameters: MutableList<BigDecimal>?): BigDecimal =
        myFunctionClass.getMethod("eval", MutableList::class.java).invoke(myFunctionObject, parameters) as BigDecimal

    override fun lazyEval(lazyParams: MutableList<LazyNumber>?): LazyNumber {
        if (!isStringFunction) {
            return super.lazyEval(lazyParams)
        }

        val result = myFunctionClass.getMethod("eval", MutableList::class.java)
            .invoke(myFunctionObject, getParams(lazyParams)) as String

        return object : LazyNumber {
            override fun eval(): BigDecimal = MyBigDecimal(result)

            override fun getString() = result
        }
    }

    private fun getParams(params: List<LazyNumber>?) = params?.map { it.string }?.toMutableList() ?: mutableListOf()

    /**
     * Checks whether the given value can be parsed to [BigDecimal] if so, returns it else returns "0".
     */
    private fun checkIfCanBeParsedToBigDecimal(param: String): String = try {
        BigDecimal(param)
        param
    } catch (e: NumberFormatException) {
        0.toString()
    }

    private inner class MyBigDecimal(val actualValue: String) : BigDecimal(checkIfCanBeParsedToBigDecimal(actualValue)) {
        override fun toPlainString() = actualValue

        override fun toEngineeringString() = actualValue

        override fun toString() = actualValue

        override fun stripTrailingZeros(): BigDecimal = this

        override fun toByte(): Byte {
            throw UnsupportedOperationException()
        }

        override fun toChar(): Char {
            throw UnsupportedOperationException()
        }

        override fun toShort(): Short {
            throw UnsupportedOperationException()
        }

    }
}
