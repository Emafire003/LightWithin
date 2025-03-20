package me.emafire003.dev.lightwithin.mixin.light_triggers;

import me.emafire003.dev.lightwithin.events.EntityDeathEvent;
import me.emafire003.dev.lightwithin.networking.GlowEntitiesPacketS2C;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class EntityDeathMixin extends Entity implements Attackable {

    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    public EntityDeathMixin(EntityType<?> type, World world) {
        super(type, world);
    }


    @Inject(method = "onDeath", at = @At("HEAD"))
    public void injectOnDeath(DamageSource damageSource, CallbackInfo ci) {
        EntityDeathEvent.EVENT.invoker().dead(((LivingEntity) (Object) this), damageSource);
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    public void injectClearForestAuraColor(DamageSource damageSource, CallbackInfo ci) {
        if(!this.isRemoved() && this.hasStatusEffect(LightEffects.FOREST_AURA) && !this.getWorld().isClient()){
            if(((LivingEntity) (Object) this) instanceof PlayerEntity){
                GlowEntitiesPacketS2C glowingPacket = new GlowEntitiesPacketS2C(null, true);
                ServerPlayNetworking.send(((ServerPlayerEntity) (Object) this), GlowEntitiesPacketS2C.ID, glowingPacket);
            }
        }
    }

}
