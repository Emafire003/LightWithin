package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.util.IRenderEffectsEntity;
import me.emafire003.dev.lightwithin.util.RenderEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;

import static me.emafire003.dev.lightwithin.LightWithin.LIGHT_COMPONENT;

@Mixin(Entity.class)
public abstract class RenderEffectsEntityMixin implements IRenderEffectsEntity {

    @Unique
    private final HashMap<RenderEffect, Boolean> shouldRenderEffects = new HashMap<>();

    @Unique
    private final HashMap<RenderEffect, Integer> ticksForRenderEffects = new HashMap<>();

    @Unique
    private final HashMap<RenderEffect, Long> startTimeEffect = new HashMap<>();


    @Override
    public void lightWithin$renderEffect(RenderEffect effect, int ticks){
        shouldRenderEffects.put(effect, true);
        ticksForRenderEffects.put(effect, ticks);
        startTimeEffect.put(effect, System.currentTimeMillis());
    }

    @Override
    public boolean lightWithin$shouldRender(RenderEffect effect){
        if(((Entity) (Object) this) instanceof PlayerEntity){
            if(startTimeEffect.get(effect) != null){
                if(System.currentTimeMillis() > (startTimeEffect.get(effect) + LIGHT_COMPONENT.get(((Entity) (Object) this)).getDuration()* 1000L)){
                    return false;
                }
            }
        }

        if(!shouldRenderEffects.containsKey(effect) ){
            return false;
        }
        return shouldRenderEffects.get(effect);
    }

    /**A return of -1 means the effect isn't in the map*/
    @Override
    public int lightWithin$getRenderTicks(RenderEffect effect){
        if(!ticksForRenderEffects.containsKey(effect)){
            return -1;
        }
        return ticksForRenderEffects.get(effect);
    }

    @Override
    public void lightWithin$stopEffect(RenderEffect effect){
        shouldRenderEffects.put(effect, false);
    }

}
