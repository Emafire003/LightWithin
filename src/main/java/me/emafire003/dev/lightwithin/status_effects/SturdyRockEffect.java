package me.emafire003.dev.lightwithin.status_effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;

public class SturdyRockEffect extends StatusEffect {

    public SturdyRockEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x3B2206);
    }

    //TODO THIS HAS NOTHING TO DO WITH THIS EFFECT, IT'S JUST A TEST NEEDS TO BE REMOVED LATER


    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the status effect every tick.
        return true;
    }

    boolean run = false;
    LivingEntity targetedLivingEntity;

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        super.applyUpdateEffect(entity, amplifier);
        if(!run){
            if(!entity.getWorld().isClient()){
                run = true;
                this.targetedLivingEntity = entity;
            }
        }
        if(entity.getWorld().isClient){
            // TODO this may be useful
            MinecraftClient.getInstance().getEntityRenderDispatcher().render();
        }



        if(entity instanceof PlayerEntity player){
            //TODO fare un loop di tutte le entity type registrate e vedere quale è quella piu presente ecc
            Stats
        }

        return super.applyUpdateEffect(entity, amplifier);
    }

}
