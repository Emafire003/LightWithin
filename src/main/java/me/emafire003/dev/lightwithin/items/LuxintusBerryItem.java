package me.emafire003.dev.lightwithin.items;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

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
}
