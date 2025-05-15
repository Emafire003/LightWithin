package me.emafire003.dev.lightwithin.status_effects;

import me.emafire003.dev.lightwithin.client.LightWithinClient;
import me.emafire003.dev.lightwithin.items.LightItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

//TODO make sure this works on a dedicated server without crashing
public class LuxcognitaDreamEffect extends StatusEffect {

    public LuxcognitaDreamEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x62E5C6);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the status effect every tick.
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        super.applyUpdateEffect(entity, amplifier);
        //Only want the client to be run here
        if(!(entity instanceof PlayerEntity && entity.getWorld().isClient())){
            return;
        }
        //If the song isn't playing, set it playing and set the ticker to 0
        if(!LightWithinClient.isIsLuxcognitaBGMPlaying()){
            entity.playSound(LightItems.MUSIC_DISC_LUXCOGNITA_DREAM.getSound(), 0.65f, 1f);
            LightWithinClient.setIsLuxcognitaBGMPlaying(true);
            LightWithinClient.setLuxcognitaBGMTicker(0);
        }
        if(LightWithinClient.isIsLuxcognitaBGMPlaying()){
            //If the song is playing, increment the ticker
            LightWithinClient.setLuxcognitaBGMTicker(LightWithinClient.getLuxcognitaBGMTicker()+1);
            //if the ticker is longer than the music duration, restart it
            if(LightWithinClient.getLuxcognitaBGMTicker() > LightItems.MUSIC_DISC_LUXCOGNITA_DREAM.getSongLengthInTicks()){
                LightWithinClient.setLuxcognitaBGMTicker(0);
            }
        }
    }



}
