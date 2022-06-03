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

package com.github.kawaiifoxx.inlinecalculator.customfunctions

import com.udojava.evalex.AbstractFunction
import java.math.BigDecimal

class AbstractFunctionAdapter(name: String?, numParams: Int, booleanFunction: Boolean, private val myFunctionClass: Class<*>) :
    AbstractFunction(name, numParams, booleanFunction) {
    private val myFunctionObject = myFunctionClass.getConstructor().newInstance()


    override fun eval(parameters: MutableList<BigDecimal>?): BigDecimal
         = myFunctionClass.getMethod("eval", MutableList::class.java).invoke(myFunctionObject, parameters) as BigDecimal
}
