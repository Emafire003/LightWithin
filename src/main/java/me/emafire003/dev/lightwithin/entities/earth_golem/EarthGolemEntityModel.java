package me.emafire003.dev.lightwithin.entities.earth_golem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.entity.passive.IronGolemEntity;

@Environment(value= EnvType.CLIENT)
public class EarthGolemEntityModel<T extends IronGolemEntity> extends IronGolemEntityModel<T> {
    public EarthGolemEntityModel(ModelPart root) {
        super(root);
    }
}
