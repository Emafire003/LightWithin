package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.items.BottledLightItem;
import me.emafire003.dev.lightwithin.items.LightItems;
import me.emafire003.dev.lightwithin.networking.LightReadyPacketS2C;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GlassBottleItem.class)
public abstract class BottleLightMixin {

    @Shadow public abstract TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand);
    @Shadow protected abstract ItemStack fill(ItemStack stack, PlayerEntity player, ItemStack outputStack);

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void injectRefillLightOnUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir){
        //This is done to prevent the player from bottling up their first naturally triggered light
        if(!LightWithin.LIGHT_COMPONENT.get(user).hasTriggeredNaturally()){
            return;
        }

        if(LightWithin.CURRENTLY_READY_LIGHT_PLAYER_CACHE.containsKey(user.getUuid()) && !world.isClient()){
            if(!world.isClient()){
                LightParticlesUtil.spawnLightBottledUpEffect((ServerPlayerEntity) user);
                ServerPlayNetworking.send((ServerPlayerEntity) user, LightReadyPacketS2C.ID, new LightReadyPacketS2C(false));
                LightWithin.CURRENTLY_READY_LIGHT_PLAYER_CACHE.remove(user.getUuid());
            }
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1.0f, 1.3f);

            user.addStatusEffect(new StatusEffectInstance(LightEffects.LIGHT_FATIGUE, (int) (Config.COOLDOWN_MULTIPLIER*20*LightWithin.LIGHT_COMPONENT.get(user).getMaxCooldown()/2)));

            ItemStack bottledLight = new ItemStack(LightItems.BOTTLED_LIGHT);
            BottledLightItem.setCreatedBy(user, bottledLight);

            cir.setReturnValue(TypedActionResult.success(fill(user.getStackInHand(hand), user, bottledLight), world.isClient()));
        }
    }
}
