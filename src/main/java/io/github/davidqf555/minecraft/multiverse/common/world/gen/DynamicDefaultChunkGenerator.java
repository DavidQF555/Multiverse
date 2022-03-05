package io.github.davidqf555.minecraft.multiverse.common.world.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        private final Map<ResourceKey<Biome>, Aquifer.FluidPicker> fluids;
        private final Aquifer.FluidPicker def;
        private final int sea;

        private FluidPicker(int sea) {
            fluids = new HashMap<>();
            this.sea = sea;
            Aquifer.FluidStatus water = new Aquifer.FluidStatus(sea, Blocks.WATER.defaultBlockState());
            def = (x, y, z) -> water;
        }

        @Override
        public Aquifer.FluidStatus computeFluid(int x, int y, int z) {
            if (y < Math.min(-54, sea)) {
                return LAVA;
            }
            ResourceKey<Biome> biome = ResourceKey.create(Registry.BIOME_REGISTRY, getNoiseBiome(x, y, z).value().getRegistryName());
            return fluids.computeIfAbsent(biome, key -> {
                for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenSettings().dimensions().entrySet()) {
                    if (!entry.getKey().location().getNamespace().equals(Multiverse.MOD_ID)) {
                        ChunkGenerator gen = entry.getValue().generator();
                        if (gen instanceof NoiseBasedChunkGenerator && gen.getBiomeSource().possibleBiomes().stream().map(Holder::value).map(ForgeRegistryEntry::getRegistryName).filter(Objects::nonNull).map(name -> ResourceKey.create(Registry.BIOME_REGISTRY, name)).anyMatch(key::equals)) {
                            return ((NoiseBasedChunkGenerator) gen).globalFluidPicker;
                        }
                    }
                }
                return def;
            }).computeFluid(x, y, z);
        }
    }

}