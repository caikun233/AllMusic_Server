package com.coloryr.allmusic.server.side.fabric;

import com.coloryr.allmusic.server.AllMusicFabric;
import com.coloryr.allmusic.server.TaskItem;
import com.coloryr.allmusic.server.Tasks;
import com.coloryr.allmusic.server.codec.PacketCodec;
import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.objs.enums.ComType;
import com.coloryr.allmusic.server.core.objs.music.MusicObj;
import com.coloryr.allmusic.server.core.objs.music.SongInfoObj;
import com.coloryr.allmusic.server.core.side.BaseSide;
import com.coloryr.allmusic.server.mixin.IGetCommandOutput;
import com.coloryr.allmusic.server.side.fabric.event.MusicAddEvent;
import com.coloryr.allmusic.server.side.fabric.event.MusicPlayEvent;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Util;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

public class SideFabric extends BaseSide {

    @Override
    public void runTask(Runnable run) {
        AllMusicFabric.server.execute(run);
    }

    @Override
    public void runTask(Runnable run1, int delay) {
        Tasks.add(new TaskItem() {{
            tick = delay;
            run = run1;
        }});
    }

    @Override
    public boolean checkPermission(Object player) {
        ServerCommandSource source = (ServerCommandSource) player;
        return source.hasPermissionLevel(2);
    }

    @Override
    public boolean isPlayer(Object player) {
        ServerCommandSource source = (ServerCommandSource) player;
        return source.getEntity() instanceof ServerPlayerEntity;
    }

    @Override
    public boolean checkPermission(Object player, String permission) {
        return checkPermission(player);
    }

    @Override
    public Collection<?> getPlayers() {
        return AllMusicFabric.server.getPlayerManager().getPlayerList();
    }

    @Override
    public String getPlayerName(Object player) {
        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity player1 = (ServerPlayerEntity) player;
            return player1.getName().getString();
        }

        return null;
    }

    @Override
    public String getPlayerServer(Object player) {
        return null;
    }

    @Override
    public void send(Object player, ComType type, String data, int data1) {
        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity player1 = (ServerPlayerEntity) player;
            send(player1, PacketCodec.pack(type, data, data1));
        }
    }

    @Override
    public Object getPlayer(String player) {
        return null;
    }

    @Override
    public void sendBar(Object player, String data) {
        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity player1 = (ServerPlayerEntity) player;
            FabricApi.sendBar(player1, data);
        }
    }

    @Override
    public File getFolder() {
        return new File("allmusic/");
    }

    @Override
    public boolean needPlay(boolean islist) {
        for (ServerPlayerEntity player : AllMusicFabric.server.getPlayerManager().getPlayerList()) {
            if (!AllMusic.isSkip(player.getName().getString(), null, false, islist)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void broadcast(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        for (ServerPlayerEntity player : AllMusicFabric.server.getPlayerManager().getPlayerList()) {
            player.sendMessage(Text.of(message), false);
        }
    }

    @Override
    public void broadcastWithRun(String message, String end, String command) {
        if (message == null || message.isEmpty()) {
            return;
        }
        FabricApi.sendMessageBqRun(message, end, command);
    }

    @Override
    public void sendMessage(Object obj, String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        IGetCommandOutput output = (IGetCommandOutput) obj;
        if (!output.getSilent()) {
            output.getOutput().sendSystemMessage(Text.of(message), Util.NIL_UUID);
        }
    }

    @Override
    public void sendMessageRun(Object obj, String message, String end, String command) {
        if (message == null || message.isEmpty()) {
            return;
        }
        FabricApi.sendMessageRun(obj, message, end, command);
    }

    @Override
    public void sendMessageSuggest(Object obj, String message, String end, String command) {
        if (message == null || message.isEmpty()) {
            return;
        }
        FabricApi.sendMessageSuggest(obj, message, end, command);
    }

    @Override
    public boolean onMusicPlay(SongInfoObj obj) {
        return MusicPlayEvent.EVENT.invoker().interact(obj) != ActionResult.PASS;
    }

    @Override
    public boolean onMusicAdd(Object obj, MusicObj music) {
        ServerCommandSource sender = (ServerCommandSource) obj;
        ServerPlayerEntity entity = null;
        if (sender.getEntity() instanceof ServerPlayerEntity) {
            entity = (ServerPlayerEntity) sender.getEntity();
        }
        return MusicAddEvent.EVENT.invoker().interact(entity, music) != ActionResult.PASS;
    }

    private void send(ServerPlayerEntity players, ByteBuf data) {
        if (players == null)
            return;
        try {
            runTask(() -> ServerPlayNetworking.send(players, AllMusicFabric.ID, new PacketByteBuf(data)));
        } catch (Exception e) {
            AllMusic.log.warning("§c数据发送发生错误");
            e.printStackTrace();
        }
    }
}
