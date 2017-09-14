package ml.duncte123.skybot.commands.music;

import ml.duncte123.skybot.audio.GuildMusicManager;
import ml.duncte123.skybot.audio.TrackScheduler;
import ml.duncte123.skybot.objects.command.Command;
import ml.duncte123.skybot.utils.AirUtils;
import ml.duncte123.skybot.utils.AudioUtils;
import ml.duncte123.skybot.utils.Config;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class RepeatCommand extends Command {

    /**
     * This is the executeCommand of the command, the thing you want the command to to needs to be in here
     * @param args The command agruments
     * @param event a instance of {@link net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent GuildMessageReceivedEvent}
     */
    @Override
    public void executeCommand(String[] args, GuildMessageReceivedEvent event) {

        if(!event.getGuild().getAudioManager().isConnected()){
            event.getChannel().sendMessage(AirUtils.embedField(AirUtils.au.embedTitle, "I'm not in a voice channel, use `"+Config.prefix+"join` to make me join a channel")).queue();
            return;
        }

        if(!event.getGuild().getAudioManager().getConnectedChannel().getMembers().contains(event.getMember())){
            event.getChannel().sendMessage(AirUtils.embedField(AirUtils.au.embedTitle, "I'm sorry, but you have to be in the same channel as me to use any music related commands")).queue();
            return;
        }

        AudioUtils au = AirUtils.au;

        Guild guild = event.getGuild();
        GuildMusicManager mng = au.getMusicManager(guild);
        TrackScheduler scheduler = mng.scheduler;

        scheduler.setRepeating(!scheduler.isRepeating());

        event.getChannel().sendMessage(AirUtils.embedField(au.embedTitle, "Player was set to: **" + (scheduler.isRepeating() ? "repeat" : "not repeat") + "**")).queue();

    }

    /**
     * The usage instructions of the command
     * @return a String
     */
    @Override
    public String help() {
        // TODO Auto-generated method stub
        return "Makes the player repeat the currently playing song";
    }

    @Override
    public String getName() {
        return "repeat";
    }

}
