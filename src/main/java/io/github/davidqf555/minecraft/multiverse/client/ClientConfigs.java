package io.github.davidqf555.minecraft.multiverse.client;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ClientConfigs {

    public static final ClientConfigs INSTANCE;
    public static final ModConfigSpec SPEC;

    static {
        Pair<ClientConfigs, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(ClientConfigs::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    public final ModConfigSpec.IntValue riftLayers, riftRenderDistance, riftExplosionParticles;
    public final ModConfigSpec.DoubleValue riftZOffset, riftLayerStart, riftLayerGrowth, riftMinOpacity, riftMaxOpacity, riftExplosionParticleRange, riftSoundFrequency, riftParticleMax, riftParticleRate;
    public final ModConfigSpec.BooleanValue vanillaOnly, multicolor;

    public ClientConfigs(ModConfigSpec.Builder builder) {
        builder.comment("Multiverse client-side configuration");
        riftLayers = builder.comment("This is the number of layers to render rifts with")
                .defineInRange("riftLayers", 3, 1, Integer.MAX_VALUE);
        riftZOffset = builder.comment("This is the offset in blocks for each rift layer to combat Z-fighting. ")
                .defineInRange("riftZOffset", 0.0005, 0, Double.MAX_VALUE);
        riftLayerStart = builder.comment("This is the fraction of the width/height of the rift that the first layer starts. ")
                .defineInRange("riftLayerStart", 0.75, 0, 1);
        riftLayerGrowth = builder.comment("This is the rate that the distance between rift layer grows using an exponential function. ")
                .defineInRange("riftLayerGrowth", 0.5, 0, Double.MAX_VALUE);
        riftMinOpacity = builder.comment("This is the minimum opacity of a rift's layers as a fraction. ")
                .defineInRange("riftMinOpacity", 0.65, 0, 1);
        riftMaxOpacity = builder.comment("This is the maximum opacity of a rift's layers as a fraction. Must be at least riftMinOpacity. ")
                .defineInRange("riftMaxOpacity", 1.0, 0, 1);
        riftRenderDistance = builder.comment("This is the distance in blocks that rift blocks are rendered. ")
                .defineInRange("riftRenderDistance", 256, 0, Integer.MAX_VALUE);
        riftExplosionParticleRange = builder.comment("This is the max distance in blocks that rift explosion particles will spawn from the center. ")
                .defineInRange("riftExplosionParticleRange", 4, 0, Double.MAX_VALUE);
        riftExplosionParticles = builder.comment("This is the number of rift explosion particles spawned per tick. ")
                .defineInRange("riftExplosionParticles", 6, 0, Integer.MAX_VALUE);
        riftSoundFrequency = builder.comment("This is the chance that each rift block plays a sound per tick. ")
                .defineInRange("riftSoundFrequency", 0.005, 0, 1);
        riftParticleMax = builder.comment("This is the max distance in blocks from a rift that particles will spawn. ")
                .defineInRange("riftParticleMax", 1, 0, Double.MAX_VALUE);
        riftParticleRate = builder.comment("This is the chance that a rift particle spawns per tick for every rift block surface area in. ")
                .defineInRange("riftParticleRate", 0.75, 0, Double.MAX_VALUE);
        multicolor = builder.comment("This is whether rifts use multiple colors. ")
                .define("multicolor", true);
        vanillaOnly = builder.comment("This is whether only vanilla shaders are used to render. Try setting to true if rifts are invisible due to custom shaders. ")
                .define("vanillaOnly", false);
    }

}
