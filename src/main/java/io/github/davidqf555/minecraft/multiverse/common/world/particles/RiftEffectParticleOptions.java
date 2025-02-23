package io.github.davidqf555.minecraft.multiverse.common.world.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.registration.ParticleTypeRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Locale;

public record RiftEffectParticleOptions(ResourceKey<Level> color) implements ParticleOptions {

    public static final Codec<RiftEffectParticleOptions> CODEC = ResourceKey.codec(Registries.DIMENSION).fieldOf("color").codec().xmap(RiftEffectParticleOptions::new, RiftEffectParticleOptions::color);
    @SuppressWarnings("deprecation")
    public static final Deserializer<RiftEffectParticleOptions> DESERIALIZER = new Deserializer<>() {
        @Override
        public RiftEffectParticleOptions fromCommand(ParticleType<RiftEffectParticleOptions> pParticleType, StringReader pReader) throws CommandSyntaxException {
            pReader.expect(' ');
            String color = pReader.readString();
            return new RiftEffectParticleOptions(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(color)));
        }

        @Override
        public RiftEffectParticleOptions fromNetwork(ParticleType<RiftEffectParticleOptions> pParticleType, FriendlyByteBuf pBuffer) {
            ResourceLocation color = pBuffer.readResourceLocation();
            return new RiftEffectParticleOptions(ResourceKey.create(Registries.DIMENSION, color));
        }
    };

    @Override
    public ParticleType<? extends RiftEffectParticleOptions> getType() {
        return ParticleTypeRegistry.RIFT_EFFECT.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf pBuffer) {
        pBuffer.writeResourceLocation(color.location());
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %s", ForgeRegistries.PARTICLE_TYPES.getKey(getType()), color.location());
    }

}
