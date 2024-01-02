package me.emafire003.dev.lightwithin.items;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.events.LightTriggeringAndEvents;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.TargetType;
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

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.UUID;

//This will change the type and the target if needed of one's light. Randomly.
public class LuxmutuaBerryItem extends Item {

    public LuxmutuaBerryItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
            //TODO make cooldown bypassable in config
        if(LightWithin.isPlayerInCooldown(user) && Config.LUXIMUTUA_BYPASS_COOLDOWN){
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
            user.playSound(LightSounds.LIGHT_READY, 1, 1.3F);
        }
        if(user instanceof ServerPlayerEntity){
            if(LightWithin.isPlayerInCooldown((PlayerEntity) user) && Config.LUXIMUTUA_BYPASS_COOLDOWN){
                return stack;
            }
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(user);

            Pair<InnerLightType, TargetType> current = new Pair<>(component.getType(), component.getTargets());
            String[] id_bits = UUID.randomUUID().toString().toLowerCase().split("-");
            Pair<InnerLightType, TargetType> newone = LightTriggeringAndEvents.determineTypeAndTarget(id_bits, 1,3);

            while(current.getFirst().equals(newone.getFirst())){
                id_bits = UUID.randomUUID().toString().toLowerCase().split("-");
                newone = LightTriggeringAndEvents.determineTypeAndTarget(id_bits, 1,3);
            }

            component.setType(newone.getFirst());
            component.setTargets(newone.getSecond());

            //TODO config
            ((ServerPlayerEntity) user).sendMessage(Text.literal("Your light resonated with you again and decided it was time to change"), true);
        }
        return this.isFood() ? user.eatFood(world, stack) : stack;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(!Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("item.lightwithin.berry.tooltip"));
        } else {
            tooltip.add(Text.translatable("item.lightwithin.luxmutua_berry.tooltip"));
            tooltip.add(Text.translatable("item.lightwithin.luxmutua_berry.tooltip1"));
        }
    }
}
