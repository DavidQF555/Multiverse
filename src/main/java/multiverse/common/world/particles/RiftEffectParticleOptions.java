package multiverse.common.world.particles;

import com.mojang.serialization.MapCodec;
import multiverse.common.util.TagUtil;
import multiverse.registration.ParticleTypeRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record RiftEffectParticleOptions(ResourceKey<Level> color) implements ParticleOptions {

    public static final MapCodec<RiftEffectParticleOptions> CODEC = ResourceKey.codec(Registries.DIMENSION).xmap(RiftEffectParticleOptions::new, RiftEffectParticleOptions::color).fieldOf("color");
    public static final StreamCodec<RegistryFriendlyByteBuf, RiftEffectParticleOptions> STREAM_CODEC = StreamCodec.composite(
            TagUtil.WORLD_CODEC,
            RiftEffectParticleOptions::color,
            RiftEffectParticleOptions::new
    );

    @Override
    public ParticleType<? extends RiftEffectParticleOptions> getType() {
        return ParticleTypeRegistry.RIFT_EFFECT.get();
    }

}
