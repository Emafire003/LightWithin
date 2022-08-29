package me.emafire003.dev.lightwithin.entities.earth_golem;

import com.google.common.collect.ImmutableMap;
import me.emafire003.dev.lightwithin.LightWithin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.IronGolemCrackFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.Map;

@Environment(value= EnvType.CLIENT)
public class EarthGolemCrackFeatureRenderer
        extends FeatureRenderer<EarthGolemEntity, EarthGolemEntityModel<EarthGolemEntity>> {
    private static final Map<EarthGolemEntity.Crack, Identifier> DAMAGE_TO_TEXTURE = ImmutableMap.of(EarthGolemEntity.Crack.LOW, new Identifier(LightWithin.MOD_ID, "textures/entity/earth_golem/earth_golem_crackiness_low.png"), EarthGolemEntity.Crack.MEDIUM, new Identifier(LightWithin.MOD_ID, "textures/entity/earth_golem/earth_golem_crackiness_medium.png"), EarthGolemEntity.Crack.HIGH, new Identifier(LightWithin.MOD_ID, "textures/entity/earth_golem/earth_golem_crackiness_high.png"));

    public EarthGolemCrackFeatureRenderer(FeatureRendererContext<EarthGolemEntity, EarthGolemEntityModel<EarthGolemEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, EarthGolemEntity ironGolemEntity, float f, float g, float h, float j, float k, float l) {
        if (ironGolemEntity.isInvisible()) {
            return;
        }
        EarthGolemEntity.Crack crack = ironGolemEntity.getCrack();
        if (crack == EarthGolemEntity.Crack.NONE) {
            return;
        }
        Identifier identifier = DAMAGE_TO_TEXTURE.get(crack);
        IronGolemCrackFeatureRenderer.renderModel(this.getContextModel(), identifier, matrixStack, vertexConsumerProvider, i, ironGolemEntity, 1.0f, 1.0f, 1.0f);
    }
}
