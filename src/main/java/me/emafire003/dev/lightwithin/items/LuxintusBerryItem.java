package me.emafire003.dev.lightwithin.items;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class LuxintusBerryItem extends Item {

    public LuxintusBerryItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if(user instanceof ServerPlayerEntity){
            //TODO make cooldown bypassable in config
            if(user.hasStatusEffect(LightEffects.LIGHT_FATIGUE)){
                user.removeStatusEffect(LightEffects.LIGHT_FATIGUE);
                user.playSound(LightSounds.HEAL_LIGHT, 1, 1);
            }
            LightWithin.activateLight((ServerPlayerEntity) user);
        }
        return this.isFood() ? user.eatFood(world, stack) : stack;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(!Screen.hasShiftDown()) {
            tooltip.add(new TranslatableText("item.lightwithin.berry.tooltip"));
        } else {
            tooltip.add(new TranslatableText("item.lightwithin.luxintus_berry.tooltip"));
        }
    }
}
