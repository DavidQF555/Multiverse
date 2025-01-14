package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.LazyMultiverseSurfaceRuleSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class SurfaceRuleSourceRegistry {

    public static final DeferredRegister<Codec<? extends SurfaceRules.RuleSource>> SOURCES = DeferredRegister.create(Registries.MATERIAL_RULE, Multiverse.MOD_ID);

    public static final RegistryObject<Codec<LazyMultiverseSurfaceRuleSource>> MULTIVERSE = register("multiverse", () -> LazyMultiverseSurfaceRuleSource.DIRECT);

    private SurfaceRuleSourceRegistry() {
    }

    private static <T extends SurfaceRules.RuleSource> RegistryObject<Codec<T>> register(String name, Supplier<Codec<T>> codec) {
        return SOURCES.register(name, codec);
    }

}
