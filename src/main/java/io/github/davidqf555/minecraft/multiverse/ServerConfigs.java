package io.github.davidqf555.minecraft.multiverse;

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

    public final ForgeConfigSpec.ConfigValue<Double> additionalBiomeTypeChance;
    public final ForgeConfigSpec.ConfigValue<Double> fixedTimeChance;
    public final ForgeConfigSpec.ConfigValue<Boolean> inverse;

    public ServerConfigs(ForgeConfigSpec.Builder builder) {
        builder.push("Server config for Multiverse mod");
        additionalBiomeTypeChance = builder.comment("Each additional biome type has this chance to be in new Multiverse dimensions. ")
                .defineInRange("Additional Biome Type Chance", 0.1, 0, 1);
        fixedTimeChance = builder.comment("This is the chance that a Multiverse dimension has a random, fixed time. ")
                .defineInRange("Fixed Time Chance", 0.25, 0, 1);
        inverse = builder.comment("This is whether Multiverse dimensions can generate worlds with a ceiling but no floor. Defaulted to false because it is extremely difficult and painful to navigate in these worlds. ")
                .define("Inverse", false);
        builder.pop();
    }
}
