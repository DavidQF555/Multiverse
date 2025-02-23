package io.github.davidqf555.minecraft.multiverse.registration;

import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.particles.RiftEffectParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ParticleTypeRegistry {

    public static final DeferredRegister<ParticleType<?>> TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, Multiverse.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RIFT = register("rift", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RIFT_EXPLOSION = register("rift_explosion", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RIFT_EXPLOSION_EMITTER = register("rift_explosion_emitter", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, ParticleType<RiftEffectParticleOption>> RIFT_EFFECT = register("rift_effect", () -> new ParticleType<>(false) {
        @Override
        public MapCodec<RiftEffectParticleOption> codec() {
            return RiftEffectParticleOption.CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, RiftEffectParticleOption> streamCodec() {
            return RiftEffectParticleOption.STREAM_CODEC;
        }
    });

    private ParticleTypeRegistry() {
    }

    private static <T extends ParticleType<?>> DeferredHolder<ParticleType<?>, T> register(String name, Supplier<T> type) {
        return TYPES.register(name, type);
    }

}
