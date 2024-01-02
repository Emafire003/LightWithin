package me.emafire003.dev.lightwithin.entities.earth_golem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

@Environment(value= EnvType.CLIENT)
@SuppressWarnings("all")
public class EarthGolemFlowerFeatureRenderer extends FeatureRenderer<EarthGolemEntity, EarthGolemEntityModel<EarthGolemEntity>> {
    private final BlockRenderManager blockRenderManager;

    public EarthGolemFlowerFeatureRenderer(FeatureRendererContext<EarthGolemEntity, EarthGolemEntityModel<EarthGolemEntity>> context, BlockRenderManager blockRenderManager) {
        super(context);
        this.blockRenderManager = blockRenderManager;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, EarthGolemEntity ironGolemEntity, float f, float g, float h, float j, float k, float l) {
        if (ironGolemEntity.getLookingAtVillagerTicks() == 0) {
            return;
        }
        matrixStack.push();
        ModelPart modelPart = ((EarthGolemEntityModel)this.getContextModel()).getRightArm();
        modelPart.rotate(matrixStack);
        matrixStack.translate(-1.1875, 1.0625, -0.9375);
        matrixStack.translate(0.5, 0.5, 0.5);
        float m = 0.5f;
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
        matrixStack.translate(-0.5, -0.5, -0.5);
        this.blockRenderManager.renderBlockAsEntity(Blocks.POPPY.getDefaultState(), matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
    }
}
