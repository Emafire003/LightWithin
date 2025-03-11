package me.emafire003.dev.lightwithin.status_effects;

import me.emafire003.dev.lightwithin.config.BalanceConfig;
import me.emafire003.dev.lightwithin.mixin.WorldPropertiesAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

import java.util.Objects;

import static me.emafire003.dev.lightwithin.lights.ThunderAuraLight.spawnStormLightnings;


public class StormAuraEffect extends StatusEffect {

    public StormAuraEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x74842D);
    }


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
        return super.applyUpdateEffect(entity, amplifier);
    }

    private int prev_clearTime = 0;
    private int prev_rainTime = 0;
    private boolean prev_rain = false;
    private boolean prev_thunder = false;

    @Override
    public void onApplied(LivingEntity target, int amplifier) {
        super.onApplied(target, amplifier);

        if(target.getWorld().isClient()){
            return;
        }

        prev_clearTime = ((WorldPropertiesAccessor) target.getWorld()).getWorldProperties().getClearWeatherTime();
        prev_rainTime = ((WorldPropertiesAccessor) target.getWorld()).getWorldProperties().getRainTime();
        prev_rain = ((WorldPropertiesAccessor) target.getWorld()).getWorldProperties().isRaining();
        prev_thunder = ((WorldPropertiesAccessor) target.getWorld()).getWorldProperties().isThundering();

        //i need it in seconds
        int dur = Objects.requireNonNull(target.getStatusEffect(LightEffects.STORM_AURA)).getDuration()/20;

        ((ServerWorld) target.getWorld()).setWeather(2, dur, true, true);

        //it ranges from 15 to 25 blocksTODO WIKI
        Box storm_area  = Box.from(target.getPos()).expand(BalanceConfig.THUNDER_AURA_VARIANT_STORM_MIN_SIZE + amplifier);
        //TODO wiki The number of lightnings is the same as the power multiplier, with a minimum of 1 (configurable)
        spawnStormLightnings(storm_area, dur, Math.max(BalanceConfig.THUNDER_AURA_VARIANT_LIGHTNINGS_PER_LEVEL, amplifier), target);
    }

    @Override
    public void onRemoved(AttributeContainer attributes){
        super.onRemoved(attributes);
        if(targetedLivingEntity.getWorld().isClient()){
            return;
        }
        ((ServerWorld) targetedLivingEntity.getWorld()).setWeather(prev_clearTime, prev_rainTime, prev_rain, prev_thunder);
    }

}