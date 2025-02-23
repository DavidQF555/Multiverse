package multiverse.mixin;

import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.generators.GeneratorHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.OptionalLong;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(value = ServerLevel.class)
public abstract class MixinServerLevel extends Level {

    @Shadow
    private StructureManager structureManager;
    @Shadow
    private StructureCheck structureCheck;
    @Shadow
    private RandomSequences randomSequences;
    @Unique
    private OptionalLong seed = OptionalLong.empty();

    protected MixinServerLevel(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, profiler, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

    @Inject(method = "<init>", at = @At("CTOR_HEAD"))
    private void initHead(
            MinecraftServer server,
            Executor dispatcher,
            LevelStorageSource.LevelStorageAccess levelStorageAccess,
            ServerLevelData serverLevelData,
            ResourceKey<Level> dimension,
            LevelStem levelStem,
            ChunkProgressListener progressListener,
            boolean isDebug,
            long biomeZoomSeed,
            List<CustomSpawner> customSpawners,
            boolean tickTime,
            @Nullable RandomSequences randomSequences,
            CallbackInfo callback
    ) {
        GeneratorHelper.getIndex(dimension.location())
                .ifPresent(index -> seed = OptionalLong.of(GeneratorHelper.getSeed(server.getWorldData().worldGenOptions().seed(), index)));
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initReturn(
            MinecraftServer server,
            Executor dispatcher,
            LevelStorageSource.LevelStorageAccess levelStorageAccess,
            ServerLevelData serverLevelData,
            ResourceKey<Level> dimension,
            LevelStem levelStem,
            ChunkProgressListener progressListener,
            boolean isDebug,
            long biomeZoomSeed,
            List<CustomSpawner> customSpawners,
            boolean tickTime,
            @Nullable RandomSequences randomSequences,
            CallbackInfo callback
    ) {
        GeneratorHelper.getIndex(dimension.location())
                .ifPresent(index -> {
                    long seed = GeneratorHelper.getSeed(server.getWorldData().worldGenOptions().seed(), index);
                    ServerChunkCache cache = (ServerChunkCache) getChunkSource();
                    structureCheck = new StructureCheck(
                            cache.chunkScanner(),
                            registryAccess(),
                            getServer().getStructureManager(),
                            dimension,
                            cache.getGenerator(),
                            cache.randomState(),
                            this,
                            cache.getGenerator().getBiomeSource(),
                            seed,
                            server.getFixerUpper()
                    );
                    structureManager = new StructureManager(this, server.getWorldData().worldGenOptions(), structureCheck);
                    this.randomSequences = cache.getDataStorage().computeIfAbsent(RandomSequences.factory(seed), Multiverse.MOD_ID + ".random_sequences_" + index);
                });
    }

    @Inject(method = "getSeed", at = @At("HEAD"), cancellable = true)
    private void getSeed(CallbackInfoReturnable<Long> callback) {
        seed.ifPresent(callback::setReturnValue);
    }

}
