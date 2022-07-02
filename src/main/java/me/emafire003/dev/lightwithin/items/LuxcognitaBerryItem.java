package me.emafire003.dev.lightwithin.items;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
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
        if (this.isFood()) {
            ItemStack itemStack = user.getStackInHand(hand);
            if (user.canConsume(this.getFoodComponent().isAlwaysEdible())) {
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
            user.playSound(LightSounds.LIGHT_READY, 1, 1);
        }
        if(user instanceof ServerPlayerEntity){
            if(LightWithin.isPlayerInCooldown((PlayerEntity) user) && Config.LUXCOGNITA_BYPASS_COOLDOWN){
                return stack;
            }
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(user);
            InnerLightType type = component.getType();
            user.playSound(LightSounds.LIGHT_READY, 1, 0.8f);
            if(type.equals(InnerLightType.NONE)){
                ((ServerPlayerEntity) user).sendMessage(Text.literal("Uhm... your light is... an error? Light not found, report please! "), true);
            }else if(type.equals(InnerLightType.HEAL)){
                ((ServerPlayerEntity) user).sendMessage(Text.literal("Your light, yes, is a gentle and caring one indeed"), true);
            }else if(type.equals(InnerLightType.DEFENCE)){
                ((ServerPlayerEntity) user).sendMessage(Text.literal("Resilience, that's what your light is made of"), true);
            }else if(type.equals(InnerLightType.STRENGTH)){
                ((ServerPlayerEntity) user).sendMessage(Text.literal("You have a vigorous light, lending its powers to your blade"), true);
            }else{
                ((ServerPlayerEntity) user).sendMessage(Text.literal("Uhm... your light is... an error? Light not implemented, report please! "), true);
            }
            //((ServerPlayerEntity) user).sendMessage(Text.translatable("text.lightwithin.your_light_is"), true);

        }
        return this.isFood() ? user.eatFood(world, stack) : stack;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(!Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("item.lightwithin.berry.tooltip"));
        } else {
            tooltip.add(Text.translatable("item.lightwithin.luxcognita_berry.tooltip"));
        }
    }


}
