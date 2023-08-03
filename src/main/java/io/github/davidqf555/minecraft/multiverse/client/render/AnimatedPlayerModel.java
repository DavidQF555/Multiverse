package io.github.davidqf555.minecraft.multiverse.client.render;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class AnimatedPlayerModel<T extends LivingEntity> extends PlayerModel<T> {

    public AnimatedPlayerModel(ModelPart p_170821_, boolean p_170822_) {
        super(p_170821_, p_170822_);
    }

    @Override
    public void prepareMobModel(T entity, float p_102862_, float p_102863_, float p_102864_) {
        HumanoidModel.ArmPose main = entity.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
        HumanoidModel.ArmPose off = entity.getItemInHand(InteractionHand.OFF_HAND).isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
        if (main.isTwoHanded()) {
            off = entity.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
        }
        if (entity.getMainArm() == HumanoidArm.RIGHT) {
            rightArmPose = main;
            leftArmPose = off;
        } else {
            rightArmPose = off;
            leftArmPose = main;
        }
        super.prepareMobModel(entity, p_102862_, p_102863_, p_102864_);
    }

}
