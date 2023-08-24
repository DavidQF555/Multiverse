package io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes;

public final class MultiverseBiomeTagsRegistry {

    private static MultiverseBiomes biomes = VanillaMultiverseBiomes.INSTANCE;

    private MultiverseBiomeTagsRegistry() {
    }

    public static MultiverseBiomes getMultiverseBiomes() {
        return biomes;
    }

    public static void setMultiverseBiomes(MultiverseBiomes biomes) {
        MultiverseBiomeTagsRegistry.biomes = biomes;
    }

}
