package me.emafire003.dev.lightwithin.items;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.networking.PlayRenderEffectPayloadS2C;
import me.emafire003.dev.lightwithin.lights.InnerLight;
import me.emafire003.dev.lightwithin.lights.NoneLight;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.util.RenderEffect;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

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
        if (this.getComponents().contains(DataComponentTypes.FOOD)) {
            ItemStack itemStack = user.getStackInHand(hand);
            if (this.getComponents().get(DataComponentTypes.FOOD) != null && user.canConsume(this.getComponents().get(DataComponentTypes.FOOD).canAlwaysEat())) {
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

            ServerPlayNetworking.send((ServerPlayerEntity) user, new PlayRenderEffectPayloadS2C(RenderEffect.LUXCOGNITA_SCREEN, -1));
        }
        FoodComponent foodComponent = stack.get(DataComponentTypes.FOOD);
        return foodComponent != null ? user.eatFood(world, stack, foodComponent) : stack;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        if(!Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("item.lightwithin.berry.tooltip"));
        } else {
            tooltip.add(Text.translatable("item.lightwithin.luxcognita_berry.tooltip"));
            tooltip.add(Text.translatable("item.lightwithin.luxcognita_berry.tooltip1"));
        }
    }



    // Utility methods not necessarily used in here but in the screen class

    //#f1f657
    private static final Style style = Style.EMPTY.withColor(15857239);

    /**Sends a player the description of their InnerLight type*/
    public static void sendLightTypeMessage(PlayerEntity user){
        if(user == null){
            LightWithin.LOGGER.error("Error! Can't send light type messages, the player is null!");
            return;
        }
        //light blue is 6288592
        InnerLight type = LightWithin.LIGHT_COMPONENT.get(user).getType();
        if(type instanceof NoneLight){
            user.sendMessage(Text.translatable("light.description.error").formatted(Formatting.RED), true);
            return;
        }
        user.sendMessage(Text.translatable("light.description." + type.toString().toLowerCase()).setStyle(style), true);
    }

    /**Sends a player the description of their InnerLight type*/
    public static void sendLightTargetMessage(PlayerEntity user){
        if(user == null){
            LightWithin.LOGGER.error("Error! Can't send light target messages, the player is null!");
            return;
        }
        //TODO see what can be done with custom fonts maybe?
        TargetType type = LightWithin.LIGHT_COMPONENT.get(user).getTargets();
        if(type.equals(TargetType.NONE)){
            user.sendMessage(Text.translatable("light.description.error").formatted(Formatting.RED), true);
            return;
        }
        user.sendMessage(Text.translatable("light.description.target." + type.toString().toLowerCase()).setStyle(style), true);
    }

}
