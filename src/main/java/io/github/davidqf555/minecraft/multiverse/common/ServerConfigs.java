package io.github.davidqf555.minecraft.multiverse.common;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ServerConfigs {

    public static final ServerConfigs INSTANCE;
    public static final ForgeConfigSpec SPEC;

    static {
        Pair<ServerConfigs, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ServerConfigs::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    public final ForgeConfigSpec.DoubleValue additionalBiomeTypeChance, fixedTimeChance;
    public final ForgeConfigSpec.IntValue maxDimensions, riftChance, boundlessBladeCooldown, riftRange, minRiftWidth, maxRiftWidth, minRiftHeight, maxRiftHeight;

    public ServerConfigs(ForgeConfigSpec.Builder builder) {
        builder.push("Server config for Multiverse mod");
        boundlessBladeCooldown = builder.comment("This is the cooldown of the Boundless Blade item in ticks. ")
                .defineInRange("boundlessBladeCooldown", 500, 0, Integer.MAX_VALUE);
        builder.push("Multiverse dimensions");
        maxDimensions = builder.comment("This is the number of Multiverse dimensions that rifts will generate for. ")
                .defineInRange("max", 25, 1, Integer.MAX_VALUE);
        additionalBiomeTypeChance = builder.comment("Each additional biome type has this chance to be in new Multiverse dimensions. ")
                .defineInRange("biomeTypeChance", 0.025, 0, 1);
        fixedTimeChance = builder.comment("This is the chance that a Multiverse dimension has a random, fixed time. ")
                .defineInRange("fixedTimeChance", 0.25, 0, 1);
        builder.pop();
        builder.push("Rifts");
        riftChance = builder.comment("This is the chance a rift will generate. Increasing it will cause less rifts to generate. Specifically, each rift has a reciprocal of this value chance to generate per chunk. ")
                .defineInRange("chance", 20, 1, Integer.MAX_VALUE);
        riftRange = builder.comment("This is the range that is scanned for existing rifts. ")
                .defineInRange("range", 128, 0, Integer.MAX_VALUE);
        minRiftWidth = builder.comment("This is the minimum width radius of naturally generated rifts. ")
                .defineInRange("minWidth", 1, 0, Integer.MAX_VALUE);
        maxRiftWidth = builder.comment("This is the maximum width radius of naturally generated rifts. This should be greater or equal to minWidth. ")
                .defineInRange("maxWidth", 3, 0, Integer.MAX_VALUE);
        minRiftHeight = builder.comment("This is the minimum height radius of naturally generated rifts. ")
                .defineInRange("minHeight", 6, 0, Integer.MAX_VALUE);
        maxRiftHeight = builder.comment("This is the maximum height radius of naturally generated rifts. This should be greater or equal to minHeight. ")
                .defineInRange("maxHeight", 10, 0, Integer.MAX_VALUE);
        builder.pop(2);
    }
}
