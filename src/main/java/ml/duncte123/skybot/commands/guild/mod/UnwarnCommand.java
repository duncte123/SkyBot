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

package ml.duncte123.skybot.commands.guild.mod;

import ml.duncte123.skybot.entities.jda.DunctebotGuild;
import ml.duncte123.skybot.objects.command.CommandContext;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import javax.annotation.Nonnull;
import java.util.List;

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;
import static me.duncte123.botcommons.messaging.MessageUtils.sendMsgFormat;
import static ml.duncte123.skybot.utils.ModerationUtils.modLog;

public class UnwarnCommand extends ModBaseCommand {
    public UnwarnCommand() {
        this.perms = new Permission[]{
            Permission.KICK_MEMBERS,
        };
    }

    @Override
    public void run(@Nonnull CommandContext ctx) {
        final List<Member> mentioned = ctx.getMentionedMembers();

        if (mentioned.isEmpty()) {
            sendMsg(ctx, "No users found for query");

            return;
        }

        final DunctebotGuild guild = ctx.getGuild();
        final User target = mentioned.get(0).getUser();

        ctx.getDatabaseAdapter().deleteLatestWarningForUser(
            target.getIdLong(),
            guild.getIdLong(),
            (latestWarning) -> {
                if (latestWarning == null) {
                    sendMsg(ctx, "This user has no active warnings");

                    return null;
                }

                sendMsgFormat(ctx, "Latest warning for _%s_ removed\nReason was: %s", target.getAsTag(), latestWarning.getReason());
                modLog(String.format(
                    "**%s** removed the latest warning for **%s**\nReason was: %s",
                    ctx.getAuthor().getAsTag(),
                    target.getAsTag(),
                    latestWarning.getReason()
                ), guild);

                return null;
            });
    }

    @Override
    public String getName() {
        return "unwarn";
    }

    @Override
    public String help(String prefix) {
        return "Removes the latest warning of a user\n" +
            "Usage: `" + prefix + "unwarn <@user>`";
    }
}