package io.github.davidqf555.minecraft.multiverse.common.world.gen.dynamic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraftforge.common.BiomeDictionary;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.OptionalInt;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
public class DynamicDefaultChunkGenerator extends NoiseBasedChunkGenerator {

    public static final Codec<DynamicDefaultChunkGenerator> CODEC = RecordCodecBuilder.create(p_188643_ -> commonCodec(p_188643_).and(p_188643_.group(
            RegistryOps.retrieveRegistry(Registry.NOISE_REGISTRY).forGetter(p_188716_ -> p_188716_.noises),
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(p_188711_ -> p_188711_.biomeSource),
            Codec.LONG.fieldOf("seed").stable().forGetter(p_188690_ -> p_188690_.seed),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(p_204585_ -> p_204585_.settings)
    )).apply(p_188643_, p_188643_.stable(DynamicDefaultChunkGenerator::new)));
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();
    private static final BlockState STONE = Blocks.STONE.defaultBlockState();
    private static final BlockState NETHERRACK = Blocks.NETHERRACK.defaultBlockState();
    private static final BlockState END_STONE = Blocks.END_STONE.defaultBlockState();

    public DynamicDefaultChunkGenerator(Registry<StructureSet> p_209106_, Registry<NormalNoise.NoiseParameters> p_209107_, BiomeSource p_209108_, long p_209109_, Holder<NoiseGeneratorSettings> p_209110_) {
        super(p_209106_, p_209107_, p_209108_, p_209109_, p_209110_);
        NoiseGeneratorSettings settings = p_209110_.value();
        int sea = settings.seaLevel();
        globalFluidPicker = new DynamicDefaultFluidPicker(sea, this);
        surfaceSystem = new DynamicDefaultSurfaceSystem(climateSampler(), p_209108_, p_209107_, sea, p_209109_, settings.getRandomSource());
    }

