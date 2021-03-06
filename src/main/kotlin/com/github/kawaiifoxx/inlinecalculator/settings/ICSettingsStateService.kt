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

package com.github.kawaiifoxx.inlinecalculator.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import java.math.RoundingMode


/**
 * Stores users settings.
 *
 * @author shreyansh
 */
@State(
    name = "com.github.kawaiifoxx.inlinecalculator.settings.ICSettingsState",
    storages = [Storage("InlineCalculator.xml")]
)
class ICSettingsStateService : PersistentStateComponent<ICSettingsStateService> {
    var splitter: String = ";"
    var precision: Int = 5
    var roundingMode: RoundingMode = RoundingMode.HALF_UP

    companion object {
        val instance: ICSettingsStateService
            get() = ApplicationManager.getApplication().getService(ICSettingsStateService::class.java)
    }

    override fun getState(): ICSettingsStateService {
        return this
    }

    override fun loadState(state: ICSettingsStateService) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
