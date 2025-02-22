package io.github.davidqf555.minecraft.multiverse.registration;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.particles.RiftEffectParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ParticleTypeRegistry {

    public static final DeferredRegister<ParticleType<?>> TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Multiverse.MOD_ID);

    public static final RegistryObject<SimpleParticleType> RIFT = register("rift", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> RIFT_EXPLOSION = register("rift_explosion", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> RIFT_EXPLOSION_EMITTER = register("rift_explosion_emitter", () -> new SimpleParticleType(true));
    public static final RegistryObject<ParticleType<RiftEffectParticleOption>> RIFT_EFFECT = register("rift_effect", () -> new ParticleType<>(false, RiftEffectParticleOption.DESERIALIZER) {
        @Override
        public Codec<RiftEffectParticleOption> codec() {
            return RiftEffectParticleOption.CODEC;
        }
    });

    private ParticleTypeRegistry() {
    }

    private static <T extends ParticleType<?>> RegistryObject<T> register(String name, Supplier<T> type) {
        return TYPES.register(name, type);
    }

}