    public static BlockState getDefault(ResourceKey<Biome> biome) {
        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)) {
            return NETHERRACK;
        } else if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.END)) {
            return END_STONE;
        } else {
            return STONE;
        }
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    protected OptionalInt iterateNoiseColumn(int p_158414_, int p_158415_, @Nullable BlockState[] p_158416_, @Nullable Predicate<BlockState> p_158417_, int p_158418_, int p_158419_) {
        NoiseSettings noisesettings = this.settings.value().noiseSettings();
        int i = noisesettings.getCellWidth();
        int j = noisesettings.getCellHeight();
        int k = Math.floorDiv(p_158414_, i);
        int l = Math.floorDiv(p_158415_, i);
        int i1 = Math.floorMod(p_158414_, i);
        int j1 = Math.floorMod(p_158415_, i);
        int k1 = k * i;
        int l1 = l * i;
        double d0 = (double) i1 / (double) i;
        double d1 = (double) j1 / (double) i;
        BlockState def = getDefault(ResourceKey.create(Registry.BIOME_REGISTRY, getNoiseBiome(QuartPos.fromBlock(p_158414_), 0, QuartPos.fromBlock(p_158415_)).value().getRegistryName()));

        NoiseChunk noisechunk = NoiseChunk.forColumn(k1, l1, p_158418_, p_158419_, this.router, this.settings.value(), this.globalFluidPicker);
        noisechunk.initializeForFirstCellX();
        noisechunk.advanceCellX(0);

        for (int i2 = p_158419_ - 1; i2 >= 0; --i2) {
            noisechunk.selectCellYZ(i2, 0);

            for (int j2 = j - 1; j2 >= 0; --j2) {
                int k2 = (p_158418_ + i2) * j + j2;
                double d2 = (double) j2 / (double) j;
                noisechunk.updateForY(k2, d2);
                noisechunk.updateForX(p_158414_, d0);
                noisechunk.updateForZ(p_158415_, d1);
                BlockState blockstate = noisechunk.getInterpolatedState();
                BlockState blockstate1 = blockstate == null ? def : blockstate;
                if (p_158416_ != null) {
                    int l2 = i2 * j + j2;
                    p_158416_[l2] = blockstate1;
                }

                if (p_158417_ != null && p_158417_.test(blockstate1)) {
                    noisechunk.stopInterpolation();
                    return OptionalInt.of(k2 + 1);
                }
            }
        }

        noisechunk.stopInterpolation();
        return OptionalInt.empty();
    }

    @Override
    protected ChunkAccess doFill(Blender p_188663_, StructureFeatureManager p_188664_, ChunkAccess p_188665_, int p_188666_, int p_188667_) {
        NoiseGeneratorSettings noisegeneratorsettings = this.settings.value();
        NoiseChunk noisechunk = p_188665_.getOrCreateNoiseChunk(this.router, () -> {
            return new Beardifier(p_188664_, p_188665_);
        }, noisegeneratorsettings, this.globalFluidPicker, p_188663_);
        Heightmap heightmap = p_188665_.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap heightmap1 = p_188665_.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        ChunkPos chunkpos = p_188665_.getPos();
        int i = chunkpos.getMinBlockX();
        int j = chunkpos.getMinBlockZ();
        BlockState def = getDefault(ResourceKey.create(Registry.BIOME_REGISTRY, getNoiseBiome(QuartPos.fromBlock(i), 0, QuartPos.fromBlock(j)).value().getRegistryName()));
        Aquifer aquifer = noisechunk.aquifer();
        noisechunk.initializeForFirstCellX();
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        NoiseSettings noisesettings = noisegeneratorsettings.noiseSettings();
        int k = noisesettings.getCellWidth();
        int l = noisesettings.getCellHeight();
        int i1 = 16 / k;
        int j1 = 16 / k;

        for (int k1 = 0; k1 < i1; ++k1) {
            noisechunk.advanceCellX(k1);

            for (int l1 = 0; l1 < j1; ++l1) {
                LevelChunkSection levelchunksection = p_188665_.getSection(p_188665_.getSectionsCount() - 1);

                for (int i2 = p_188667_ - 1; i2 >= 0; --i2) {
                    noisechunk.selectCellYZ(i2, l1);

                    for (int j2 = l - 1; j2 >= 0; --j2) {
                        int k2 = (p_188666_ + i2) * l + j2;
                        int l2 = k2 & 15;
                        int i3 = p_188665_.getSectionIndex(k2);
                        if (p_188665_.getSectionIndex(levelchunksection.bottomBlockY()) != i3) {
                            levelchunksection = p_188665_.getSection(i3);
                        }

                        double d0 = (double) j2 / (double) l;
                        noisechunk.updateForY(k2, d0);

                        for (int j3 = 0; j3 < k; ++j3) {
                            int k3 = i + k1 * k + j3;
                            int l3 = k3 & 15;
                            double d1 = (double) j3 / (double) k;
                            noisechunk.updateForX(k3, d1);

                            for (int i4 = 0; i4 < k; ++i4) {
                                int j4 = j + l1 * k + i4;
                                int k4 = j4 & 15;
                                double d2 = (double) i4 / (double) k;
                                noisechunk.updateForZ(j4, d2);
                                BlockState blockstate = noisechunk.getInterpolatedState();
                                if (blockstate == null) {
                                    blockstate = def;
                                }

                                blockstate = this.debugPreliminarySurfaceLevel(noisechunk, k3, k2, j4, blockstate);
                                if (blockstate != AIR && !SharedConstants.debugVoidTerrain(p_188665_.getPos())) {
                                    if (blockstate.getLightEmission() != 0 && p_188665_ instanceof ProtoChunk) {
                                        blockpos$mutableblockpos.set(k3, k2, j4);
                                        ((ProtoChunk) p_188665_).addLight(blockpos$mutableblockpos);
                                    }

                                    levelchunksection.setBlockState(l3, l2, k4, blockstate, false);
                                    heightmap.update(l3, k2, k4, blockstate);
                                    heightmap1.update(l3, k2, k4, blockstate);
                                    if (aquifer.shouldScheduleFluidUpdate() && !blockstate.getFluidState().isEmpty()) {
                                        blockpos$mutableblockpos.set(k3, k2, j4);
                                        p_188665_.markPosForPostprocessing(blockpos$mutableblockpos);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            noisechunk.swapSlices();
        }
        noisechunk.stopInterpolation();
        return p_188665_;
    }

}
