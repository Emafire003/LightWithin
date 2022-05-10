package me.emafire003.dev.lightwithin.items;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;

//This will change the type and the target if needed of one's light. Randomly.
public class LuxmutuaBerryItem extends Item {

    public LuxmutuaBerryItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if(user instanceof ServerPlayerEntity){
            //TODO make cooldown bypassable in config
            if(user.hasStatusEffect(LightEffects.LIGHT_FATIGUE) || user.hasStatusEffect(LightEffects.LIGHT_ACTIVE)){
                return this.isFood() ? user.eatFood(world, stack) : stack;
            }
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(user);
            int r = user.getRandom().nextInt(2);
            if(r == 0){
                component.setType(InnerLightType.HEAL);
            }
            else if(r == 1){
                component.setType(InnerLightType.DEFENCE);
            }
            else if(r == 2){
                component.setType(InnerLightType.STRENGTH);
            }
            //TODO config
            ((ServerPlayerEntity) user).sendMessage(new LiteralText("Your, light resonated with you again and decided it was time to change"), true);
        }
        return this.isFood() ? user.eatFood(world, stack) : stack;
    }
}
