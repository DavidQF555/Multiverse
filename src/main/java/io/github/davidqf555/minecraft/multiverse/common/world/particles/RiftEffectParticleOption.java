package io.github.davidqf555.minecraft.multiverse.common.world.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.registration.ParticleTypeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Locale;

public record RiftEffectParticleOption(ResourceKey<Level> color) implements ParticleOptions {

    public static final Codec<RiftEffectParticleOption> CODEC = ResourceKey.codec(Registry.DIMENSION_REGISTRY).fieldOf("color").codec().xmap(RiftEffectParticleOption::new, RiftEffectParticleOption::color);
    @SuppressWarnings("deprecation")
    public static final Deserializer<RiftEffectParticleOption> DESERIALIZER = new Deserializer<>() {
        @Override
        public RiftEffectParticleOption fromCommand(ParticleType<RiftEffectParticleOption> pParticleType, StringReader pReader) throws CommandSyntaxException {
            pReader.expect(' ');
            String color = pReader.readString();
            return new RiftEffectParticleOption(ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(color)));
        }

        @Override
        public RiftEffectParticleOption fromNetwork(ParticleType<RiftEffectParticleOption> pParticleType, FriendlyByteBuf pBuffer) {
            ResourceLocation color = pBuffer.readResourceLocation();
            return new RiftEffectParticleOption(ResourceKey.create(Registry.DIMENSION_REGISTRY, color));
        }
    };

    @Override
    public ParticleType<? extends RiftEffectParticleOption> getType() {
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
