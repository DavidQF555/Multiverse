package io.github.davidqf555.minecraft.multiverse.client;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class ClientHelper {

    private ClientHelper() {
    }

    public static void addDimension(ResourceKey<Level> key) {
        Minecraft.getInstance().player.connection.levels().add(key);
    }
}
