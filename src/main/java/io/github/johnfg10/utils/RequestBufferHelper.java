package io.github.johnfg10.utils;

import sx.blah.discord.Discord4J;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.obj.Channel;
import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.*;

import java.util.List;

/**
 * Created by johnfg10 on 28/03/2017.
 */
public class RequestBufferHelper {
    public static IMessage RequestBuffer(IChannel channel, String message){
        RequestBuffer.request(() -> {
            try {
                return channel.sendMessage(message);
            } catch (MissingPermissionsException e) {
                Discord4J.LOGGER.error("Missing permissions", e);
                return null;
            } catch (DiscordException e) {return null;}
        });
        return null;
    }

    public static void RequestBuffer(IChannel channel, String message, EmbedObject embedObject, boolean tts){
        RequestBuffer.request(() -> {
            try {
                channel.sendMessage(message, embedObject, tts);
            } catch (MissingPermissionsException e) {
                Discord4J.LOGGER.error("Missing permissions", e);
            } catch (DiscordException e) {}
        });
    }

    public static void RequestBuffer(IChannel channel, String message, EmbedBuilder embedBuilder, boolean tts){
        RequestBuffer.request(() -> {
            try {
                channel.sendMessage(message, embedBuilder.build(), tts);
            } catch (MissingPermissionsException e) {
                Discord4J.LOGGER.error("Missing permissions", e);
            } catch (DiscordException e) {}
        });
    }

    public static void RequestBufferDelete(IChannel channel, boolean all){
        if (all){
            MessageHistory messages = channel.getFullMessageHistory();
            RequestBuffer.request(() -> {
                try {
                    messages.bulkDelete();
                } catch (MissingPermissionsException e) {
                    Discord4J.LOGGER.error("Missing permissions", e);
                } catch (DiscordException e) {}
            });
        }
    }

    public static void RequestBufferDelete(IChannel channel, int amount){
        RequestBuffer.request(() -> {
            try {
/*                MessageList messages = channel.getMessages();
                messages.load(amount);
                for (int i = amount; i == 0; i--) {
                    messages.get(amount).delete();
                }*/

                MessageHistory messages = channel.getFullMessageHistory();

                for (int i = 0; i < amount; i++) {
                    messages.getLatestMessage().delete();
                }
            } catch (MissingPermissionsException e) {
                Discord4J.LOGGER.error("Missing permissions", e);
            } catch (DiscordException e) {}
        });
    }
}