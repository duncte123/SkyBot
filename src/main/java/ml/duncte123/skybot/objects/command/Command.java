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

package ml.duncte123.skybot.objects.command;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import ml.duncte123.skybot.Author;
import ml.duncte123.skybot.Authors;
import ml.duncte123.skybot.CommandManager;
import ml.duncte123.skybot.objects.CooldownScope;
import net.dv8tion.jda.api.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static me.duncte123.botcommons.messaging.MessageUtils.*;
import static ml.duncte123.skybot.utils.AirUtils.parsePerms;

@SuppressWarnings("SameParameterValue")
@Authors(authors = {
    @Author(nickname = "Sanduhr32", author = "Maurice R S"),
    @Author(nickname = "duncte123", author = "Duncan Sterken"),
    @Author(nickname = "ramidzkh", author = "Ramid Khan")
})
public abstract class Command implements ICommand {
    protected static final Logger logger = LoggerFactory.getLogger(Command.class);
    // The size should match the usage for stability but not more than 4.
    protected static final ScheduledExecutorService commandService = Executors.newScheduledThreadPool(3,
        (r) -> {
            final Thread thread = new Thread(r, "Command-Thread");
            thread.setDaemon(true);
            return thread;
        });

    protected boolean requiresArgs = false;
    protected int requiredArgCount = 1;
    protected boolean displayAliasesInHelp = false;
    protected CommandCategory category = CommandCategory.MAIN;
    protected String name = "null";
    protected String[] aliases = new String[0];
    protected String help = "";
    protected String usage = "";
    protected Permission[] userPermissions = new Permission[0];
    protected Permission[] botPermissions = new Permission[0];
    public Flag[] flags = new Flag[0];
    // This is the cooldown in seconds
    protected int cooldown = 0;
    // Sets the scope of the cooldown, default is on user level
    protected CooldownScope cooldownScope = CooldownScope.USER;
    // default key is <command name>|<user id>
    protected Function2<String, CommandContext, String> cooldownKey = cooldownScope::formatKey;
    // Can be used for when patrons have no cooldown on commands
    // Default is false
    protected Function1<CommandContext, Boolean> overridesCooldown = (ctx) -> false;

    @Override
    public void executeCommand(@Nonnull CommandContext ctx) {
        if (this.userPermissions.length > 0 && !ctx.getMember().hasPermission(ctx.getChannel(), this.userPermissions)) {
            final String permissionsWord = "permission" + (this.userPermissions.length > 1 ? "s" : "");

            sendMsgFormat(ctx,
                "You need the `%s` %s for this command\nPlease contact your server administrator if this is incorrect.",
                parsePerms(this.userPermissions), permissionsWord
            );

            return;
        }

        if (this.botPermissions.length > 0 && !ctx.getSelfMember().hasPermission(ctx.getChannel(), this.botPermissions)) {
            final String permissionsWord = "permission" + (this.botPermissions.length > 1 ? "s" : "");

            sendMsgFormat(ctx,
                "I need the `%s` %s for this command to work\nPlease contact your server administrator about this.",
                parsePerms(this.botPermissions), permissionsWord
            );

            return;
        }

        if (this.requiresArgs &&
            // if args are empty or the args count is less than the required args count
            (ctx.getArgs().isEmpty() || ctx.getArgs().size() < this.requiredArgCount)
        ) {
            sendMsg(ctx, "Missing arguments, usage: " + this.getUsageInstructions(ctx));
            return;
        }

        // If we have a cooldown and the event does not override it
        if (this.cooldown > 0 && !this.overridesCooldown.invoke(ctx)) {
            // Get the cooldown key for this command
            final String cooldownKey = getCooldownKey(ctx);
            final CommandManager commandManager = ctx.getCommandManager();
            final long remainingCooldown = commandManager.getRemainingCooldown(cooldownKey);

            if (remainingCooldown > 0) {
                // TODO: delete after?
                sendMsgFormat(
                    ctx,
                    "This command is on cooldown for %s more seconds%s!",
                    remainingCooldown,
                    this.cooldownScope.getExtraErrorMsg()
                );
                sendError(ctx.getMessage());
                return;
            } else {
                // Set the cooldown for the command
                commandManager.setCooldown(cooldownKey, this.cooldown);
            }
        }

        execute(ctx);
    }

    public abstract void execute(@Nonnull CommandContext ctx);

    @Nonnull
    @Override
    public String getName() {
        return this.name;
    }

    @Nonnull
    @Override
    public final String[] getAliases() {
        return this.aliases;
    }

    @Nonnull
    @Override
    public final String help(@Nonnull String invoke, @Nonnull String prefix) {
        return this.help.replace("{prefix}", prefix).trim();
//        return this.helpFunction.invoke(prefix, invoke);
    }

    @Override
    public final boolean shouldDisplayAliasesInHelp() {
        return this.displayAliasesInHelp;
    }

    @Nonnull
    public CommandCategory getCategory() {
        return this.category;
    }

    @Nonnull
    public String getUsageInstructions(@Nonnull String invoke, @Nonnull String prefix) {
        return '`' + prefix + invoke + ' ' + this.usage.replace("{prefix}", prefix).trim() + '`';
//        return this.usageInstructions.invoke(prefix, invoke);
    }

    @Nonnull
    public String getUsageInstructions(CommandContext ctx) {
        // Why are these inverted?
        return this.getUsageInstructions(ctx.getInvoke(), ctx.getPrefix());
//        return this.usageInstructions.invoke(ctx.getPrefix(), ctx.getInvoke());
    }

    protected void sendUsageInstructions(CommandContext ctx) {
        sendMsg(ctx, "Usage: " + this.getUsageInstructions(ctx));
    }

    @Override
    public String toString() {
        return "Command[" + this.name + ']';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Command)) {
            return false;
        }

        final Command command = (Command) obj;

        return this.name.equals(command.getName());
    }

    private String getCooldownKey(CommandContext ctx) {
        return this.cooldownKey.invoke(this.name, ctx);
    }
}
