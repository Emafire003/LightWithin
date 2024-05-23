package me.emafire003.dev.lightwithin.entities.earth_golem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.IronGolemCrackFeatureRenderer;

@Environment(value= EnvType.CLIENT)
public class EarthGolemCrackFeatureRenderer
        extends IronGolemCrackFeatureRenderer {

    public EarthGolemCrackFeatureRenderer(EarthGolemEntityRenderer featureRendererContext) {
        super(featureRendererContext);
    }
}
