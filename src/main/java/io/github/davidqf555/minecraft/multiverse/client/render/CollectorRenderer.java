package io.github.davidqf555.minecraft.multiverse.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.EvokerRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.SpellcasterIllager;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CollectorRenderer<T extends SpellcasterIllager> extends IllagerRenderer<T, EvokerRenderState> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "textures/entity/collector.png");

    public CollectorRenderer(EntityRendererProvider.Context manager) {
        super(manager, new IllagerModel<>(manager.bakeLayer(ModelLayers.ILLUSIONER)), 0.5f);
        addLayer(new ItemInHandLayer<>(this, manager.getItemRenderer()) {
            @Override
            public void render(PoseStack p_117193_, MultiBufferSource p_117194_, int p_117195_, EvokerRenderState state, float p_117197_, float p_117198_) {
                if (state.isCastingSpell || state.isAggressive) {
                    super.render(p_117193_, p_117194_, p_117195_, state, p_117197_, p_117198_);
                }
            }

        });
        model.getHat().visible = true;
    }

    @Override
    public void extractRenderState(T entity, EvokerRenderState state, float partial) {
        super.extractRenderState(entity, state, partial);
        state.isCastingSpell = entity.isCastingSpell();
    }

    @Override
    public EvokerRenderState createRenderState() {
        return new EvokerRenderState();
    }

    @Override
    public ResourceLocation getTextureLocation(EvokerRenderState state) {
        return TEXTURE;
    }

}
