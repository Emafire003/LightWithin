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
            //TODO make them translatable
            if(type.equals(InnerLightType.NONE)){
                ((ServerPlayerEntity) user).sendMessage(Text.translatable("light.description.error"), true);
            }else if(type.equals(InnerLightType.HEAL)){
                ((ServerPlayerEntity) user).sendMessage(Text.translatable("light.description.heal"), true);
            }else if(type.equals(InnerLightType.DEFENCE)){
                ((ServerPlayerEntity) user).sendMessage(Text.translatable("light.description.defense"), true);
            }else if(type.equals(InnerLightType.STRENGTH)){
                ((ServerPlayerEntity) user).sendMessage(Text.translatable("light.description.strength"), true);
            }else if(type.equals(InnerLightType.BLAZING)){
                ((ServerPlayerEntity) user).sendMessage(Text.translatable("light.description.blazing"), true);
            }else if(type.equals(InnerLightType.FROST)){
                ((ServerPlayerEntity) user).sendMessage(Text.translatable("light.description.frost"), true);
            }else if(type.equals(InnerLightType.EARTHEN)){
                ((ServerPlayerEntity) user).sendMessage(Text.translatable("light.description.earthen"), true);
            }else if(type.equals(InnerLightType.WIND)){
                ((ServerPlayerEntity) user).sendMessage(Text.translatable("light.description.wind"), true);
            }else if(type.equals(InnerLightType.AQUA)){
                ((ServerPlayerEntity) user).sendMessage(Text.translatable("light.description.aqua"), true);
            }
            else if(type.equals(InnerLightType.FROG)){
                ((ServerPlayerEntity) user).sendMessage(Text.translatable("light.description.frog"), true);
            }
            else{
                ((ServerPlayerEntity) user).sendMessage(Text.translatable("light.description.error "), true);
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
            tooltip.add(Text.translatable("item.lightwithin.luxcognita_berry.tooltip1"));
        }
    }


}
