package multiverse.registration.worldgen;

import com.mojang.serialization.Codec;
import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.LazyMultiverseSurfaceRuleSource;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class SurfaceRuleSourceRegistry {

    public static final DeferredRegister<Codec<? extends SurfaceRules.RuleSource>> SOURCES = DeferredRegister.create(Registry.RULE_REGISTRY, Multiverse.MOD_ID);

    public static final RegistryObject<Codec<LazyMultiverseSurfaceRuleSource>> MULTIVERSE = register("multiverse", () -> LazyMultiverseSurfaceRuleSource.CODEC);

    private SurfaceRuleSourceRegistry() {
    }

    private static <T extends SurfaceRules.RuleSource> RegistryObject<Codec<T>> register(String name, Supplier<Codec<T>> codec) {
        return SOURCES.register(name, codec);
    }

}
