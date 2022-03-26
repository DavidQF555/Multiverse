package io.github.davidqf555.minecraft.multiverse.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

public final class ClientHelper {

    private ClientHelper() {
    }

    public static void addDimension(RegistryKey<World> key) {
        Minecraft.getInstance().player.connection.levels().add(key);
    }
}
