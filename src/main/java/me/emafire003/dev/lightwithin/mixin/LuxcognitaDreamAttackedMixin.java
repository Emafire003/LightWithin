package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.items.LightItems;
import me.emafire003.dev.lightwithin.networking.LuxdreamAttackScreenPacketS2C;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.IStatusEffectWithSettableDuration;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LuxcognitaDreamAttackedMixin extends Entity implements Attackable {

    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    @Shadow public abstract StatusEffectInstance getStatusEffect(StatusEffect effect);

    @Shadow public abstract EntityDimensions getDimensions(EntityPose pose);

    @Shadow public abstract Random getRandom();

    public LuxcognitaDreamAttackedMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "damage",
            at = @At("HEAD"),
            cancellable = true)
    private void damageTaken(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if(((LivingEntity) (Object) this) instanceof PlayerEntity){
            if(this.hasStatusEffect(LightEffects.LUXCOGNITA_DREAM)){
                StatusEffectInstance effect = this.getStatusEffect(LightEffects.LUXCOGNITA_DREAM);
                if(effect.getDuration() > 60){
                    /// Starts the clock for the automatic exclusion from the dialogue screen after 3 seconds
                    ((IStatusEffectWithSettableDuration) effect).lightWithin$setDuration(60);
                    if(!this.getWorld().isClient()){
                        ServerPlayNetworking.send(((ServerPlayerEntity) (Object) this), LuxdreamAttackScreenPacketS2C.ID, new LuxdreamAttackScreenPacketS2C());
                    }

                }
                /// Makes it immune to attacks while the effect lasts, so for the next 2 seconds. Also spawns particles and sound effect
                if(!this.getWorld().isClient()){
                    //TODO seems a bit to similar each time, test it out
                    this.getWorld().playSound(null, BlockPos.ofFloored(this.getPos()), LightSounds.LUXCOGNITA_DAMAGE_BLOCK, SoundCategory.PLAYERS, 1f, 1f + (float) this.getRandom().nextBetween(0, 2) /10);

                    ((ServerWorld) this.getWorld()).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(LightItems.LUXCOGNITA_BERRY)),
                            this.getX(), this.getY()+this.getDimensions(this.getPose()).height/2, this.getZ(), 20, 0.2, 0.2, 0.2, 0.1
                            );
                }
                cir.setReturnValue(false);

            }
        }
    }
}
