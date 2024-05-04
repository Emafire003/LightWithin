package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.events.EntityAttackEntityEvent;
import me.emafire003.dev.lightwithin.events.EntityBurningEvent;
import me.emafire003.dev.lightwithin.events.EntityDrowningEvent;
import me.emafire003.dev.lightwithin.events.EntityFreezingEvent;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityDamageTriggersMixin {

    @Inject(
            method = "damage",
            at = @At("HEAD")
    )
    private void damageTriggers(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (source.getName().equalsIgnoreCase("freeze") && !entity.hasStatusEffect(LightEffects.FREEZE_RESISTANCE)) {
            EntityFreezingEvent.EVENT.invoker().freezing(entity);
        }else if(source.isOf(DamageTypes.ON_FIRE)){
            EntityBurningEvent.EVENT.invoker().burning(entity);
        }else if(source.isOf(DamageTypes.EXPLOSION)){
            //Trigger damage from explosion
        }else if(source.isOf(DamageTypes.PLAYER_EXPLOSION)){
            //TODO test properly in multiplayer
            Entity attacker = source.getAttacker();
            if(attacker instanceof LivingEntity){
                EntityAttackEntityEvent.EVENT.invoker().attack((LivingEntity) attacker, entity);
            }
            //Trigger damage from explosion ignited by player
        }else if(source.isOf(DamageTypes.FALLING_BLOCK)){
            //trigger anvil damage
        }else if(source.isOf(DamageTypes.MAGIC)){
            //trigger magical damage
        }else if(source.isOf(DamageTypes.MOB_PROJECTILE)){
            //trigger projectile damage
        }else if(source.isOf(DamageTypes.LIGHTNING_BOLT)){
            //trigger damage from lightning
        }else if(source.isOf(DamageTypes.DROWN)){
            EntityDrowningEvent.EVENT.invoker().drowning(entity);
        }else if(source.isOf(DamageTypes.STARVE)){
            //trigger damage from starving
        }else if(source.isOf(DamageTypes.FLY_INTO_WALL)){
            //wait what? well triggers when people splat into walls
        }else if(source.isOf(DamageTypes.WITHER)){
            //trigger damage from withering
        }
    }
}
