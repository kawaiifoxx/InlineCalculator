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

package com.github.kawaiifoxx.inlinecalculator.compiler

import java.util.*
import javax.tools.*


/**
 * Compile Java sources in-memory
 */
class InMemoryJavaCompiler private constructor() {
    private val javac: JavaCompiler = ToolProvider.getSystemJavaCompiler()
    private var classLoader: DynamicClassLoader = DynamicClassLoader(ClassLoader.getSystemClassLoader())
    private var options: Iterable<String> = emptyList()
    var ignoreWarnings = false
    private val sourceCodes: MutableMap<String, SourceCode> = mutableMapOf()


    fun useParentClassLoader(parent: ClassLoader?): InMemoryJavaCompiler {
        classLoader = DynamicClassLoader(parent)
        return this
    }

    /**
     * @return the class loader used internally by the compiler
     */
    val classloader: DynamicClassLoader
        get() = classLoader

    /**
     * Options used by the compiler, e.g. '-Xlint:unchecked'.
     *
     * @param options
     * @return
     */
    fun useOptions(vararg options: String): InMemoryJavaCompiler {
        this.options = listOf(*options)
        return this
    }

    /**
     * Ignore non-critical compiler output, like unchecked/unsafe operation
     * warnings.
     *
     * @return
     */
    fun ignoreWarnings(): InMemoryJavaCompiler {
        ignoreWarnings = true
        return this
    }

    /**
     * Compile all sources
     *
     * @return Map containing instances of all compiled classes
     * @throws Exception
     */
    fun compileAll(): Map<String, Class<*>> {
        if (sourceCodes.isEmpty()) {
            throw CompilationException("No source code to compile")
        }

        val collector = DiagnosticCollector<JavaFileObject>()
        val fileManager = ExtendedStandardJavaFileManager(javac.getStandardFileManager(null, null, null), classLoader)

        val task = javac.getTask(null, fileManager, collector, options, null, sourceCodes.values)
        val result = task.call()

        if (!result || collector.diagnostics.isNotEmpty()) {
            val exceptionMsg = StringBuffer()
            exceptionMsg.append("Unable to compile the source")

            var hasWarnings = false
            var hasErrors = false

            for (d in collector.diagnostics) {
                when (d.kind) {
                    Diagnostic.Kind.NOTE, Diagnostic.Kind.MANDATORY_WARNING, Diagnostic.Kind.WARNING ->
                        hasWarnings = true

                    Diagnostic.Kind.OTHER, Diagnostic.Kind.ERROR -> hasErrors = true

                    else -> hasErrors = true
                }

                exceptionMsg.append("\n").append("[kind=").append(d.kind)
                exceptionMsg.append(", ").append("line=").append(d.lineNumber)
                exceptionMsg.append(", ").append("message=").append(d.getMessage(Locale.US)).append("]")
            }

            if (hasWarnings && !ignoreWarnings || hasErrors) {
                throw CompilationException(exceptionMsg.toString())
            }
        }

        return sourceCodes.keys.associateWith { classLoader.loadClass(it) }
    }

    /**
     * Compile single source
     *
     * @param className
     * @param sourceCode
     * @return
     * @throws Exception
     */
    fun compile(className: String, sourceCode: String?): Class<*> {
        return addSource(className, sourceCode).compileAll()[className]!!
    }

    /**
     * Add source code to the compiler
     *
     * @param className
     * @param sourceCode
     * @return
     * @throws Exception
     * @see {@link .compileAll
     */
    fun addSource(className: String, sourceCode: String?): InMemoryJavaCompiler {
        sourceCodes[className] = SourceCode(className, sourceCode)
        return this
    }

    companion object {
        fun newInstance(): InMemoryJavaCompiler {
            return InMemoryJavaCompiler()
        }
    }
}
