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

import com.github.kawaiifoxx.inlinecalculator.adapters.AbstractFunctionAdapter
import com.github.kawaiifoxx.inlinecalculator.compiler.CompilationException
import com.github.kawaiifoxx.inlinecalculator.compiler.InMemoryJavaCompiler
import com.github.kawaiifoxx.inlinecalculator.domain.ICCustomFunction
import com.github.kawaiifoxx.inlinecalculator.domain.converter.ICCustomFunctionMapConverter
import com.github.kawaiifoxx.inlinecalculator.utils.ICBundle
import com.github.kawaiifoxx.inlinecalculator.utils.Utils.capitalize
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.OptionTag

/**
 * Stores user defined functions.
 *
 * @author shreyansh
 */
@State(
    name = "com.github.kawaiifoxx.inlinecalculator.settings.customfunctions.ICCustomFunctionStateService",
    storages = [Storage("ICCustomFunctions.xml")]
)
class ICCustomFunctionStateService : PersistentStateComponent<ICCustomFunctionStateService> {
    @OptionTag(converter = ICCustomFunctionMapConverter::class)
    var customFunctionsMap: MutableMap<String, ICCustomFunction> = mutableMapOf()

    @Transient
    private var preCompiledFunctionsCache: MutableMap<String, AbstractFunctionAdapter> = mutableMapOf()

    companion object {
        val instance: ICCustomFunctionStateService
            get() = ApplicationManager.getApplication().getService(ICCustomFunctionStateService::class.java)
    }

    override fun initializeComponent() {
        preCompiledFunctionsCache = customFunctionsMap.let { fnMap ->
            try {
                compileAll(fnMap).map {
                    it.key to AbstractFunctionAdapter(
                        fnMap[it.key]?.name ?: it.key,
                        fnMap[it.key]?.parameterCount ?: 0,
                        fnMap[it.key]?.isStringFunction ?: false,
                        it.value
                    )
                }.toMap().toMutableMap()
            } catch (ex: CompilationException) {
                compileOneByOne()
            }
        }
    }

    private fun compileOneByOne() = customFunctionsMap.map {
        it.key to AbstractFunctionAdapter(
            it.value.name,
            it.value.parameterCount,
            it.value.isStringFunction,
            compileCode(it.key, it.value.code) ?: return@map null
        )
    }.filterNotNull().toMap().toMutableMap()


    override fun getState(): ICCustomFunctionStateService = this

    override fun loadState(state: ICCustomFunctionStateService) {
        XmlSerializerUtil.copyBean(state, this)
    }

    fun addFunction(name: String, isStringFunction: Boolean, code: String = prepareTemplate(name, isStringFunction)): ICCustomFunction {
        val packageName = ICBundle.message("ic.custom.function.service.default.package.name")
        val className = "$packageName.${name.capitalize()}"
        val customFunction = ICCustomFunction(
            name = name,
            code = code,
            parameterCount = 0,
            isStringFunction = isStringFunction
        )

        customFunctionsMap[className] = customFunction

        compileAndAddToCache(customFunction)
        return customFunction
    }

    private fun compileCode(fqClassName: String, sourceCode: String): Class<*>? =
        try {
            InMemoryJavaCompiler.newInstance()
                .ignoreWarnings()
                .compile(fqClassName, sourceCode)
        } catch (e: CompilationException) {
            null
        }

    private fun compileAll(customFunctions: Map<String, ICCustomFunction>): Map<String, Class<*>> {
        val compiler = InMemoryJavaCompiler.newInstance()
            .ignoreWarnings()

        customFunctions.forEach {
            compiler.addSource(it.key, it.value.code)
        }

        return compiler.compileAll()
    }

    private fun compileAndAddToCache(function: ICCustomFunction) {
        val fqClassName =
            "${ICBundle.message("ic.custom.function.service.default.package.name")}.${function.name.capitalize()}"
        val myClazz = compileCode(fqClassName, function.code) ?: return

        preCompiledFunctionsCache[fqClassName] = AbstractFunctionAdapter(
            function.name,
            function.parameterCount,
            function.isStringFunction,
            myClazz
        )
    }

    fun updateFunction(newFunction: ICCustomFunction) {
        val newFqClassName =
            "${ICBundle.message("ic.custom.function.service.default.package.name")}.${newFunction.name.capitalize()}"

        val myClazz = compileCode(newFqClassName, newFunction.code) ?: return

        preCompiledFunctionsCache[newFqClassName] = AbstractFunctionAdapter(
            newFunction.name,
            newFunction.parameterCount,
            newFunction.isStringFunction,
            myClazz
        )
    }

    fun removeFunction(function: ICCustomFunction): Boolean {
        val fqClassName =
            "${ICBundle.message("ic.custom.function.service.default.package.name")}.${function.name.capitalize()}"

        preCompiledFunctionsCache.remove(fqClassName)
        return customFunctionsMap.remove(fqClassName) != null
    }

    private fun prepareTemplate(name: String, isStringFunction: Boolean = false): String {
        val funType = if (isStringFunction) "String" else "BigDecimal"
        return """
                    package ${ICBundle.message("ic.custom.function.service.default.package.name")};
        
                    ${if (!isStringFunction) "import java.math.BigDecimal;" else ""}
                    import java.util.List;
        
                    public class ${name.capitalize()} {
                        
                        public ${name.capitalize()}() {
                        }
                        
                        public $funType eval(List<$funType> parameters) {
                            //TODO: Implement this to add custom function.
                            return null;
                        }
                    }
            """.trimIndent()
    }

    fun checkIfExists(name: String) =
        customFunctionsMap
            .containsKey("${ICBundle.message("ic.custom.function.service.default.package.name")}.${name.capitalize()}")

    fun getCompiledFunctionWithName(name: String) =
        preCompiledFunctionsCache["${ICBundle.message("ic.custom.function.service.default.package.name")}.${name.capitalize()}"]

}

