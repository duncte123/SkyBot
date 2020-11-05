/*
 * Skybot, a multipurpose discord bot
 *      Copyright (C) 2017 - 2020  Duncan "duncte123" Sterken & Ramid "ramidzkh" Khan & Maurice R S "Sanduhr32"
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ml.duncte123.skybot.web.handlers

import com.dunctebot.models.settings.GuildSetting
import com.fasterxml.jackson.databind.JsonNode
import ml.duncte123.skybot.SkyBot
import ml.duncte123.skybot.Variables
import ml.duncte123.skybot.extensions.isUnavailable
import ml.duncte123.skybot.web.WebSocketClient
import ml.duncte123.skybot.websocket.SocketHandler

class GuildSettingsHandler(private val variables: Variables, client: WebSocketClient) : SocketHandler(client) {
    override fun handleInternally(data: JsonNode) {
        if (data.has("remove")) {
            removeGuildSettings(data["remove"])
        }

        if (data.has("update")) {
            updateGuildSettings(data["update"])
        }
    }

    private fun updateGuildSettings(guildSettings: JsonNode) {
        val shardManager = SkyBot.getInstance().shardManager

        guildSettings.forEach {
            val setting = variables.jackson.readValue(it.traverse(), GuildSetting::class.java)

            if (shardManager.getGuildById(setting.guildId) != null && !shardManager.isUnavailable(setting.guildId)) {
                variables.guildSettingsCache.put(setting.guildId, setting)

                // TODO
                //  - Check if invite caching is enabled
                //  - Init caching if it is enabled
            }
        }
    }

    private fun removeGuildSettings(guildsIds: JsonNode) {
        guildsIds.forEach {
            val longId = it.asLong()

            variables.guildSettingsCache.invalidate(longId)
            variables.vcAutoRoleCache.remove(longId)
        }
    }
}
