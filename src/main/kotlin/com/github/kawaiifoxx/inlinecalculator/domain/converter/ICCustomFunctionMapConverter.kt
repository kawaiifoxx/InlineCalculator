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
package com.github.kawaiifoxx.inlinecalculator.domain.converter

import com.github.kawaiifoxx.inlinecalculator.domain.ICCustomFunction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.util.xmlb.Converter

class ICCustomFunctionMapConverter : Converter<Map<String, ICCustomFunction>>() {
    private val gson = Gson()

    override fun toString(value: Map<String, ICCustomFunction>): String = gson.toJson(value)

    override fun fromString(value: String): Map<String, ICCustomFunction> =
        gson.fromJson(value, object : TypeToken<Map<String, ICCustomFunction>>() {}.type)
}
