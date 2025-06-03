package me.emafire003.dev.lightwithin.status_effects;

import me.emafire003.dev.lightwithin.items.LightItems;
import me.emafire003.dev.lightwithin.networking.LuxDialogueActions;
import me.emafire003.dev.lightwithin.networking.LuxdreamServerPayloadS2C;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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

    private int songLength;
    private LivingEntity entity;

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        super.onApplied(entity, amplifier);

        songLength = JukeboxSong.getSongEntryFromStack(entity.getWorld().getRegistryManager(), new ItemStack(LightItems.MUSIC_DISC_LUXCOGNITA_DREAM, 1))
                .get().value().getLengthInTicks();
        this.entity = entity;

        if(entity instanceof  PlayerEntity && !entity.getWorld().isClient()){
            ServerPlayNetworking.send((ServerPlayerEntity) entity, new LuxdreamServerPayloadS2C(LuxDialogueActions.START_BGM));
        }
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        super.applyUpdateEffect(entity, amplifier);
        if(entity.getWorld().isClient()) {
            return false;
        }
        if(songTicker > songLength){
            songTicker = 0;
            if(entity instanceof  PlayerEntity && !entity.getWorld().isClient()){

                ServerPlayNetworking.send((ServerPlayerEntity) entity, new LuxdreamServerPayloadS2C(LuxDialogueActions.START_BGM));
            }
            return true; //returns so it doesn't already tick the next one
        }
        songTicker++;
        return true;
    }

    @Override
    public void onRemoved(AttributeContainer attributeContainer) {
        if(entity instanceof  PlayerEntity && !entity.getWorld().isClient()){
            ServerPlayNetworking.send((ServerPlayerEntity) entity, new LuxdreamServerPayloadS2C(LuxDialogueActions.STOP_BGM));
        }
        songTicker = 0;
    }
}
