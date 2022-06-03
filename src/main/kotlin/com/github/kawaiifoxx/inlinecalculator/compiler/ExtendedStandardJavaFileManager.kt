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

import javax.tools.FileObject
import javax.tools.ForwardingJavaFileManager
import javax.tools.JavaFileManager
import javax.tools.JavaFileObject

class ExtendedStandardJavaFileManager
/**
 * Creates a new instance of ForwardingJavaFileManager.
 *
 * @param fileManager
 * delegate to this file manager
 * @param cl
 */
    (fileManager: JavaFileManager?, private val cl: DynamicClassLoader) :
    ForwardingJavaFileManager<JavaFileManager?>(fileManager) {
    private val compiledCode: MutableList<CompiledCode> = ArrayList()


    override fun getJavaFileForOutput(
        location: JavaFileManager.Location, className: String,
        kind: JavaFileObject.Kind, sibling: FileObject,
    ): JavaFileObject = try {
        val innerClass = CompiledCode(className)

        compiledCode.add(innerClass)
        cl.addCode(innerClass)

        innerClass
    } catch (e: Exception) {
        throw RuntimeException("Error while creating in-memory output file for $className", e)
    }


    override fun getClassLoader(location: JavaFileManager.Location): ClassLoader {
        return cl
    }
}
