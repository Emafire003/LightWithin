package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DrownedEntity.class)
public abstract class DrownedEntityMixin extends ZombieEntity implements RangedAttackMob {

    public DrownedEntityMixin(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "canDrownedAttackTarget", at = @At("HEAD"), cancellable = true)
    public void canAttackIfNotAquaLight(LivingEntity target, CallbackInfoReturnable<Boolean> cir){
        if(target != null){
            if(target instanceof PlayerEntity && (target.hasStatusEffect(LightEffects.LIGHT_ACTIVE) || target.hasStatusEffect(LightEffects.LIGHT_FATIGUE))){
                LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);
                cir.setReturnValue(!component.getType().equals(InnerLightType.AQUA));
            }
        }
    }


}
