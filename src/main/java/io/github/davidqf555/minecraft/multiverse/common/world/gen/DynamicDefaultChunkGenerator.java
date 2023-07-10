package io.github.davidqf555.minecraft.multiverse.common.world.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraftforge.common.BiomeDictionary;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DynamicDefaultChunkGenerator extends NoiseBasedChunkGenerator {

    public static final Codec<DynamicDefaultChunkGenerator> CODEC = RecordCodecBuilder.create(p_188643_ -> commonCodec(p_188643_).and(p_188643_.group(
            RegistryOps.retrieveRegistry(Registry.NOISE_REGISTRY).forGetter(p_188716_ -> p_188716_.noises),
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(p_188711_ -> p_188711_.biomeSource),
            Codec.LONG.fieldOf("seed").stable().forGetter(p_188690_ -> p_188690_.seed),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(p_204585_ -> p_204585_.settings)
    )).apply(p_188643_, p_188643_.stable(DynamicDefaultChunkGenerator::new)));

    public DynamicDefaultChunkGenerator(Registry<StructureSet> p_209106_, Registry<NormalNoise.NoiseParameters> p_209107_, BiomeSource p_209108_, long p_209109_, Holder<NoiseGeneratorSettings> p_209110_) {
        super(p_209106_, p_209107_, p_209108_, p_209109_, p_209110_);
        NoiseGeneratorSettings settings = p_209110_.value();
        int sea = settings.seaLevel();
        globalFluidPicker = new FluidPicker(sea);
        surfaceSystem = new DynamicDefaultSurfaceSystem(climateSampler(), p_209108_, p_209107_, sea, p_209109_, settings.getRandomSource());
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    private class FluidPicker implements Aquifer.FluidPicker {

        private static final Aquifer.FluidStatus LAVA = new Aquifer.FluidStatus(-54, Blocks.LAVA.defaultBlockState());
        private final Aquifer.FluidStatus water, lava, air;
        private final int sea;

        private FluidPicker(int sea) {
            this.sea = sea;
            water = new Aquifer.FluidStatus(sea, Blocks.WATER.defaultBlockState());
            lava = new Aquifer.FluidStatus(sea, Blocks.LAVA.defaultBlockState());
            air = new Aquifer.FluidStatus(sea, Blocks.WATER.defaultBlockState());
        }

        @Override
        public Aquifer.FluidStatus computeFluid(int x, int y, int z) {
            if (y < Math.min(-54, sea)) {
                return LAVA;
            }
            ResourceKey<Biome> biome = ResourceKey.create(Registry.BIOME_REGISTRY, getNoiseBiome(x, y, z).value().getRegistryName());
            if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)) {
                return lava;
            } else if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.END)) {
                return air;
            } else {
                return water;
            }
        }
    }

}
