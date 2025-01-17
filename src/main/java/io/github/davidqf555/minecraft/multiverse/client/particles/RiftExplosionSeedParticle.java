package io.github.davidqf555.minecraft.multiverse.client.particles;

import io.github.davidqf555.minecraft.multiverse.client.ClientConfigs;
import io.github.davidqf555.minecraft.multiverse.registration.ParticleTypeRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;

public class RiftExplosionSeedParticle extends NoRenderParticle {

    private static final int LIFETIME = 8;
    private final double red, green, blue;

    protected RiftExplosionSeedParticle(ClientLevel pLevel, double pX, double pY, double pZ, double red, double green, double blue) {
        super(pLevel, pX, pY, pZ, 0, 0, 0);
        hasPhysics = false;
        this.red = red;
        this.green = green;
        this.blue = blue;
        lifetime = LIFETIME;
    }

    @Override
    protected int getLightColor(float pPartialTick) {
        return 0xF000F0;
    }

    @Override
    public void tick() {
        super.tick();
        double range = ClientConfigs.INSTANCE.riftExplosionParticleRange.get();
        for (int i = 0; i < ClientConfigs.INSTANCE.riftExplosionParticles.get(); ++i) {
            double pX = x + (random.nextDouble() - random.nextDouble()) * range;
            double pY = y + (random.nextDouble() - random.nextDouble()) * range;
            double pZ = z + (random.nextDouble() - random.nextDouble()) * range;
            level.addParticle(ParticleTypeRegistry.RIFT_EXPLOSION.get(), pX, pY, pZ, red, green, blue);
        }
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new RiftExplosionSeedParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        }

    }

}
