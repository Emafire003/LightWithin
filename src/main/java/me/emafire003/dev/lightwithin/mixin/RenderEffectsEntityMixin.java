package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.util.IRenderEffectsEntity;
import me.emafire003.dev.lightwithin.util.RenderEffect;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

import java.util.HashMap;

@Mixin(LivingEntity.class)
public abstract class RenderEffectsEntityMixin implements IRenderEffectsEntity {

    private HashMap<RenderEffect, Boolean> shouldRenderEffects = new HashMap<>();

    @Override
    public void lightWithin$renderEffect(RenderEffect effect){
        LightWithin.LOGGER.info("Starting animation");
        shouldRenderEffects.put(effect, true);
    }

    @Override
    public boolean lightWithin$shouldRender(RenderEffect effect){
        if(!shouldRenderEffects.containsKey(effect)){
            return false;
        }
        return shouldRenderEffects.get(effect);
    }

    @Override
    public void lightWithin$stopEffect(RenderEffect effect){
        LightWithin.LOGGER.info("Stopping animation");
        shouldRenderEffects.put(effect, false);
    }

}
