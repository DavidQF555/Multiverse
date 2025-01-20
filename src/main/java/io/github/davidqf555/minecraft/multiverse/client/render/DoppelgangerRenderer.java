package io.github.davidqf555.minecraft.multiverse.client.render;

import io.github.davidqf555.minecraft.multiverse.common.world.entities.DoppelgangerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

public class DoppelgangerRenderer extends HumanoidMobRenderer<DoppelgangerEntity, PlayerRenderState, PlayerModel> {

    public DoppelgangerRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel(context.bakeLayer(ModelLayers.PLAYER), false), 0.5f);
        addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getEquipmentRenderer()));
        addLayer(new ItemInHandLayer<>(this));
        addLayer(new ArrowLayer<>(this, context));
        addLayer(new CustomHeadLayer<>(this, context.getModelSet()));
        addLayer(new WingsLayer<>(this, context.getModelSet(), context.getEquipmentRenderer()));
        addLayer(new SpinAttackEffectLayer(this, context.getModelSet()));
        addLayer(new BeeStingerLayer<>(this, context));
    }

    @Nonnull
    @Override
    public PlayerRenderState createRenderState() {
        return new PlayerRenderState();
    }

    @Override
    public ResourceLocation getTextureLocation(PlayerRenderState state) {
        return state.skin.texture();
    }

    @Override
    public void extractRenderState(DoppelgangerEntity entity, PlayerRenderState state, float partial) {
        super.extractRenderState(entity, state, partial);
        UUID id = entity.getOriginalId();
        if (id != null) {
            PlayerInfo info = Minecraft.getInstance().getConnection().getPlayerInfo(id);
            if (info != null) {
                state.skin = info.getSkin();
                return;
            }
        }
        state.skin = DefaultPlayerSkin.getDefaultSkin();
        state.arrowCount = entity.getArrowCount();
        state.stingerCount = entity.getStingerCount();
    }

}
