package me.emafire003.dev.lightwithin.mixin.light_triggers;

import me.emafire003.dev.lightwithin.events.EntityDeathEvent;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.emafire003.dev.lightwithin.status_effects.ForestAuraEffect.sendGlowEntitiesPacket;

@Mixin(LivingEntity.class)
public abstract class EntityDeathMixin extends Entity implements Attackable {

    public EntityDeathMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

    @Inject(method = "onDeath", at = @At("HEAD"))
    public void injectOnDeath(DamageSource damageSource, CallbackInfo ci) {
        EntityDeathEvent.EVENT.invoker().dead(((LivingEntity) (Object) this), damageSource);
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    public void injectClearForestAuraColor(DamageSource damageSource, CallbackInfo ci) {
        if(!this.isRemoved() && this.hasStatusEffect(LightEffects.FOREST_AURA) && !this.getWorld().isClient()){
            if(((LivingEntity) (Object) this) instanceof PlayerEntity){
                sendGlowEntitiesPacket(((ServerPlayerEntity) (Object) this), null, true);
            }
        }
    }

}
