package io.github.davidqf555.minecraft.multiverse.client;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ClientConfigs {

    public static final ClientConfigs INSTANCE;
    public static final ForgeConfigSpec SPEC;

    static {
        Pair<ClientConfigs, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ClientConfigs::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    public final ForgeConfigSpec.IntValue riftLayers;
    public final ForgeConfigSpec.DoubleValue riftZOffset, riftLayerStart, riftLayerGrowth, riftMinOpacity, riftMaxOpacity;
    public final ForgeConfigSpec.BooleanValue vanillaOnly;

    public ClientConfigs(ForgeConfigSpec.Builder builder) {
        builder.comment("Multiverse client-side configuration");
        riftLayers = builder.comment("This is the number of layers to render rifts with")
                .defineInRange("riftLayers", 3, 1, Integer.MAX_VALUE);
        riftZOffset = builder.comment("This is the offset in blocks for each rift layer to combat Z-fighting. ")
                .defineInRange("riftZOffset", 0.0005, 0, Double.MAX_VALUE);
        riftLayerStart = builder.comment("This is the fraction of the width/height of the rift that the first layer starts. ")
                .defineInRange("riftLayerStart", 0.85, 0, 1);
        riftLayerGrowth = builder.comment("This is the rate that the distance between rift layer grows using an exponential function. ")
                .defineInRange("riftLayerGrowth", 0.5, 0, Double.MAX_VALUE);
        riftMinOpacity = builder.comment("This is the minimum opacity of a rift's layers as a fraction. ")
                .defineInRange("riftMinOpacity", 0.5, 0, 1);
        riftMaxOpacity = builder.comment("This is the maximum opacity of a rift's layers as a fraction. Must be at least riftMinOpacity. ")
                .defineInRange("riftMaxOpacity", 1.0, 0, 1);
        vanillaOnly = builder.comment("This is whether only vanilla shaders are used to render. Try setting to true if rifts are invisible due to custom shaders. ")
                .define("vanillaOnly", false);
    }

}
