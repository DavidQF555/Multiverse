package io.github.davidqf555.minecraft.multiverse.common.world.gen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DynamicDefaultSurfaceSystem extends SurfaceSystem {

    private final Map<ResourceKey<Biome>, BlockState> defaults;
    private final BiomeResolver source;
    private final Climate.Sampler climate;

    public DynamicDefaultSurfaceSystem(Climate.Sampler climate, BiomeResolver source, Registry<NormalNoise.NoiseParameters> noise, int sea, long seed, WorldgenRandom.Algorithm algorithm) {
        super(noise, Blocks.STONE.defaultBlockState(), sea, seed, algorithm);
        defaults = new HashMap<>();
        this.source = source;
        this.climate = climate;
    }

    @Override
    public void buildSurface(BiomeManager p_189945_, Registry<Biome> p_189946_, boolean p_189947_, WorldGenerationContext p_189948_, final ChunkAccess p_189949_, NoiseChunk p_189950_, SurfaceRules.RuleSource p_189951_) {
        final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        final ChunkPos chunkpos = p_189949_.getPos();
        int i = chunkpos.getMinBlockX();
        int j = chunkpos.getMinBlockZ();
        BlockColumn blockcolumn = new BlockColumn() {

            @Override
            public BlockState getBlock(int p_190006_) {
                return p_189949_.getBlockState(blockpos$mutableblockpos.setY(p_190006_));
            }

            @Override
            public void setBlock(int p_190008_, BlockState p_190009_) {
                LevelHeightAccessor levelheightaccessor = p_189949_.getHeightAccessorForGeneration();
                if (p_190008_ >= levelheightaccessor.getMinBuildHeight() && p_190008_ < levelheightaccessor.getMaxBuildHeight()) {
                    p_189949_.setBlockState(blockpos$mutableblockpos.setY(p_190008_), p_190009_, false);
                    if (!p_190009_.getFluidState().isEmpty()) {
                        p_189949_.markPosForPostprocessing(blockpos$mutableblockpos);
                    }
                }

            }

            @Override
            public String toString() {
                return "ChunkBlockColumn " + chunkpos;
            }
        };
        SurfaceRules.Context surfacerules$context = new SurfaceRules.Context(this, p_189949_, p_189950_, p_189945_::getBiome, p_189946_, p_189948_);
        SurfaceRules.SurfaceRule surfacerules$surfacerule = p_189951_.apply(surfacerules$context);
        BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();

        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                int i1 = i + k;
                int j1 = j + l;
                int k1 = p_189949_.getHeight(Heightmap.Types.WORLD_SURFACE_WG, k, l) + 1;
                blockpos$mutableblockpos.setX(i1).setZ(j1);
                Holder<Biome> holder = p_189945_.getBiome(blockpos$mutableblockpos1.set(i1, p_189947_ ? 0 : k1, j1));
                if (holder.is(Biomes.ERODED_BADLANDS)) {
                    this.erodedBadlandsExtension(blockcolumn, i1, j1, k1, p_189949_);
                }

                int l1 = p_189949_.getHeight(Heightmap.Types.WORLD_SURFACE_WG, k, l) + 1;
                surfacerules$context.updateXZ(i1, j1);
                int i2 = 0;
                int j2 = Integer.MIN_VALUE;
                int k2 = Integer.MAX_VALUE;
                int l2 = p_189949_.getMinBuildHeight();
                BlockState def = getDefault(ResourceKey.create(Registry.BIOME_REGISTRY, holder.value().getRegistryName()));
                for (int i3 = l1; i3 >= l2; --i3) {
                    BlockState blockstate = blockcolumn.getBlock(i3);
                    if (blockstate.isAir()) {
                        i2 = 0;
                        j2 = Integer.MIN_VALUE;
                    } else if (!blockstate.getFluidState().isEmpty()) {
                        if (j2 == Integer.MIN_VALUE) {
                            j2 = i3 + 1;
                        }
                    } else {
                        if (k2 >= i3) {
                            k2 = DimensionType.WAY_BELOW_MIN_Y;

                            for (int j3 = i3 - 1; j3 >= l2 - 1; --j3) {
                                BlockState blockstate1 = blockcolumn.getBlock(j3);
                                if (!this.isStone(blockstate1)) {
                                    k2 = j3 + 1;
                                    break;
                                }
                            }
                        }

                        ++i2;
                        int k3 = i3 - k2 + 1;
                        surfacerules$context.updateY(i2, k3, j2, i1, i3, j1);
                        if (blockstate == def) {
                            BlockState blockstate2 = surfacerules$surfacerule.tryApply(i1, i3, j1);
                            if (blockstate2 != null) {
                                blockcolumn.setBlock(i3, blockstate2);
                            }
                        }
                    }
                }

                if (holder.is(Biomes.FROZEN_OCEAN) || holder.is(Biomes.DEEP_FROZEN_OCEAN)) {
                    this.frozenOceanExtension(surfacerules$context.getMinSurfaceLevel(), holder.value(), blockcolumn, blockpos$mutableblockpos1, i1, j1, k1);
                }
            }
        }

    }

    @Override
    protected void erodedBadlandsExtension(BlockColumn p_189955_, int p_189956_, int p_189957_, int p_189958_, LevelHeightAccessor p_189959_) {
        double d1 = Math.min(Math.abs(this.badlandsSurfaceNoise.getValue(p_189956_, 0.0D, p_189957_) * 8.25D), this.badlandsPillarNoise.getValue((double) p_189956_ * 0.2D, 0.0D, (double) p_189957_ * 0.2D) * 15.0D);
        if (!(d1 <= 0.0D)) {
            double d4 = Math.abs(this.badlandsPillarRoofNoise.getValue((double) p_189956_ * 0.75D, 0.0D, (double) p_189957_ * 0.75D) * 1.5D);
            double d5 = 64.0D + Math.min(d1 * d1 * 2.5D, Math.ceil(d4 * 50.0D) + 24.0D);
            int i = Mth.floor(d5);
            if (p_189958_ <= i) {
                BlockState def = getDefault(ResourceKey.create(Registry.BIOME_REGISTRY, source.getNoiseBiome(p_189956_, p_189958_, p_189957_, climate).value().getRegistryName()));
                for (int j = i; j >= p_189959_.getMinBuildHeight(); --j) {
                    BlockState blockstate = p_189955_.getBlock(j);
                    if (blockstate.is(def.getBlock())) {
                        break;
                    }

                    if (blockstate.is(Blocks.WATER)) {
                        return;
                    }
                }

                for (int k = i; k >= p_189959_.getMinBuildHeight() && p_189955_.getBlock(k).isAir(); --k) {
                    p_189955_.setBlock(k, def);
                }

            }
        }
    }

    private BlockState getDefault(ResourceKey<Biome> biome) {
        return defaults.computeIfAbsent(biome, key -> {
            for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenSettings().dimensions().entrySet()) {
                if (!entry.getKey().location().getNamespace().equals(Multiverse.MOD_ID)) {
                    ChunkGenerator gen = entry.getValue().generator();
                    if (gen instanceof NoiseBasedChunkGenerator && gen.getBiomeSource().possibleBiomes().stream().map(Holder::value).map(ForgeRegistryEntry::getRegistryName).filter(Objects::nonNull).map(name -> ResourceKey.create(Registry.BIOME_REGISTRY, name)).anyMatch(key::equals)) {
                        return ((NoiseBasedChunkGenerator) gen).defaultBlock;
                    }
                }
            }
            return Blocks.STONE.defaultBlockState();
        });
    }
}