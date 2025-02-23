package io.github.davidqf555.minecraft.multiverse.client.particles;

import io.github.davidqf555.minecraft.multiverse.client.ClientConfigs;
import io.github.davidqf555.minecraft.multiverse.client.ShaderHelper;
import io.github.davidqf555.minecraft.multiverse.client.colors.MultiverseColorHelper;
import io.github.davidqf555.minecraft.multiverse.common.world.particles.RiftEffectParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.FastColor;
import org.jetbrains.annotations.Nullable;

public class RiftEffectParticle extends TextureSheetParticle {

    private final double xStart, yStart, zStart;

    public RiftEffectParticle(ClientLevel pLevel, double x, double y, double z) {
        super(pLevel, x, y, z);
        xStart = x;
        yStart = y;
        zStart = z;
        quadSize = random.nextFloat() * 0.02f + 0.05f;
        lifetime = random.nextInt(10) + 40;
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        if (age++ >= lifetime) {
            remove();
        } else {
            double time = (double) age / lifetime;
            double factor = time * time;
            x = xStart + xd * factor;
            y = yStart + yd * factor;
            z = zStart + zd * factor;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ClientConfigs.INSTANCE.vanillaOnly.get() ? ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT : ShaderHelper.RIFT_PARTICLE_TYPE;
    }

    public static class Provider implements ParticleProvider<RiftEffectParticleOptions> {

        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Nullable
        @Override
        public Particle createParticle(RiftEffectParticleOptions pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            RiftEffectParticle particle = new RiftEffectParticle(pLevel, pX, pY, pZ);
            particle.pickSprite(sprites);
            particle.setParticleSpeed(pXSpeed, pYSpeed, pZSpeed);
            int color = MultiverseColorHelper.getColors(pLevel, pType.color(), 1)[0];
            particle.setColor(FastColor.ARGB32.red(color) / 255f, FastColor.ARGB32.green(color) / 255f, FastColor.ARGB32.blue(color) / 255f);
            particle.setAlpha((float) (double) ClientConfigs.INSTANCE.riftMaxOpacity.get());
            return particle;
        }

    }

}
