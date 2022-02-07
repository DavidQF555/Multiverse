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

    public final ForgeConfigSpec.ConfigValue<Integer> maxDimensions;
    public final ForgeConfigSpec.ConfigValue<Double> additionalBiomeTypeChance;
    public final ForgeConfigSpec.ConfigValue<Double> fixedTimeChance;
    public final ForgeConfigSpec.ConfigValue<Boolean> inverse;
    public final ForgeConfigSpec.ConfigValue<Integer> riftChance;
    public final ForgeConfigSpec.ConfigValue<Integer> dimensionSlasherCooldown;
    public final ForgeConfigSpec.ConfigValue<Integer> riftRange;

    public ServerConfigs(ForgeConfigSpec.Builder builder) {
        builder.push("Server config for Multiverse mod");
        maxDimensions = builder.comment("This is the number of Multiverse dimensions that rifts will generate for. ")
                .defineInRange("maxDimensions", 25, 1, Integer.MAX_VALUE);
        additionalBiomeTypeChance = builder.comment("Each additional biome type has this chance to be in new Multiverse dimensions. ")
                .defineInRange("biomeTypeChance", 0.1, 0, 1);
        fixedTimeChance = builder.comment("This is the chance that a Multiverse dimension has a random, fixed time. ")
                .defineInRange("fixedTimeChance", 0.25, 0, 1);
        inverse = builder.comment("This is whether Multiverse dimensions can generate worlds with a ceiling but no floor. Defaulted to false because it is extremely difficult and painful to navigate in these worlds. ")
                .define("inverse", false);
        riftChance = builder.comment("This is the chance a rift will generate. Increasing it will cause less rifts to generate. Specifically, each rift has a reciprocal of this value chance to generate per chunk. ")
                .defineInRange("riftChance", 20, 1, Integer.MAX_VALUE);
        dimensionSlasherCooldown = builder.comment("This is the cooldown of the dimension slasher item in ticks. ")
                .defineInRange("dimensionSlasherCooldown", 500, 0, Integer.MAX_VALUE);
        riftRange = builder.comment("This is the range that is scanned for existing rifts. ")
                .defineInRange("riftRange", 128, 0, Integer.MAX_VALUE);
        builder.pop();
    }
}
