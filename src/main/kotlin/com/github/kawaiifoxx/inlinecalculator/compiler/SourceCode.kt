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

import java.net.URI
import javax.tools.JavaFileObject
import javax.tools.SimpleJavaFileObject


class SourceCode(className: String, contents: String?) :
    SimpleJavaFileObject(URI.create("string:///" + className.replace('.', '/')
            + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE) {
    private val contents: String?
    private val className: String

    init {
        this.contents = contents
        this.className = className
    }

    override fun getCharContent(ignoreEncodingErrors: Boolean): CharSequence? {
        return contents
    }
}