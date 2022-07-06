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

import com.github.kawaiifoxx.inlinecalculator.utils.ICBundle
import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.EnumComboBoxModel
import com.intellij.ui.dsl.builder.*
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import java.math.RoundingMode

class ICSettingsConfigurable : BoundSearchableConfigurable(
    displayName = ICBundle.message("ic.settings.display.name"),
    helpTopic = ICBundle.message("ic.settings.display.help.topic"),
    _id = ICBundle.message("ic.settings.id")
) {
    override fun createPanel(): DialogPanel = panel {
        row {
            textField()
                .bindText(
                    getter = { ICSettingsStateService.instance.splitter },
                    setter = { ICSettingsStateService.instance.splitter = it }
                )
                .label(ICBundle.message("ic.settings.expression.splitter.label"))
                .focused()

            contextHelp(
                ICBundle.message("ic.settings.expression.splitter.help.text", ICSettingsStateService.instance.splitter),
                ICBundle.message("ic.settings.expression.splitter.help.title")
            )

            intTextField()
                .bindIntText(
                    getter = { ICSettingsStateService.instance.precision },
                    setter = { ICSettingsStateService.instance.precision = it }
                )
                .label(ICBundle.message("ic.settings.expression.precision.label"))
        }

        row {
            val roundingModeComboBox = comboBox(EnumComboBoxModel(RoundingMode::class.java))
                .bindItem(
                    getter = { ICSettingsStateService.instance.roundingMode },
                    setter = { ICSettingsStateService.instance.roundingMode = it ?: RoundingMode.HALF_UP }
                )
                .label(ICBundle.message("ic.settings.expression.rounding.mode.label"))

            val halfUpHelp = contextHelp(
                ICBundle.message("ic.settings.expression.rounding.mode.half.up.tooltip"),
                ICBundle.message("ic.settings.expression.rounding.mode.help.title")
            ).visible(ICSettingsStateService.instance.roundingMode == RoundingMode.HALF_UP)

            val halfDownHelp = contextHelp(
                ICBundle.message("ic.settings.expression.rounding.mode.half.down.tooltip"),
                ICBundle.message("ic.settings.expression.rounding.mode.help.title")
            ).visible(ICSettingsStateService.instance.roundingMode == RoundingMode.HALF_DOWN)

            val ceilingHelp = contextHelp(
                ICBundle.message("ic.settings.expression.rounding.mode.ceiling.tooltip"),
                ICBundle.message("ic.settings.expression.rounding.mode.help.title")
            ).visible(ICSettingsStateService.instance.roundingMode == RoundingMode.CEILING)

            val floorHelp = contextHelp(
                ICBundle.message("ic.settings.expression.rounding.mode.floor.tooltip"),
                ICBundle.message("ic.settings.expression.rounding.mode.help.title")
            ).visible(ICSettingsStateService.instance.roundingMode == RoundingMode.FLOOR)

            val halfEvenHelp = contextHelp(
                ICBundle.message("ic.settings.expression.rounding.mode.half.even.tooltip"),
                ICBundle.message("ic.settings.expression.rounding.mode.help.title")
            ).visible(ICSettingsStateService.instance.roundingMode == RoundingMode.HALF_EVEN)

            val upHelp = contextHelp(
                ICBundle.message("ic.settings.expression.rounding.mode.up.tooltip"),
                ICBundle.message("ic.settings.expression.rounding.mode.help.title")
            ).visible(ICSettingsStateService.instance.roundingMode == RoundingMode.UP)

            val downHelp = contextHelp(
                ICBundle.message("ic.settings.expression.rounding.mode.down.tooltip"),
                ICBundle.message("ic.settings.expression.rounding.mode.help.title")
            ).visible(ICSettingsStateService.instance.roundingMode == RoundingMode.DOWN)

            val unnecessaryHelp = contextHelp(
                ICBundle.message("ic.settings.expression.rounding.mode.unnecessary.tooltip"),
                ICBundle.message("ic.settings.expression.rounding.mode.help.title")
            ).visible(ICSettingsStateService.instance.roundingMode == RoundingMode.UNNECESSARY)

            val roundingModeMap = mapOf(
                RoundingMode.HALF_UP to halfUpHelp,
                RoundingMode.HALF_DOWN to halfDownHelp,
                RoundingMode.CEILING to ceilingHelp,
                RoundingMode.FLOOR to floorHelp,
                RoundingMode.HALF_EVEN to halfEvenHelp,
                RoundingMode.UP to upHelp,
                RoundingMode.DOWN to downHelp,
                RoundingMode.UNNECESSARY to unnecessaryHelp
            )

            roundingModeComboBox
                .applyToComponent {
                    addItemListener(object : ItemListener {
                        override fun itemStateChanged(e: ItemEvent?) {
                            if (e == null || e.stateChange != ItemEvent.SELECTED) return

                            roundingModeMap.values.forEach { it.visible(false) }
                            roundingModeMap[e.item]?.visible(true)
                        }
                    })
                }
        }
    }
}
