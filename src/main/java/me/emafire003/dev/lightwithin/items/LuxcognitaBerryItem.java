package me.emafire003.dev.lightwithin.items;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;

import java.util.Random;

public class LuxcognitaBerryItem extends Item {

    public LuxcognitaBerryItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if(user instanceof ServerPlayerEntity){
            //TODO make cooldown bypassable in config
            if(user instanceof PlayerEntity){

                LightComponent component = LightWithin.LIGHT_COMPONENT.get(user);
                InnerLightType type = component.getType();
                if(type.equals(InnerLightType.NONE)){
                    ((ServerPlayerEntity) user).sendMessage(new LiteralText("Uhm... your light is... an error? Light not found, report please! "), true);
                }else if(type.equals(InnerLightType.HEAL)){
                    ((ServerPlayerEntity) user).sendMessage(new LiteralText("Your light, yes, is a gentle and caring one indeed"), true);
                }else if(type.equals(InnerLightType.DEFENCE)){
                    ((ServerPlayerEntity) user).sendMessage(new LiteralText("Resilience, that's what your light is made of"), true);
                }else if(type.equals(InnerLightType.STRENGTH)){
                    ((ServerPlayerEntity) user).sendMessage(new LiteralText("You have a vigorous light, lending its power to you"), true);
                }else{
                    ((ServerPlayerEntity) user).sendMessage(new LiteralText("Uhm... your light is... an error? Light not implemented, report please! "), true);
                }
                //((ServerPlayerEntity) user).sendMessage(new TranslatableText("text.lightwithin.your_light_is"), true);
            }
        }
        return this.isFood() ? user.eatFood(world, stack) : stack;
    }
}
