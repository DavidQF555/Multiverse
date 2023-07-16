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

    public final ForgeConfigSpec.DoubleValue additionalBiomeTypeChance, fixedTimeChance, fabricOfReailtyChance;
    public final ForgeConfigSpec.IntValue maxDimensions, boundlessBladeCooldown, riftRange;
    public final ForgeConfigSpec.BooleanValue mixedBiomes;

    public ServerConfigs(ForgeConfigSpec.Builder builder) {
        builder.push("Server config for Multiverse mod");
        boundlessBladeCooldown = builder.comment("This is the cooldown of the Boundless Blade item in ticks. ")
                .defineInRange("boundlessBladeCooldown", 500, 0, Integer.MAX_VALUE);
        builder.push("Multiverse dimensions");
        mixedBiomes = builder.comment("When set to true, multiverse dimensions with biomes from different dimensions may appear. However, they load much slower and cause more lag. ")
                .define("mixedBiomes", false);
        maxDimensions = builder.comment("This is the number of Multiverse dimensions that rifts will generate for. ")
                .defineInRange("max", 25, 1, Integer.MAX_VALUE);
        additionalBiomeTypeChance = builder.comment("Each additional biome type has this chance to be in new Multiverse dimensions. ")
                .defineInRange("biomeTypeChance", 0.025, 0, 1);
        fixedTimeChance = builder.comment("This is the chance that a Multiverse dimension has a random, fixed time. ")
                .defineInRange("fixedTimeChance", 0.25, 0, 1);
        builder.pop();
        builder.push("Rifts");
        fabricOfReailtyChance = builder.comment("This is the chance of a Fabric of Reality dropping whenever a rift opens. ")
                .defineInRange("fabricChance", 0.05, 0, 1);
        riftRange = builder.comment("This is the range that is scanned for existing rifts. ")
                .defineInRange("range", 128, 0, Integer.MAX_VALUE);
        builder.pop(2);
    }
}
