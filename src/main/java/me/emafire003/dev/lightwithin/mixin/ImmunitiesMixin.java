package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.lights.ThunderAuraLight;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class ImmunitiesMixin extends Entity implements Attackable {

    public ImmunitiesMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    //Freeze Resistance effect
    @Inject(
            method = "damage",
            at = @At("HEAD"),
            cancellable = true
    )
    private void makeFreezeImmune(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getName().equalsIgnoreCase("freeze") && this.hasStatusEffect(LightEffects.FREEZE_RESISTANCE)) {
            cir.setReturnValue(false);
        }
    }

    //Lightning immunity
    @Inject(
            method = "damage",
            at = @At("HEAD"),
            cancellable = true
    )
    private void makeLightningImmune(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if(source.isOf(DamageTypes.LIGHTNING_BOLT)){
            if(this.hasStatusEffect(LightEffects.THUNDER_AURA)){
                cir.setReturnValue(false);
            }
            if(this.hasStatusEffect(LightEffects.LIGHT_ACTIVE) && LightWithin.LIGHT_COMPONENT.get(this).getType() instanceof ThunderAuraLight){
                cir.setReturnValue(false);
            }
        }
    }
}
