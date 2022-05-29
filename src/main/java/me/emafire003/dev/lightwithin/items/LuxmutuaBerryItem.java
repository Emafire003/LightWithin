package me.emafire003.dev.lightwithin.items;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
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
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

//This will change the type and the target if needed of one's light. Randomly.
public class LuxmutuaBerryItem extends Item {

    public LuxmutuaBerryItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
            //TODO make cooldown bypassable in config
        if(user.hasStatusEffect(LightEffects.LIGHT_FATIGUE) || user.hasStatusEffect(LightEffects.LIGHT_ACTIVE)){
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
            //TODO make cooldown bypassable in config
            if(user.hasStatusEffect(LightEffects.LIGHT_FATIGUE) || user.hasStatusEffect(LightEffects.LIGHT_ACTIVE)){
                ((ServerPlayerEntity) user).sendMessage(new LiteralText("Helo, returning cuz yes"), false);
                return stack;
            }
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(user);
            InnerLightType current = component.getType();
            InnerLightType newone = genNewLight((ServerPlayerEntity) user);
            TargetType current_targets = component.getTargets();
            if(newone.equals(InnerLightType.NONE)){
                return stack;
            }
            while (current.equals(newone)){
                newone = genNewLight((ServerPlayerEntity) user);
                if(newone.equals(InnerLightType.NONE)){
                    return stack;
                }
            }
            TargetType newtarget = genNewTarget((ServerPlayerEntity) user, newone);
            if(newtarget.equals(TargetType.NONE)){
                return stack;
            }
            while (current_targets.equals(newtarget)){
                newtarget = genNewTarget((ServerPlayerEntity) user, newone);
            }
            component.setType(newone);
            component.setTargets(newtarget);

            //TODO config
            ((ServerPlayerEntity) user).sendMessage(new LiteralText("Your light resonated with you again and decided it was time to change"), true);
        }
        return this.isFood() ? user.eatFood(world, stack) : stack;
    }

    public InnerLightType genNewLight(ServerPlayerEntity player){
        int r = player.getRandom().nextInt(3);
        if(r == 0){
            return InnerLightType.HEAL;
        }
        else if(r == 1){
            return InnerLightType.DEFENCE;
        }
        else if(r == 2){
            return InnerLightType.STRENGTH;
        }else{
            player.sendMessage(new LiteralText("There was an error, sorry. " + r), false);
            return InnerLightType.NONE;
        }
    }

    public TargetType genNewTarget(ServerPlayerEntity player, InnerLightType type){
        int r = player.getRandom().nextInt(6);
        if(r >= 0 && r <= 3){
            return TargetType.SELF;
        }
        else if(r > 3 && r <=5){
            return TargetType.ALLIES;
        }
        else if(r == 6){
            return TargetType.OTHER;
        }else{
            player.sendMessage(new LiteralText("There was an error, sorry. " + r), false);
            return TargetType.NONE;
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(!Screen.hasShiftDown()) {
            tooltip.add(new TranslatableText("item.lightwithin.berry.tooltip"));
        } else {
            tooltip.add(new TranslatableText("item.lightwithin.luxmutua_berry.tooltip"));
        }
    }
}
