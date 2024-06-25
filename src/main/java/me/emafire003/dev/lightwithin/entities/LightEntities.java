package me.emafire003.dev.lightwithin.entities;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.entities.earth_golem.EarthGolemEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class LightEntities {
    public static final EntityType<EarthGolemEntity> EARTH_GOLEM = Registry.register(
            Registries.ENTITY_TYPE,
            LightWithin.getIdentifier("earth_golem"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, EarthGolemEntity::new).dimensions(EntityDimensions.fixed(1.4f, 2.7f)).trackRangeBlocks(10).build()
    );

    @SuppressWarnings("all")
    public static void registerEntities(){
        FabricDefaultAttributeRegistry.register(EARTH_GOLEM, EarthGolemEntity.createEarthGolemAttributes());
    }
}
