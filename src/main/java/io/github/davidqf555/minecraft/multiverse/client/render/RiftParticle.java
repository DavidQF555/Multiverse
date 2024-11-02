package io.github.davidqf555.minecraft.multiverse.client.render;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RiftParticle extends SimpleAnimatedParticle {

    public RiftParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites) {
        super(world, x, y, z, sprites, 0);
        lifetime = 10 + random.nextInt(6);
        hasPhysics = false;
        scale(10);
        setSpriteFromAge(sprites);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dX, double dY, double dZ) {
            RiftParticle particle = new RiftParticle(level, x, y, z, sprites);
            particle.setColor((float) dX, (float) dY, (float) dZ);
            return particle;
        }

    }

}
