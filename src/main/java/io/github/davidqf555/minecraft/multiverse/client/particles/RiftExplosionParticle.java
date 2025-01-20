package io.github.davidqf555.minecraft.multiverse.client.particles;

import io.github.davidqf555.minecraft.multiverse.client.ClientConfigs;
import io.github.davidqf555.minecraft.multiverse.client.ShaderHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class RiftExplosionParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    protected RiftExplosionParticle(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ);
        sprites = pSprites;
        hasPhysics = false;
        setAlpha((float) (double) ClientConfigs.INSTANCE.riftMaxOpacity.get());
        lifetime = 6 + random.nextInt(4);
        quadSize = 2 - random.nextFloat();
        setSpriteFromAge(pSprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ClientConfigs.INSTANCE.vanillaOnly.get() ? ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT : ShaderHelper.RIFT_PARTICLE_TYPE;
    }

    @Override
    public void tick() {
        super.tick();
        setSpriteFromAge(sprites);
    }

    @Override
    protected int getLightColor(float pPartialTick) {
        return 0xF000F0;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dX, double dY, double dZ) {
            RiftExplosionParticle particle = new RiftExplosionParticle(level, x, y, z, sprites);
            particle.setColor((float) dX, (float) dY, (float) dZ);
            return particle;
        }

    }

}
