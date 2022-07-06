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

@file:Suppress("unused")

package icons

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object ICIcons {
    @JvmField
    val PluginIcon12: Icon = IconLoader.getIcon("/icons/pluginIcon12By12.svg", javaClass)

    @JvmField
    val PluginIcon13: Icon = IconLoader.getIcon("/icons/pluginIcon13By13.svg", javaClass)

    @JvmField
    val PluginIcon16: Icon = IconLoader.getIcon("/icons/pluginIcon16By16.svg", javaClass)
}
