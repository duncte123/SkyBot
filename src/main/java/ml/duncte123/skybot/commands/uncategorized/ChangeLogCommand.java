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

package ml.duncte123.skybot.commands.uncategorized;

import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.botcommons.web.WebUtils;
import ml.duncte123.skybot.Author;
import ml.duncte123.skybot.objects.command.Command;
import ml.duncte123.skybot.objects.command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;

import javax.annotation.Nonnull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed;

@Author(nickname = "duncte123", author = "Duncan Sterken")
public class ChangeLogCommand extends Command {

    private String embedJson = null;

    public ChangeLogCommand() {
        this.name = "changelog";
        this.helpFunction = (prefix, invoke) -> "Shows the latest changelog from the bot";
    }

    @Override
    public void execute(@Nonnull CommandContext ctx) {

        if (embedJson == null || embedJson.isEmpty()) {
            fetchLatetstGitHubCommits(ctx.getEvent());
            return;
        }

        final JDAImpl jda = (JDAImpl) ctx.getJDA();

        final MessageEmbed embed = jda.getEntityBuilder().createMessageEmbed(DataObject.fromJson(embedJson));

        sendEmbed(ctx.getEvent(), embed);
    }

    private void fetchLatetstGitHubCommits(GuildMessageReceivedEvent event) {
        WebUtils.ins.getJSONObject("https://api.github.com/repos/DuncteBot/SkyBot/releases/latest").async(json -> {
            final String body = json.get("body").asText();
            final EmbedBuilder eb = EmbedUtils.defaultEmbed()
                .setTitle("Changelog for DuncteBot", json.get("html_url").asText());

            for (final String item : body.split("\n")) {
                final String hash = item.substring(0, 7);
                final String text = item.substring(8);

                eb.appendDescription(String.format("[%s](http://g.entered.space/%s)%n", text, hash));
            }

            // fallback if with url is too long
            if (eb.getDescriptionBuilder().length() > MessageEmbed.TEXT_MAX_LENGTH) {
                eb.setDescription(body);
            }

            final MessageEmbed embed = eb.setFooter("Released at", null)
                .setTimestamp(Instant.ofEpochMilli(parseTimeStamp(json.get("published_at").asText())))
                .build();

            embedJson = embed.toData()
                .put("type", "rich")
                .toString();

            sendEmbed(event, embed);
        });
    }

    private long parseTimeStamp(String timestamp) {
        try {
            final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            final Date parsed = format.parse(timestamp);

            return parsed.getTime();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return 0L;
    }
}
