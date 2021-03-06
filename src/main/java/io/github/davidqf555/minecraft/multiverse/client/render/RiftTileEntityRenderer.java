package io.github.davidqf555.minecraft.multiverse.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RiftTileEntityRenderer extends TileEntityRenderer<RiftTileEntity> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(Multiverse.MOD_ID, "textures/block/rift_background.png");
    private static final ResourceLocation PARTICLES = new ResourceLocation(Multiverse.MOD_ID, "textures/block/rift_particles.png");
    private static final List<RenderType> LAYERS = IntStream.range(1, 16).mapToObj(RiftTileEntityRenderer::createRenderType).collect(Collectors.toList());

    public RiftTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    private static RenderType createRenderType(int layer) {
        RenderState.TransparencyState transparency;
        RenderState.TextureState texture;
        if (layer == 1) {
            transparency = RenderState.TRANSLUCENT_TRANSPARENCY;
            texture = new RenderState.TextureState(BACKGROUND, false, false);
        } else {
            transparency = RenderState.ADDITIVE_TRANSPARENCY;
            texture = new RenderState.TextureState(PARTICLES, false, false);
        }
        return RenderType.create("rift", DefaultVertexFormats.POSITION_COLOR, 7, 256, false, true, RenderType.State.builder().setTransparencyState(transparency).setTextureState(texture).setTexturingState(new RenderState.PortalTexturingState(layer + 8)).setFogState(RenderState.BLACK_FOG).createCompositeState(false));
    }

    @Override
    public void render(RiftTileEntity entity, float partial, MatrixStack matrixStack, IRenderTypeBuffer buffer, int overlay, int packedLight) {
        double distSq = entity.getBlockPos().distSqr(renderer.camera.getPosition(), true);
        Random rand = entity.getColorsRandom();
        Pair<Integer, Integer> colors = entity.getColors(rand);
        int layers = getLayers(distSq);
        Matrix4f pose = matrixStack.last().pose();
        int color = colors.getFirst();
        renderCube(pose, buffer.getBuffer(LAYERS.get(0)), ColorHelper.PackedColor.red(color) / 255f, ColorHelper.PackedColor.green(color) / 255f, ColorHelper.PackedColor.blue(color) / 255f, ColorHelper.PackedColor.alpha(color) / 255f);
        int particles = colors.getSecond();
        float pRed = ColorHelper.PackedColor.red(particles) / 255f;
        float pGreen = ColorHelper.PackedColor.green(particles) / 255f;
        float pBlue = ColorHelper.PackedColor.blue(particles) / 255f;
        float pAlpha = ColorHelper.PackedColor.alpha(particles) / 255f;
        for (int layer = 1; layer < layers; layer++) {
            renderCubeOffsetColors(rand, pose, buffer.getBuffer(LAYERS.get(layer)), pRed, pGreen, pBlue, pAlpha, 2f / (LAYERS.size() - layer + 1));
        }
    }

    private void renderCubeOffsetColors(Random rand, Matrix4f matrix, IVertexBuilder builder, float oRed, float oGreen, float oBlue, float oAlpha, float factor) {
        float red = rand.nextFloat() * factor + oRed;
        float green = rand.nextFloat() * factor + oGreen;
        float blue = rand.nextFloat() * factor + oBlue;
        renderCube(matrix, builder, red, blue, green, oAlpha);
    }

    private void renderCube(Matrix4f matrix, IVertexBuilder builder, float red, float green, float blue, float alpha) {
        renderFace(matrix, builder, 0, 1, 0, 1, 1, 1, 1, 1, red, green, blue, alpha);
        renderFace(matrix, builder, 0, 1, 1, 0, 0, 0, 0, 0, red, green, blue, alpha);
        renderFace(matrix, builder, 1, 1, 1, 0, 0, 1, 1, 0, red, green, blue, alpha);
        renderFace(matrix, builder, 0, 0, 0, 1, 0, 1, 1, 0, red, green, blue, alpha);
        renderFace(matrix, builder, 0, 1, 0, 0, 0, 0, 1, 1, red, green, blue, alpha);
        renderFace(matrix, builder, 0, 1, 1, 1, 1, 1, 0, 0, red, green, blue, alpha);
    }

    private void renderFace(Matrix4f matrix, IVertexBuilder builder, float startX, float endX, float startY, float endY, float z1, float z2, float z3, float z4, float red, float green, float blue, float alpha) {
        builder.vertex(matrix, startX, startY, z1).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, endX, startY, z2).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, endX, endY, z3).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, startX, endY, z4).color(red, green, blue, alpha).endVertex();
    }

    protected int getLayers(double distSq) {
        if (distSq > 36864) {
            return 1;
        } else if (distSq > 25600) {
            return 3;
        } else if (distSq > 16384) {
            return 5;
        } else if (distSq > 9216) {
            return 7;
        } else if (distSq > 4096) {
            return 9;
        } else if (distSq > 1024) {
            return 11;
        } else if (distSq > 576) {
            return 13;
        } else if (distSq > 256) {
            return 14;
        } else {
            return 15;
        }
    }

}