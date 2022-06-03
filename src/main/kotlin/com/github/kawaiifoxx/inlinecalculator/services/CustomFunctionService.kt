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

import com.github.kawaiifoxx.inlinecalculator.compiler.InMemoryJavaCompiler
import com.github.kawaiifoxx.inlinecalculator.customfunctions.AbstractFunctionAdapter
import com.udojava.evalex.AbstractFunction

class CustomFunctionService {
    fun addFunction(name: String, function: String) {
        TODO("Not yet implemented")
    }

    fun getFunction(name: String): AbstractFunction {
        val myClass = """
            package com.github.kawaiifoxx.inlinecalculator.customfunctions;

            import java.math.BigDecimal;
            import java.util.List;

            public class CustomFunction {
                
                public CustomFunction() {
                }
                
                public BigDecimal eval(List<BigDecimal> parameters) {
                    return parameters.get(0).add(BigDecimal.TEN);
                }
            }

        """.trimIndent()

        val myClazz = InMemoryJavaCompiler.newInstance()
            .ignoreWarnings()
            .compile("com.github.kawaiifoxx.inlinecalculator.customfunctions.CustomFunction", myClass)

        return AbstractFunctionAdapter("add10", 1, false, myClazz)
    }
}
