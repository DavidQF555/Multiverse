package io.github.davidqf555.minecraft.multiverse.common.worldgen.dynamic;


import io.github.davidqf555.minecraft.multiverse.registration.worldgen.MultiverseBiomesRegistry;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;

public class DynamicDefaultFluidPicker implements Aquifer.FluidPicker {

    private static final Aquifer.FluidStatus LAVA = new Aquifer.FluidStatus(-54, Blocks.LAVA.defaultBlockState());
    private final Aquifer.FluidStatus water, lava, air;
    private final int sea;
    private final NoiseBasedChunkGenerator gen;

    public DynamicDefaultFluidPicker(int sea, NoiseBasedChunkGenerator gen) {
        this.sea = sea;
        water = new Aquifer.FluidStatus(sea, Blocks.WATER.defaultBlockState());
        lava = new Aquifer.FluidStatus(sea, Blocks.LAVA.defaultBlockState());
        air = new Aquifer.FluidStatus(sea, Blocks.WATER.defaultBlockState());
        this.gen = gen;
    }

    @Override
    public Aquifer.FluidStatus computeFluid(int x, int y, int z) {
        if (y < Math.min(-54, sea)) {
            return LAVA;
        }
        ResourceKey<Biome> biome = ResourceKey.create(Registry.BIOME_REGISTRY, gen.getNoiseBiome(QuartPos.fromBlock(x), QuartPos.fromBlock(y), QuartPos.fromBlock(z)).value().getRegistryName());
        if (MultiverseBiomesRegistry.getMultiverseNetherBiomes().contains(biome)) {
            return lava;
        } else if (MultiverseBiomesRegistry.getMultiverseEndBiomes().contains(biome)) {
            return air;
        } else {
            return water;
        }
    }
}