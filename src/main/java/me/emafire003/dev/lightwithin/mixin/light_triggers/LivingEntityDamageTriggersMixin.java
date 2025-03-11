package me.emafire003.dev.lightwithin.mixin.light_triggers;

import me.emafire003.dev.lightwithin.events.*;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityDamageTriggersMixin extends Entity implements Attackable {

    @Shadow public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

    public LivingEntityDamageTriggersMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "damage",
            at = @At("HEAD")
    )
    private void damageTriggers(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getName().equalsIgnoreCase("freeze") && !this.hasStatusEffect(LightEffects.FREEZE_RESISTANCE)) {
            EntityFreezingEvent.EVENT.invoker().freezing(this);
        }else if(source.isOf(DamageTypes.ON_FIRE)){
            EntityBurningEvent.EVENT.invoker().burning(this);
        }else if(source.isOf(DamageTypes.EXPLOSION)){
            //Trigger damage from explosion
        }else if(source.isOf(DamageTypes.PLAYER_EXPLOSION)){
            Entity attacker = source.getAttacker();
            if(attacker instanceof LivingEntity){
                EntityAttackEntityEvent.EVENT.invoker().attack((LivingEntity) attacker, ((LivingEntity) (Object) this));
            }
            //Trigger damage from explosion ignited by player
        }else if(source.isOf(DamageTypes.FALLING_BLOCK)){
            //trigger anvil damage
        }
        else if(source.isOf(DamageTypes.IN_WALL)){
            //trigger suffocation damage
        }
        else if(source.isOf(DamageTypes.MAGIC)){
            //trigger magical damage
        }else if(source.isOf(DamageTypes.MOB_PROJECTILE)){
            //trigger projectile damage
        }else if(source.isOf(DamageTypes.LIGHTNING_BOLT)){
            EntityStruckByLightningEvent.EVENT.invoker().lightningBolted(this);
            //trigger damage from lightning
        }else if(source.isOf(DamageTypes.DROWN)){
            EntityDrowningEvent.EVENT.invoker().drowning(this);
        }else if(source.isOf(DamageTypes.STARVE)){
            //trigger damage from starving
        }else if(source.isOf(DamageTypes.FLY_INTO_WALL)){
            //wait what? well triggers when people splat into walls
        }else if(source.isOf(DamageTypes.WITHER)){
            //trigger damage from withering
        }
    }
}
