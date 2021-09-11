package fr.cleymax.signgui;

import com.google.common.base.Preconditions;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftSign;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;

import java.beans.ConstructorProperties;

/**
 * File <b>SignGUI</b> located on fr.cleymax.signgui SignGUI is a part of SignGUI.
 * <p>
 * Copyright (c) 2019 SignGUI .
 * <p>
 *
 * @author Clément P. (Cleymax), {@literal <cleymaxpro@gmail.com>} Created the 11/09/2019 at 23:32
 */

public final class SignGUI {

    private final SignManager signManager;
    private final SignClickCompleteHandler completeHandler;
    private Player player;
    private String[] lines;

    @ConstructorProperties({"signManager", "completeHandler"})
    public SignGUI(SignManager signManager, SignClickCompleteHandler completeHandler) {
        this.signManager = signManager;
        this.completeHandler = completeHandler;
        this.lines = new String[4];
        this.player = null;
    }

    public SignGUI withLines(String... lines) {
        if (lines.length != 4) {
            throw new IllegalArgumentException("Must have at least 4 lines");
        }

        this.lines = lines;
        return this;
    }

    public void open(Player player) {
        this.player = player;

        final var blockPosition = new BlockPosition(player.getLocation().getBlockX(), 1, player.getLocation().getBlockZ());

        var packet = new PacketPlayOutBlockChange((World) player.getWorld(), blockPosition);
        sendPacket(packet);

        IChatBaseComponent[] components = CraftSign.sanitizeLines(lines);
        var sign = new TileEntitySign();

        for (var i = 0; i < components.length; i++)
            sign.lines[i] = (components[i]);

        sendPacket(sign.getUpdatePacket());

        var outOpenSignEditor = new PacketPlayOutOpenSignEditor(blockPosition);
        sendPacket(outOpenSignEditor);
        this.signManager.addGui(player.getUniqueId(), this);
    }

    private void sendPacket(Packet<?> packet) {
        Preconditions.checkNotNull(this.player);
        ((CraftPlayer) this.player).getHandle().playerConnection.sendPacket(packet);
    }

    SignClickCompleteHandler getCompleteHandler() {
        return this.completeHandler;
    }
}
