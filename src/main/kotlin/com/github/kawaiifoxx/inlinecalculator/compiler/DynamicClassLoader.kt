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


class DynamicClassLoader(parent: ClassLoader?) : ClassLoader(parent) {
    private val customCompiledCode: MutableMap<String, CompiledCode> = HashMap()
    fun addCode(cc: CompiledCode) {
        customCompiledCode[cc.name] = cc
    }


    override fun findClass(name: String): Class<*> {
        val cc = customCompiledCode[name] ?: return super.findClass(name)
        val byteCode = cc.byteCode

        return defineClass(name, byteCode, 0, byteCode.size)
    }
}
