package io.github.davidqf555.minecraft.multiverse.client.render;

import io.github.davidqf555.minecraft.multiverse.common.entities.DoppelgangerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

public class DoppelgangerRenderer extends HumanoidMobRenderer<DoppelgangerEntity, PlayerModel<DoppelgangerEntity>> {

    public DoppelgangerRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5f);
        addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
        addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        addLayer(new ArrowLayer<>(context, this));
        addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
        addLayer(new ElytraLayer<>(this, context.getModelSet()));
        addLayer(new SpinAttackEffectLayer<>(this, context.getModelSet()));
        addLayer(new BeeStingerLayer<>(this));
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(DoppelgangerEntity entity) {
        UUID id = entity.getOriginalId();
        if (id != null) {
            PlayerInfo info = Minecraft.getInstance().getConnection().getPlayerInfo(id);
            return info == null ? DefaultPlayerSkin.getDefaultSkin(id) : info.getSkinLocation();
        }
        return DefaultPlayerSkin.getDefaultSkin();
    }

}
