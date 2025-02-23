package multiverse.registration.worldgen;

import com.mojang.serialization.MapCodec;
import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.LazyMultiverseSurfaceRuleSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class SurfaceRuleSourceRegistry {

    public static final DeferredRegister<MapCodec<? extends SurfaceRules.RuleSource>> SOURCES = DeferredRegister.create(Registries.MATERIAL_RULE, Multiverse.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends SurfaceRules.RuleSource>, MapCodec<LazyMultiverseSurfaceRuleSource>> MULTIVERSE = register("multiverse", () -> LazyMultiverseSurfaceRuleSource.DIRECT);

    private SurfaceRuleSourceRegistry() {
    }

    private static <T extends SurfaceRules.RuleSource> DeferredHolder<MapCodec<? extends SurfaceRules.RuleSource>, MapCodec<T>> register(String name, Supplier<MapCodec<T>> codec) {
        return SOURCES.register(name, codec);
    }

}
