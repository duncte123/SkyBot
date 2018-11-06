/*
 * Skybot, a multipurpose discord bot
 *      Copyright (C) 2017 - 2018  Duncan "duncte123" Sterken & Ramid "ramidzkh" Khan
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

package ml.duncte123.skybot.web.routes.api

import ml.duncte123.skybot.Author
import ml.duncte123.skybot.web.WebHolder
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.User
import org.apache.http.client.utils.URLEncodedUtils
import org.json.JSONObject
import spark.Spark.path
import spark.kotlin.post
import java.nio.charset.Charset

@Author(nickname = "duncte123", author = "Duncan Sterken")
class FindUserAndGuild(private val holder: WebHolder) {

    init {
        path("/api") {
            post("/checkUserAndGuild") {
                val pairs = URLEncodedUtils.parse(request.body(), Charset.defaultCharset())
                val params = holder.toMap(pairs)

                val userId = params["user_id"]
                val guildId = params["guild_id"]

                if (userId.isNullOrEmpty() || guildId.isNullOrEmpty()) {
                    response.status(406)
                    return@post JSONObject()
                        .put("status", "failure")
                        .put("message", "missing_input")
                        .put("code", response.status())
                }

                val user: User? = holder.shardManager.getUserById(userId)
                val guild: Guild? = holder.shardManager.getGuildById(guildId)

                if (user == null) {
                    response.status(404)
                    return@post JSONObject()
                        .put("status", "failure")
                        .put("message", "no_user")
                        .put("code", response.status())
                }

                if (guild == null) {
                    response.status(404)
                    return@post JSONObject()
                        .put("status", "failure")
                        .put("message", "no_guild")
                        .put("code", response.status())
                }

                val guildJson = JSONObject()
                    .put("id", guild.id)
                    .put("name", guild.name)

                val userJson = JSONObject()
                    .put("id", user.id)
                    .put("name", user.name)
                    .put("formatted", String.format("%#s", user))

                return@post JSONObject()
                    .put("status", "success")
                    .put("guild", guildJson)
                    .put("user", userJson)
                    .put("code", response.status())
            }
        }
    }

}
