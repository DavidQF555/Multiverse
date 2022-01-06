package io.github.davidqf555.minecraft.multiverse;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("multiverse")
public class Multiverse {

    public static final String MOD_ID = "multiverse";

    public Multiverse() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
