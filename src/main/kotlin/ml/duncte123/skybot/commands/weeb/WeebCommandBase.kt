/*
 * Skybot, a multipurpose discord bot
 *      Copyright (C) 2017 - 2019  Duncan "duncte123" Sterken & Ramid "ramidzkh" Khan
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

package ml.duncte123.skybot.commands.weeb

import me.duncte123.botcommons.messaging.EmbedUtils
import me.duncte123.botcommons.messaging.MessageUtils.sendEmbed
import me.duncte123.weebJava.models.WeebApi
import ml.duncte123.skybot.Author
import ml.duncte123.skybot.objects.command.Command
import ml.duncte123.skybot.objects.command.CommandCategory
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

@Author(nickname = "duncte123", author = "Duncan Sterken")
abstract class WeebCommandBase : Command() {
    init {
        this.displayAliasesInHelp = true
        this.category = CommandCategory.WEEB
    }

    private fun getDefaultWeebEmbed(): EmbedBuilder {
        return EmbedUtils.defaultEmbed()
            .setFooter("Powered by weeb.sh", null)
            .setTimestamp(null)
    }

    protected fun getWeebEmbedImageAndDesc(description: String, imageUrl: String): MessageEmbed {
        return getDefaultWeebEmbed().setDescription(description).setImage(imageUrl).build()
    }

    protected fun getWeebEmbedImage(imageUrl: String): MessageEmbed {
        return getDefaultWeebEmbed().setImage(imageUrl).build()
    }

    protected fun requestAndSend(type: String, thing: String, args: List<String>, event: GuildMessageReceivedEvent, weebApi: WeebApi) {
        weebApi.getRandomImage(type).async {
            val imageUrl = it.url
            if (args.isEmpty()) {
                sendEmbed(event, getWeebEmbedImageAndDesc(
                    "<@210363111729790977> $thing ${event.member!!.asMention}", imageUrl))
                return@async
            }
            if (!event.message.mentionedMembers.isNotEmpty()) {
                sendEmbed(event, getWeebEmbedImageAndDesc(
                    "${event.member!!.asMention} $thing ${event.message.mentionedMembers[0].asMention}"
                    , imageUrl))
                return@async
            }
            sendEmbed(event, getWeebEmbedImageAndDesc(
                "${event.member!!.asMention} $thing ${args.joinToString(" ")}", imageUrl))
        }

    }
}
