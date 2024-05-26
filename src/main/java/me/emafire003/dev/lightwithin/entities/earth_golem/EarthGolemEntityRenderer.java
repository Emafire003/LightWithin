package me.emafire003.dev.lightwithin.entities.earth_golem;

import me.emafire003.dev.lightwithin.LightWithin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IronGolemEntityRenderer;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;

@Environment(value= EnvType.CLIENT)
@SuppressWarnings("all")
public class EarthGolemEntityRenderer extends IronGolemEntityRenderer {
    private static final Identifier TEXTURE = new Identifier(LightWithin.MOD_ID, "textures/entity/earth_golem/earth_golem.png");

    public EarthGolemEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.addFeature(new EarthGolemCrackFeatureRenderer(this));
        this.addFeature(new EarthGolemFlowerFeatureRenderer(this, context.getBlockRenderManager()));
    }

    @Override
    public Identifier getTexture(IronGolemEntity earthGolemEntity) {
        return TEXTURE;
    }
}

