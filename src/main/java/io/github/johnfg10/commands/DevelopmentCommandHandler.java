package io.github.johnfg10.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import io.github.johnfg10.ExpeditConst;
import io.github.johnfg10.utils.RequestBufferHelper;
import io.github.johnfg10.utils.StringHelper;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GitHub;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created by johnfg10 on 17/04/2017.
 */
public class DevelopmentCommandHandler implements CommandExecutor {
    @Command(aliases = {"debuginfo"})
    public void onCommandMyUserID(IMessage message, IUser user, IGuild guild, IChannel channel, String command, String[] args) throws RateLimitException, DiscordException, MissingPermissionsException {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.appendField("Guild ID", guild.getStringID(), false);
        embedBuilder.appendField("Channel ID", channel.getStringID(), false);
        embedBuilder.appendField("User ID", user.getStringID(), false);

        RequestBufferHelper.RequestBuffer(channel, "", embedBuilder.build(), false);
    }

    @Command(aliases = {"leaveall"}, showInHelpPage = false)
    public void onCommandLeaveAllConnectedVoiceChannels(IMessage message, IUser user, IGuild guild, IChannel channel, String command, String[] args) throws RateLimitException, DiscordException, MissingPermissionsException {
        //my id
        if (user.getLongID() == 200989665304641536L) {
            for (IVoiceChannel iVoiceChannel : ExpeditConst.iDiscordClient.getVoiceChannels()) {
                iVoiceChannel.leave();
            }
        }
    }

    @Command(aliases = {"memstat"}, showInHelpPage = false)
    public void onCommandMemStat(IMessage message, IUser user, IGuild guild, IChannel channel, String command, String[] args) throws RateLimitException, DiscordException, MissingPermissionsException {
        //my id
        if (user.getLongID() == 200989665304641536L) {
            long totalMemory = Runtime.getRuntime().totalMemory() / 1000000;
            long freeMemory = Runtime.getRuntime().freeMemory() / 1000000;
            long maxMemory = Runtime.getRuntime().maxMemory() / 1000000;
            long usedMemory = maxMemory - freeMemory;
            long percentageUsed = (usedMemory*100)/maxMemory;

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.withTitle("Memory stats");
            embedBuilder.appendField("Total memory available ", String.valueOf(totalMemory) + "MB's", false );
            embedBuilder.appendField("Free memory available ", String.valueOf(freeMemory) + "MB's", false );
            embedBuilder.appendField("Max memory available ", String.valueOf(maxMemory) + "MB's", false );
            embedBuilder.appendField("Used memory ", String.valueOf(usedMemory) + "MB's", false );
            embedBuilder.appendField("Memory % used ", String.valueOf(percentageUsed) + '%', false );

            channel.sendMessage(embedBuilder.build());
        }
    }

    @Command(aliases = {"rungc"}, showInHelpPage = false)
    public void onCommandRunGC(IMessage message, IUser user, IGuild guild, IChannel channel, String command, String[] args) throws RateLimitException, DiscordException, MissingPermissionsException {
        System.gc();
        channel.sendMessage("Requested that the JVM run a force FC");
    }

        @Command(aliases = {"warnall"}, showInHelpPage = false)
    public void onCommandWarnAll(IMessage message, IUser user, IGuild guild, IChannel channel, String command, String[] args) throws RateLimitException, DiscordException, MissingPermissionsException {
        //my id
        if (user.getLongID() == 200989665304641536L) {
            for (IGuild g : ExpeditConst.iDiscordClient.getGuilds()) {
                RequestBufferHelper.RequestBuffer(g.getChannels().get(0), StringHelper.arrayToString(args));
            }
        }
    }

    @Command(aliases = {"msgowners"}, showInHelpPage = false)
    public void onCommandOwners(IMessage message, IUser user, IGuild guild, IChannel channel, String command, String[] args) throws RateLimitException, DiscordException, MissingPermissionsException {
        //my id
        if (user.getLongID() == 200989665304641536L) {
            for (IGuild g : ExpeditConst.iDiscordClient.getGuilds()) {
                RequestBufferHelper.RequestBuffer(g.getOwner().getOrCreatePMChannel(), StringHelper.arrayToString(args));
            }
        }
    }

    @Command(aliases = {"ping"}, description = "Pong!", async = true)
    public void onCommandPing(IMessage message, IUser user, IGuild guild, IChannel channel, String command, String[] args) throws RateLimitException, DiscordException, MissingPermissionsException {
        IMessage msg = channel.sendMessage("Pong!");
        LocalDateTime firstTime = message.getTimestamp();
        LocalDateTime secondTime = msg.getTimestamp();
        RequestBufferHelper.RequestBuffer(channel, "That took " + ChronoUnit.MICROS.between(firstTime, secondTime) + " micro seconds");
    }



    @Command(aliases = {"reportbug"}, description = "Reports a bug!")
    public void onCommandReportBug(IMessage message, IUser user, IGuild guild, IChannel channel, String command, String[] args) throws RateLimitException, DiscordException, MissingPermissionsException {
        if (ExpeditConst.githubBlackListedUsers.contains(user.getStringID())) {
            RequestBufferHelper.RequestBuffer(channel, "you have been blacklisted from using this command if you belive this is in error please contact johnfg10");
        } else {
            try {
                GitHub github = GitHub.connectUsingPassword(ExpeditConst.configSettings.getNode().getNode("token", "githublogin").getString(), ExpeditConst.configSettings.getNode().getNode("token", "githubpass").getString());
                //System.out.println(github.createGist().description("test").file("test", "testing stuff").create().getUrl());
                GHIssue issue = github.getUser("johnfg10").getRepository("Expedit_Public").createIssue(user.getName() + "s Bug report").body(StringHelper.arrayToString(args) +
                        "\n" +
                        "additional information:" +
                        "\nGuild ID: " +
                        guild.getStringID() +
                        "\nChannel ID: " +
                        channel.getStringID() +
                        "\nUser ID:" +
                        user.getStringID()
                ).assignee("johnfg10").create();

                RequestBufferHelper.RequestBuffer(channel, "link: " + issue.getHtmlUrl());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
