package me.emafire003.dev.lightwithin.items;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.lights.InnerLight;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.events.LightCreationAndEvent;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

//This will change the type and the target if needed of one's light. Randomly.
public class LuxmutuaBerryItem extends Item {

    public LuxmutuaBerryItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(LightWithin.isPlayerInCooldown(user) && Config.LUXMUTUA_BYPASS_COOLDOWN){
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
            user.playSound(LightSounds.LIGHT_READY, 1, 1.3F);
        }
        if(user instanceof ServerPlayerEntity){
            if(LightWithin.isPlayerInCooldown((PlayerEntity) user) && Config.LUXMUTUA_BYPASS_COOLDOWN){
                return stack;
            }
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(user);

            Pair<InnerLight, TargetType> current = new Pair<>(component.getType(), component.getTargets());
            String[] id_bits = UUID.randomUUID().toString().toLowerCase().split("-");
            Pair<InnerLight, TargetType> newone = LightCreationAndEvent.determineTypeAndTarget(id_bits, LightCreationAndEvent.TYPE_BIT,LightCreationAndEvent.TARGET_BIT);

            while(current.getFirst().equals(newone.getFirst())){
                id_bits = UUID.randomUUID().toString().toLowerCase().split("-");
                newone = LightCreationAndEvent.determineTypeAndTarget(id_bits, LightCreationAndEvent.TYPE_BIT,LightCreationAndEvent.TARGET_BIT);
            }

            component.setType(newone.getFirst());
            component.setTargets(newone.getSecond());

            ((ServerPlayerEntity) user).sendMessage(Text.translatable("item.lightwithin.luxmutua_berry.lightchange"), true);
        }
        FoodComponent foodComponent = stack.get(DataComponentTypes.FOOD);
        return foodComponent != null ? user.eatFood(world, stack, foodComponent) : stack;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        if(!Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("item.lightwithin.berry.tooltip"));
        } else {
            tooltip.add(Text.translatable("item.lightwithin.luxmutua_berry.tooltip"));
            tooltip.add(Text.translatable("item.lightwithin.luxmutua_berry.tooltip1"));
        }
    }
}
