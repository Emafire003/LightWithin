package me.emafire003.dev.lightwithin.items;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BottledLightItem extends Item {

    public BottledLightItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(LightWithin.isPlayerInCooldown(user)){
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        LightComponent component = LightWithin.LIGHT_COMPONENT.get(user);
        //TODO make configurable, and optional. Kinda.
        if(!component.hasTriggeredNaturally()){
            if(!world.isClient()){
                user.sendMessage(Text.translatable("light.needs_natural_trigger"));
            }
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        int charges = component.getCurrentLightCharges()+1;
        if(charges > component.getMaxLightStack()){
            //TODO or another error-sound
            user.playSound(SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, 0.5f, 1.76f);
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        //TODO implement NBT-based checks for
        // 1) The right type of light
        // 2) Or, the exact light type. Like the player itself and its uuid so everyone has to have theirs
        component.setLightCharges(charges);
        //TODO add the fancy sound effects of charging up and the animation here as well
        world.addBlockBreakParticles(user.getBlockPos().up(), Blocks.GLASS.getDefaultState());
        user.playSound(SoundEvents.BLOCK_GLASS_BREAK, 0.6f, 1.3f);
        user.playSound(LightSounds.LIGHT_READY, 1f, 1.5f);
        ItemStack stack = user.getStackInHand(hand);
        user.addStatusEffect(new StatusEffectInstance(LightEffects.LIGHT_FATIGUE, (int) (Config.COOLDOWN_MULTIPLIER*20*LightWithin.LIGHT_COMPONENT.get(user).getMaxCooldown())));
        user.getStackInHand(hand).decrement(1);
        return TypedActionResult.consume(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.lightwithin.bottled_light.tooltip"));
        //TODO make the name of the player show up in here! Since it's unique. Or at least the
    }


}
