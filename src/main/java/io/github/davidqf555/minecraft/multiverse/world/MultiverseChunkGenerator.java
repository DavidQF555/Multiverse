package io.github.davidqf555.minecraft.multiverse.world;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.Multiverse;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.jigsaw.JigsawJunction;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class MultiverseChunkGenerator extends NoiseChunkGenerator {

    public static final Codec<MultiverseChunkGenerator> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            BiomeProvider.CODEC.fieldOf("biome_source").forGetter(gen -> gen.biomeSource),
            Codec.LONG.fieldOf("seed").stable().forGetter(gen -> gen.seed),
            DimensionSettings.CODEC.fieldOf("settings").forGetter(gen -> gen.settings)
    ).apply(builder, builder.stable(MultiverseChunkGenerator::new)));
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();
    private static final Map<RegistryKey<Biome>, Pair<BlockState, BlockState>> DEFAULTS = new HashMap<>();

    public MultiverseChunkGenerator(BiomeProvider provider, long seed, Supplier<DimensionSettings> settings) {
        super(provider, seed, settings);
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long seed) {
        return new MultiverseChunkGenerator(biomeSource, seed, settings);
    }

    @Override
    protected int iterateNoiseColumn(int p_236087_1_, int p_236087_2_, @Nullable BlockState[] p_236087_3_, @Nullable Predicate<BlockState> p_236087_4_) {
        int i = Math.floorDiv(p_236087_1_, this.chunkWidth);
        int j = Math.floorDiv(p_236087_2_, this.chunkWidth);
        int k = Math.floorMod(p_236087_1_, this.chunkWidth);
        int l = Math.floorMod(p_236087_2_, this.chunkWidth);
        double d0 = (double) k / (double) this.chunkWidth;
        double d1 = (double) l / (double) this.chunkWidth;
        double[][] adouble = new double[][]{this.makeAndFillNoiseColumn(i, j), this.makeAndFillNoiseColumn(i, j + 1), this.makeAndFillNoiseColumn(i + 1, j), this.makeAndFillNoiseColumn(i + 1, j + 1)};
        BiomeProvider provider = getBiomeSource();
        for (int i1 = this.chunkCountY - 1; i1 >= 0; --i1) {
            double d2 = adouble[0][i1];
            double d3 = adouble[1][i1];
            double d4 = adouble[2][i1];
            double d5 = adouble[3][i1];
            double d6 = adouble[0][i1 + 1];
            double d7 = adouble[1][i1 + 1];
            double d8 = adouble[2][i1 + 1];
            double d9 = adouble[3][i1 + 1];

            for (int j1 = this.chunkHeight - 1; j1 >= 0; --j1) {
                double d10 = (double) j1 / (double) this.chunkHeight;
                double d11 = MathHelper.lerp3(d10, d0, d1, d2, d6, d4, d8, d3, d7, d5, d9);
                int k1 = i1 * this.chunkHeight + j1;
                BlockState blockstate = getDefaultBlockState(RegistryKey.create(Registry.BIOME_REGISTRY, provider.getNoiseBiome(p_236087_1_, k1, p_236087_2_).getRegistryName()), d11, k1);
                if (p_236087_3_ != null) {
                    p_236087_3_[k1] = blockstate;
                }
                if (p_236087_4_ != null && p_236087_4_.test(blockstate)) {
                    return k1 + 1;
                }
            }
        }

        return 0;
    }

    @Override
    public void buildSurfaceAndBedrock(WorldGenRegion p_225551_1_, IChunk p_225551_2_) {
        ChunkPos chunkpos = p_225551_2_.getPos();
        int i = chunkpos.x;
        int j = chunkpos.z;
        SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
        sharedseedrandom.setBaseChunkSeed(i, j);
        ChunkPos chunkpos1 = p_225551_2_.getPos();
        int k = chunkpos1.getMinBlockX();
        int l = chunkpos1.getMinBlockZ();
        Pair<BlockState, BlockState> defaults = getDefaults(RegistryKey.create(Registry.BIOME_REGISTRY, p_225551_1_.getNoiseBiome(k, 0, l).getRegistryName()));
        BlockState block = defaults.getFirst();
        BlockState fluid = defaults.getSecond();
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        for (int i1 = 0; i1 < 16; ++i1) {
            for (int j1 = 0; j1 < 16; ++j1) {
                int k1 = k + i1;
                int l1 = l + j1;
                int i2 = p_225551_2_.getHeight(Heightmap.Type.WORLD_SURFACE_WG, i1, j1) + 1;
                double d1 = this.surfaceNoise.getSurfaceNoiseValue((double) k1 * 0.0625D, (double) l1 * 0.0625D, 0.0625D, (double) i1 * 0.0625D) * 15.0D;
                p_225551_1_.getBiome(blockpos$mutable.set(k + i1, i2, l + j1)).buildSurfaceAt(sharedseedrandom, p_225551_2_, k1, l1, i2, d1, block, fluid, this.getSeaLevel(), p_225551_1_.getSeed());
            }
        }
        this.setBedrock(p_225551_2_, sharedseedrandom);
    }

    @Override
    public void fillFromNoise(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk p_230352_3_) {
        ObjectList<StructurePiece> objectlist = new ObjectArrayList<>(10);
        ObjectList<JigsawJunction> objectlist1 = new ObjectArrayList<>(32);
        ChunkPos chunkpos = p_230352_3_.getPos();
        int i = chunkpos.x;
        int j = chunkpos.z;
        int k = i << 4;
        int l = j << 4;

        for (Structure<?> structure : Structure.NOISE_AFFECTING_FEATURES) {
            p_230352_2_.startsForFeature(SectionPos.of(chunkpos, 0), structure).forEach((p_236089_5_) -> {
                for (StructurePiece structurepiece1 : p_236089_5_.getPieces()) {
                    if (structurepiece1.isCloseToChunk(chunkpos, 12)) {
                        if (structurepiece1 instanceof AbstractVillagePiece) {
                            AbstractVillagePiece abstractvillagepiece = (AbstractVillagePiece) structurepiece1;
                            JigsawPattern.PlacementBehaviour jigsawpattern$placementbehaviour = abstractvillagepiece.getElement().getProjection();
                            if (jigsawpattern$placementbehaviour == JigsawPattern.PlacementBehaviour.RIGID) {
                                objectlist.add(abstractvillagepiece);
                            }

                            for (JigsawJunction jigsawjunction1 : abstractvillagepiece.getJunctions()) {
                                int l5 = jigsawjunction1.getSourceX();
                                int i6 = jigsawjunction1.getSourceZ();
                                if (l5 > k - 12 && i6 > l - 12 && l5 < k + 15 + 12 && i6 < l + 15 + 12) {
                                    objectlist1.add(jigsawjunction1);
                                }
                            }
                        } else {
                            objectlist.add(structurepiece1);
                        }
                    }
                }

            });
        }
        double[][][] adouble = new double[2][this.chunkCountZ + 1][this.chunkCountY + 1];
        for (int i5 = 0; i5 < this.chunkCountZ + 1; ++i5) {
            adouble[0][i5] = new double[this.chunkCountY + 1];
            this.fillNoiseColumn(adouble[0][i5], i * this.chunkCountX, j * this.chunkCountZ + i5);
            adouble[1][i5] = new double[this.chunkCountY + 1];
        }
        RegistryKey<Biome> biome = RegistryKey.create(Registry.BIOME_REGISTRY, p_230352_1_.getBiome(new BlockPos(k, 0, l)).getRegistryName());
        ChunkPrimer chunkprimer = (ChunkPrimer) p_230352_3_;
        Heightmap heightmap = chunkprimer.getOrCreateHeightmapUnprimed(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap heightmap1 = chunkprimer.getOrCreateHeightmapUnprimed(Heightmap.Type.WORLD_SURFACE_WG);
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        ObjectListIterator<StructurePiece> objectlistiterator = objectlist.iterator();
        ObjectListIterator<JigsawJunction> objectlistiterator1 = objectlist1.iterator();

        for (int i1 = 0; i1 < this.chunkCountX; ++i1) {
            for (int j1 = 0; j1 < this.chunkCountZ + 1; ++j1) {
                this.fillNoiseColumn(adouble[1][j1], i * this.chunkCountX + i1 + 1, j * this.chunkCountZ + j1);
            }

            for (int j5 = 0; j5 < this.chunkCountZ; ++j5) {
                ChunkSection chunksection = chunkprimer.getOrCreateSection(15);
                chunksection.acquire();

                for (int k1 = this.chunkCountY - 1; k1 >= 0; --k1) {
                    double d0 = adouble[0][j5][k1];
                    double d1 = adouble[0][j5 + 1][k1];
                    double d2 = adouble[1][j5][k1];
                    double d3 = adouble[1][j5 + 1][k1];
                    double d4 = adouble[0][j5][k1 + 1];
                    double d5 = adouble[0][j5 + 1][k1 + 1];
                    double d6 = adouble[1][j5][k1 + 1];
                    double d7 = adouble[1][j5 + 1][k1 + 1];

                    for (int l1 = this.chunkHeight - 1; l1 >= 0; --l1) {
                        int i2 = k1 * this.chunkHeight + l1;
                        int j2 = i2 & 15;
                        int k2 = i2 >> 4;
                        if (chunksection.bottomBlockY() >> 4 != k2) {
                            chunksection.release();
                            chunksection = chunkprimer.getOrCreateSection(k2);
                            chunksection.acquire();
                        }

                        double d8 = (double) l1 / (double) this.chunkHeight;
                        double d9 = MathHelper.lerp(d8, d0, d4);
                        double d10 = MathHelper.lerp(d8, d2, d6);
                        double d11 = MathHelper.lerp(d8, d1, d5);
                        double d12 = MathHelper.lerp(d8, d3, d7);

                        for (int l2 = 0; l2 < this.chunkWidth; ++l2) {
                            int i3 = k + i1 * this.chunkWidth + l2;
                            int j3 = i3 & 15;
                            double d13 = (double) l2 / (double) this.chunkWidth;
                            double d14 = MathHelper.lerp(d13, d9, d10);
                            double d15 = MathHelper.lerp(d13, d11, d12);

                            for (int k3 = 0; k3 < this.chunkWidth; ++k3) {
                                int l3 = l + j5 * this.chunkWidth + k3;
                                int i4 = l3 & 15;
                                double d16 = (double) k3 / (double) this.chunkWidth;
                                double d17 = MathHelper.lerp(d16, d14, d15);
                                double d18 = MathHelper.clamp(d17 / 200.0D, -1.0D, 1.0D);

                                int j4;
                                int k4;
                                int l4;
                                for (d18 = d18 / 2.0D - d18 * d18 * d18 / 24.0D; objectlistiterator.hasNext(); d18 += getContribution(j4, k4, l4) * 0.8D) {
                                    StructurePiece structurepiece = objectlistiterator.next();
                                    MutableBoundingBox mutableboundingbox = structurepiece.getBoundingBox();
                                    j4 = Math.max(0, Math.max(mutableboundingbox.x0 - i3, i3 - mutableboundingbox.x1));
                                    k4 = i2 - (mutableboundingbox.y0 + (structurepiece instanceof AbstractVillagePiece ? ((AbstractVillagePiece) structurepiece).getGroundLevelDelta() : 0));
                                    l4 = Math.max(0, Math.max(mutableboundingbox.z0 - l3, l3 - mutableboundingbox.z1));
                                }

                                objectlistiterator.back(objectlist.size());

                                while (objectlistiterator1.hasNext()) {
                                    JigsawJunction jigsawjunction = objectlistiterator1.next();
                                    int k5 = i3 - jigsawjunction.getSourceX();
                                    j4 = i2 - jigsawjunction.getSourceGroundY();
                                    k4 = l3 - jigsawjunction.getSourceZ();
                                    d18 += getContribution(k5, j4, k4) * 0.4D;
                                }

                                objectlistiterator1.back(objectlist1.size());
                                BlockState blockstate = getDefaultBlockState(biome, d18, i2);
                                if (blockstate != AIR) {
                                    blockpos$mutable.set(i3, i2, l3);
                                    if (blockstate.getLightValue(chunkprimer, blockpos$mutable) != 0) {
                                        chunkprimer.addLight(blockpos$mutable);
                                    }

                                    chunksection.setBlockState(j3, j2, i4, blockstate, false);
                                    heightmap.update(j3, i2, i4, blockstate);
                                    heightmap1.update(j3, i2, i4, blockstate);
                                }
                            }
                        }
                    }
                }

                chunksection.release();
            }

            double[][] adouble1 = adouble[0];
            adouble[0] = adouble[1];
            adouble[1] = adouble1;
        }

    }

    private BlockState getDefaultBlockState(RegistryKey<Biome> biome, double p_236086_1_, int y) {
        Pair<BlockState, BlockState> states = getDefaults(biome);
        if (p_236086_1_ > 0) {
            return states.getFirst();
        } else if (y < getSeaLevel()) {
            return states.getSecond();
        } else {
            return AIR;
        }
    }

    private Pair<BlockState, BlockState> getDefaults(RegistryKey<Biome> key) {
        if (DEFAULTS.containsKey(key)) {
            return DEFAULTS.get(key);
        } else {
            Pair<BlockState, BlockState> states = null;
            for (Map.Entry<RegistryKey<Dimension>, Dimension> entry : ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenSettings().dimensions().entrySet()) {
                if (!entry.getKey().location().getNamespace().equals(Multiverse.MOD_ID)) {
                    ChunkGenerator gen = entry.getValue().generator();
                    if (gen instanceof NoiseChunkGenerator && gen.getBiomeSource().possibleBiomes().stream().anyMatch(b -> key.location().equals(b.getRegistryName()))) {
                        DimensionSettings settings = ((NoiseChunkGenerator) gen).settings.get();
                        states = Pair.of(settings.getDefaultBlock(), settings.getDefaultFluid());
                        break;
                    }
                }
            }
            if (states == null) {
                states = Pair.of(Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState());
            }
            DEFAULTS.put(key, states);
            return states;
        }
    }

}
