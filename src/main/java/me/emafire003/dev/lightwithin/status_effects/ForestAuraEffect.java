package me.emafire003.dev.lightwithin.status_effects;

import me.emafire003.dev.lightwithin.compat.coloredglowlib.CGLCompat;
import me.emafire003.dev.lightwithin.lights.ForestAuraLight;
import me.emafire003.dev.lightwithin.networking.GlowEntitiesPacketS2C;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.emafire003.dev.lightwithin.LightWithin.BOX_EXPANSION_AMOUNT;


/**
 * Players will be able to pass through natural blocks, the one in the tag list,
 * (TODO) will not suffocate in these
 * and will also see the entities that are nearby them when they get the effect for the first time*/
//TODO maybe update the entites list every 5 seconds or something?
public class ForestAuraEffect extends StatusEffect {

    public ForestAuraEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x1BC131);
    }

    List<LivingEntity> visibleEntities = new ArrayList<>();

    // This method is called when it applies the status effect. We implement custom functionality here.
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {

    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);
        if(!(entity instanceof PlayerEntity)){
            return;
        }
        visibleEntities = entity.getWorld().getEntitiesByClass(LivingEntity.class, new Box(entity.getBlockPos()).expand(BOX_EXPANSION_AMOUNT*1.5), (entity1 -> !entity.equals(entity1)));

        List<UUID> uuids = new ArrayList<>();
        visibleEntities.forEach(entity1 -> {
            uuids.add(entity1.getUuid());
            if(FabricLoader.getInstance().isModLoaded("coloredglowlib")){
                CGLCompat.getLib().setExclusiveColorFor(entity1, ForestAuraLight.COLOR, (PlayerEntity) entity);
            }
        });
        if(!entity.getWorld().isClient()){
            GlowEntitiesPacketS2C glowingPacket = new GlowEntitiesPacketS2C(uuids, false);
            ServerPlayNetworking.send((ServerPlayerEntity) entity, GlowEntitiesPacketS2C.ID, glowingPacket);
        }

    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier){
        super.onRemoved(entity, attributes, amplifier);
        if(FabricLoader.getInstance().isModLoaded("coloredglowlib")){
            visibleEntities.forEach(entity1 -> CGLCompat.getLib().clearExclusiveColorFor(entity1, (PlayerEntity) entity, true));
        }
        if(!entity.getWorld().isClient()){
            GlowEntitiesPacketS2C glowingPacket = new GlowEntitiesPacketS2C(null, true);
            ServerPlayNetworking.send((ServerPlayerEntity) entity, GlowEntitiesPacketS2C.ID, glowingPacket);
        }
    }


}
