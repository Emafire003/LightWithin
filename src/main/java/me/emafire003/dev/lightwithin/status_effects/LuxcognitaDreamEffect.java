package me.emafire003.dev.lightwithin.status_effects;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.items.LightItems;
import me.emafire003.dev.lightwithin.networking.LuxDialogueActions;
import me.emafire003.dev.lightwithin.networking.LuxdreamServerPacketS2C;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class LuxcognitaDreamEffect extends StatusEffect {

    public LuxcognitaDreamEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x62E5C6);
    }

    public int songTicker = 0;

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the status effect every tick.
        return true;
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);
        if(entity instanceof  PlayerEntity && !entity.getWorld().isClient()){
            ServerPlayNetworking.send((ServerPlayerEntity) entity, LuxdreamServerPacketS2C.ID, new LuxdreamServerPacketS2C(LuxDialogueActions.START_BGM));
        }
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        super.applyUpdateEffect(entity, amplifier);
        if(entity.getWorld().isClient()){
            return;
        }
        if(songTicker > LightItems.MUSIC_DISC_LUXCOGNITA_DREAM.getSongLengthInTicks()){
            LightWithin.LOGGER.info("The songTicker is: " + songTicker + " while the song lenght: " + LightItems.MUSIC_DISC_LUXCOGNITA_DREAM.getSongLengthInTicks());
            LightWithin.LOGGER.info("In seconds: " + songTicker/20 + " song: " + LightItems.MUSIC_DISC_LUXCOGNITA_DREAM.getSongLengthInTicks()/20);

            songTicker = 0;
            if(entity instanceof  PlayerEntity && !entity.getWorld().isClient()){
                ServerPlayNetworking.send((ServerPlayerEntity) entity, LuxdreamServerPacketS2C.ID, new LuxdreamServerPacketS2C(LuxDialogueActions.START_BGM));
            }
            return; //retuns so it doesn't already tick the next one
        }
        songTicker++;

    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier){
        super.onRemoved(entity, attributes, amplifier);
        if(entity instanceof  PlayerEntity && !entity.getWorld().isClient()){
            ServerPlayNetworking.send((ServerPlayerEntity) entity, LuxdreamServerPacketS2C.ID, new LuxdreamServerPacketS2C(LuxDialogueActions.STOP_BGM));
        }
        songTicker = 0;
    }







}
