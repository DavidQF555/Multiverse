package io.github.davidqf555.minecraft.multiverse.common.world.particles;

import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.common.util.TagUtil;
import io.github.davidqf555.minecraft.multiverse.registration.ParticleTypeRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record RiftEffectParticleOption(ResourceKey<Level> color) implements ParticleOptions {

    public static final MapCodec<RiftEffectParticleOption> CODEC = ResourceKey.codec(Registries.DIMENSION).xmap(RiftEffectParticleOption::new, RiftEffectParticleOption::color).fieldOf("color");
    public static final StreamCodec<RegistryFriendlyByteBuf, RiftEffectParticleOption> STREAM_CODEC = StreamCodec.composite(
            TagUtil.WORLD_CODEC,
            RiftEffectParticleOption::color,
            RiftEffectParticleOption::new
    );

    @Override
    public ParticleType<? extends RiftEffectParticleOption> getType() {
        return ParticleTypeRegistry.RIFT_EFFECT.get();
    }

}
