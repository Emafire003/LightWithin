package me.emafire003.dev.lightwithin.entities.earth_golem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.IronGolemFlowerFeatureRenderer;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.entity.passive.IronGolemEntity;

@Environment(value= EnvType.CLIENT)
@SuppressWarnings("all")
public class EarthGolemFlowerFeatureRenderer extends IronGolemFlowerFeatureRenderer {

    public EarthGolemFlowerFeatureRenderer(FeatureRendererContext<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> context, BlockRenderManager blockRenderManager) {
        super(context, blockRenderManager);
    }
}
