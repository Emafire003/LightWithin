package me.emafire003.dev.lightwithin.items;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.networking.PlayRenderEffectPacketS2C;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.RenderEffect;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;
import java.util.List;

public class LuxcognitaBerryItem extends Item {

    public LuxcognitaBerryItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(LightWithin.isPlayerInCooldown(user) && Config.LUXCOGNITA_BYPASS_COOLDOWN){
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        if(user.hasStatusEffect(LightEffects.LUXCOGNITA_OFFENDED)){
            user.sendMessage(Text.translatable("screen.lightwithin.luxcognita.offended.msg").formatted(Formatting.YELLOW), true);
            user.playSound(LightSounds.LUXCOGNITA_DAMAGE_BLOCK, 1f, 2f);
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        if (this.isFood()) {
            ItemStack itemStack = user.getStackInHand(hand);
            if (this.getFoodComponent() != null && user.canConsume(this.getFoodComponent().isAlwaysEdible())) {
                user.setCurrentHand(hand);
                return TypedActionResult.consume(itemStack);
            } else {
                return TypedActionResult.fail(itemStack);
            }
        } else {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if(world.isClient){
            user.playSound(LightSounds.LIGHT_READY, 1, 0.8f);
        }
        if(user instanceof ServerPlayerEntity && !world.isClient){
            if(LightWithin.isPlayerInCooldown((PlayerEntity) user) && Config.LUXCOGNITA_BYPASS_COOLDOWN){
                return stack;
            }
            user.playSound(LightSounds.LIGHT_READY, 1, 0.8f);

            ServerPlayNetworking.send((ServerPlayerEntity) user, PlayRenderEffectPacketS2C.ID, new PlayRenderEffectPacketS2C(RenderEffect.LUXCOGNITA_SCREEN));
            user.addStatusEffect(new StatusEffectInstance(LightEffects.LUXCOGNITA_DREAM, 999999999, 0, false, false));
        }
        return this.isFood() ? user.eatFood(world, stack) : stack;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.lightwithin.luxcognita_berry.tooltip"));
    }

}
