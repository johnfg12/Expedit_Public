package io.github.johnfg10.handlers;

import io.github.johnfg10.ExpeditConst;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by johnfg10 on 30/03/2017.
 */
public class onUserVoiceChannelLeaveEvent implements IListener<UserVoiceChannelLeaveEvent> {
    @Override
    public void handle(UserVoiceChannelLeaveEvent event) {
        try {
            if (event.getVoiceChannel().getName().matches(ExpeditConst.databaseUtils.getSetting("musicVoice", event.getVoiceChannel().getGuild().getID())) && event.getVoiceChannel().isConnected()) {
                if (event.getVoiceChannel().getGuild().getVoiceChannelByID(event.getVoiceChannel().getID()).getConnectedUsers().size() <=  1){
                   ExpeditConst.audioHelper.getGuildAudioPlayer(event.getVoiceChannel().getGuild()).player.setPaused(true);
                   event.getVoiceChannel().leave();
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
