package me.emafire003.dev.lightwithin.entities.earth_golem;

import me.emafire003.dev.lightwithin.LightWithin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(value= EnvType.CLIENT)
@SuppressWarnings("all")
public class EarthGolemEntityRenderer extends MobEntityRenderer<EarthGolemEntity, EarthGolemEntityModel<EarthGolemEntity>> {
    private static final Identifier TEXTURE = new Identifier(LightWithin.MOD_ID, "textures/entity/earth_golem/earth_golem.png");

    public EarthGolemEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new EarthGolemEntityModel(context.getPart(EntityModelLayers.IRON_GOLEM)), 0.7f);
        this.addFeature(new EarthGolemCrackFeatureRenderer(this));
        this.addFeature(new EarthGolemFlowerFeatureRenderer(this, context.getBlockRenderManager()));
    }

    @Override
    public Identifier getTexture(EarthGolemEntity earthGolemEntity) {
        return TEXTURE;
    }

    @Override
    protected void setupTransforms(EarthGolemEntity earthGolemEntity, MatrixStack matrixStack, float f, float g, float h) {
        super.setupTransforms(earthGolemEntity, matrixStack, f, g, h);
        if (!((double)earthGolemEntity.limbAnimator.getSpeed() < 0.01D)) {
            float i = 13.0F;
            float j = earthGolemEntity.limbAnimator.getPos(h) + 6.0F;
            float k = (Math.abs(j % 13.0F - 6.5F) - 3.25F) / 3.25F;
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(6.5F * k));
        }
    }
}

