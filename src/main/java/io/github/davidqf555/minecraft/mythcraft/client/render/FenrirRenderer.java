package io.github.davidqf555.minecraft.mythcraft.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.davidqf555.minecraft.mythcraft.MythCraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class FenrirRenderer extends WolfRenderer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(MythCraft.MOD_ID, "textures/entity/fenrir_entity.png");

    public FenrirRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public void render(WolfEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn) {
        float scale = entityIn.getRenderScale();
        matrixStackIn.push();
        matrixStackIn.scale(scale, scale, scale);
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.pop();
    }

    @Nonnull
    @Override
    public ResourceLocation getEntityTexture(WolfEntity entity) {
        return TEXTURE;
    }
}
