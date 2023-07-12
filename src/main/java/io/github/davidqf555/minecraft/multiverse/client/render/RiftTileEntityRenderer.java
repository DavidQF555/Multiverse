package io.github.davidqf555.minecraft.multiverse.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.Random;

public class RiftTileEntityRenderer implements BlockEntityRenderer<RiftTileEntity> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(Multiverse.MOD_ID, "textures/block/rift_background.png");
    private static final ResourceLocation PARTICLES = new ResourceLocation(Multiverse.MOD_ID, "textures/block/rift_particles.png");
    private static final Random RANDOM = new Random(0);
    public static ShaderInstance SHADER = null;
    private static final RenderType TYPE = RenderType.create("rift", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder().setShaderState(new RenderStateShard.ShaderStateShard(() -> SHADER)).setTextureState(RenderStateShard.MultiTextureStateShard.builder().add(BACKGROUND, false, false).add(PARTICLES, false, false).build()).createCompositeState(false));

    @Override
    public void render(RiftTileEntity entity, float partial, PoseStack matrixStack, MultiBufferSource buffer, int overlay, int packedLight) {
        int world = entity.getTarget();
        RANDOM.setSeed(entity.getLevel().getBiomeManager().biomeZoomSeed + world);
        float red = RANDOM.nextFloat();
        float green = RANDOM.nextFloat();
        float blue = RANDOM.nextFloat();
        Matrix4f pose = matrixStack.last().pose();
        renderCube(pose, buffer.getBuffer(TYPE), red, green, blue);
    }

    private void renderCube(Matrix4f matrix, VertexConsumer buffer, float red, float green, float blue) {
        this.renderFace(matrix, buffer, 0, 1, 0, 1, 1, 1, 1, 1, red, green, blue);
        this.renderFace(matrix, buffer, 0, 1, 1, 0, 0, 0, 0, 0, red, green, blue);
        this.renderFace(matrix, buffer, 1, 1, 1, 0, 0, 1, 1, 0, red, green, blue);
        this.renderFace(matrix, buffer, 0, 0, 0, 1, 0, 1, 1, 0, red, green, blue);
        this.renderFace(matrix, buffer, 0, 1, 0, 0, 0, 0, 1, 1, red, green, blue);
        this.renderFace(matrix, buffer, 0, 1, 1, 1, 1, 1, 0, 0, red, green, blue);
    }

    private void renderFace(Matrix4f p_173696_, VertexConsumer p_173697_, float p_173698_, float p_173699_, float p_173700_, float p_173701_, float p_173702_, float p_173703_, float p_173704_, float p_173705_, float red, float green, float blue) {
        p_173697_.vertex(p_173696_, p_173698_, p_173700_, p_173702_).color(red, green, blue, 1).endVertex();
        p_173697_.vertex(p_173696_, p_173699_, p_173700_, p_173703_).color(red, green, blue, 1).endVertex();
        p_173697_.vertex(p_173696_, p_173699_, p_173701_, p_173704_).color(red, green, blue, 1).endVertex();
        p_173697_.vertex(p_173696_, p_173698_, p_173701_, p_173705_).color(red, green, blue, 1).endVertex();
    }

}