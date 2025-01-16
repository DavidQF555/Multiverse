package io.github.davidqf555.minecraft.multiverse.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.entities.ConquerorEntity;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class ConquerorRenderer extends IllagerRenderer<ConquerorEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Multiverse.MOD_ID, "textures/entity/conqueror.png");

    public ConquerorRenderer(EntityRendererProvider.Context manager) {
        super(manager, new IllagerModel<>(manager.bakeLayer(ModelLayers.VINDICATOR)), 0.5f);
        addLayer(new ItemInHandLayer<>(this) {
            @Override
            public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, ConquerorEntity pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
                if (!pLivingEntity.isCastingSpell()) {
                    super.render(pMatrixStack, pBuffer, pPackedLight, pLivingEntity, pLimbSwing, pLimbSwingAmount, pPartialTicks, pAgeInTicks, pNetHeadYaw, pHeadPitch);
                }
            }
        });
        getModel().getHat().visible = true;
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull ConquerorEntity traveler) {
        return TEXTURE;
    }

}
