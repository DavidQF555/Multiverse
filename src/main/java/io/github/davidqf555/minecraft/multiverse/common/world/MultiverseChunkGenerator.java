package io.github.davidqf555.minecraft.multiverse.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.material.WorldGenMaterialRule;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class MultiverseChunkGenerator extends NoiseBasedChunkGenerator {

    private static final Map<ResourceKey<Biome>, Aquifer.FluidStatus> FLUIDS = new HashMap<>();
    public static final Codec<MultiverseChunkGenerator> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                    RegistryLookupCodec.create(Registry.NOISE_REGISTRY).forGetter(gen -> gen.noises),
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(gen -> gen.biomeSource),
                    Codec.LONG.fieldOf("seed").stable().forGetter(gen -> gen.seed),
                    NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(gen -> gen.settings))
            .apply(builder, builder.stable(MultiverseChunkGenerator::new)));

    public MultiverseChunkGenerator(Registry<NormalNoise.NoiseParameters> noise, BiomeSource source, long seed, Supplier<NoiseGeneratorSettings> settings) {
        super(noise, source, seed, settings);
        Aquifer.FluidStatus lava = new Aquifer.FluidStatus(-54, Blocks.LAVA.defaultBlockState());
        int sea = settings.get().seaLevel();
        globalFluidPicker = (x, y, z) -> y < Math.min(-54, sea) ? lava : getDefaultFluid(x, y, z, sea);
        materialRule = new BiomeMaterialRule(materialRule);
    }

    private Aquifer.FluidStatus getDefaultFluid(int x, int y, int z, int sea) {
        ResourceKey<Biome> biome = ResourceKey.create(Registry.BIOME_REGISTRY, getNoiseBiome(x, y, z).getRegistryName());
        if (FLUIDS.containsKey(biome)) {
            return FLUIDS.get(biome);
        }
        BlockState fluid = null;
        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenSettings().dimensions().entrySet()) {
            if (!entry.getKey().location().getNamespace().equals(Multiverse.MOD_ID)) {
                ChunkGenerator gen = entry.getValue().generator();
                if (gen instanceof NoiseBasedChunkGenerator && gen.getBiomeSource().possibleBiomes().stream().anyMatch(b -> biome.location().equals(b.getRegistryName()))) {
                    NoiseGeneratorSettings settings = ((NoiseBasedChunkGenerator) gen).settings.get();
                    fluid = settings.getDefaultFluid();
                    break;
                }
            }
        }
        if (fluid == null) {
            fluid = settings.get().getDefaultFluid();
        }
        Aquifer.FluidStatus status = new Aquifer.FluidStatus(sea, fluid);
        FLUIDS.put(biome, status);
        return status;
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    private class BiomeMaterialRule implements WorldGenMaterialRule {

        private static final Map<ResourceKey<Biome>, BlockState> BLOCKS = new HashMap<>();
        private final WorldGenMaterialRule rule;

        public BiomeMaterialRule(WorldGenMaterialRule rule) {
            this.rule = rule;
        }

        @Override
        public BlockState apply(NoiseChunk chunk, int x, int y, int z) {
            BlockState state = rule.apply(chunk, x, y, z);
            if (state == null) {
                ResourceKey<Biome> biome = ResourceKey.create(Registry.BIOME_REGISTRY, getNoiseBiome(x, y, z).getRegistryName());
                if (BLOCKS.containsKey(biome)) {
                    return BLOCKS.get(biome);
                }
                BlockState block = null;
                for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenSettings().dimensions().entrySet()) {
                    if (!entry.getKey().location().getNamespace().equals(Multiverse.MOD_ID)) {
                        ChunkGenerator gen = entry.getValue().generator();
                        if (gen instanceof NoiseBasedChunkGenerator && gen.getBiomeSource().possibleBiomes().stream().anyMatch(b -> biome.location().equals(b.getRegistryName()))) {
                            NoiseGeneratorSettings settings = ((NoiseBasedChunkGenerator) gen).settings.get();
                            block = settings.getDefaultBlock();
                            break;
                        }
                    }
                }
                if (block == null) {
                    block = settings.get().getDefaultBlock();
                }
                BLOCKS.put(biome, block);
                return block;
            }
            return state;
        }
    }


}