package multiverse.client.particles;

import multiverse.client.ClientConfigs;
import multiverse.client.ShaderHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RiftParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    protected RiftParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites) {
        super(world, x, y, z);
        lifetime = 10 + random.nextInt(6);
        hasPhysics = false;
        this.sprites = sprites;
        scale(10);
        setAlpha((float) (double) ClientConfigs.INSTANCE.riftMaxOpacity.get());
        setSpriteFromAge(sprites);
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
            RiftParticle particle = new RiftParticle(level, x, y, z, sprites);
            particle.setColor((float) dX, (float) dY, (float) dZ);
            return particle;
        }

    }

}
