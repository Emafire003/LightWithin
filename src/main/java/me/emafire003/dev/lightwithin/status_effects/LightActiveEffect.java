package me.emafire003.dev.lightwithin.status_effects;

import me.emafire003.dev.coloredglowlib.ColoredGlowLib;
import me.emafire003.dev.coloredglowlib.util.Color;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.lights.InnerLight;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;

import static me.emafire003.dev.lightwithin.LightWithin.LIGHT_COMPONENT;

public class LightActiveEffect extends StatusEffect {

    //LORE: Basicly your light after being used decays and well it needs to rechange so you can't use it for a while
    //it's a cool way to make a cooldown visible for the player too. As lot's have said, it's not a bug it's a feature
    //just look at it the right way
    //xD
    //TODO mixin into the GlowingEffect and make it so it can clear the ColoredGlowLib color

    private Color former_color = null;
    private boolean rainbow = false;
    private boolean already_run = false;
    private InnerLight type;

    public LightActiveEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xF3FF28);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the status effect every tick.
        return true;
    }

    // This method is called when it applies the status effect. We implement custom functionality here.
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(!already_run){
            former_color = ColoredGlowLib.getEntityColor(entity);
            rainbow = ColoredGlowLib.getEntityRainbowColor(entity);
            already_run = true;
        }
        if(entity instanceof ServerPlayerEntity){
            LightComponent component = LIGHT_COMPONENT.get(entity);
            if(component.getType().equals(InnerLightType.WIND) && !component.getTargets().equals(TargetType.OTHER)){
                ((ServerPlayerEntity) entity).getWorld().spawnParticles((ServerPlayerEntity) entity, ParticleTypes.CLOUD, false, entity.getX(), entity.getY(), entity.getZ(), 5, 0, 0, 0, 0.1);
            }
        }
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier){
        if(!entity.hasStatusEffect(StatusEffects.GLOWING)){
            entity.setGlowing(false);
        }
        if(former_color != null ){
            if(!former_color.equals(Color.getWhiteColor())){
                ColoredGlowLib.setColorToEntity(entity, former_color);
            }
            if(rainbow){
                ColoredGlowLib.setRainbowColorToEntity(entity, true);
            }
        }
        if(entity instanceof PlayerEntity){
            LightComponent component = LIGHT_COMPONENT.get(entity);
            entity.addStatusEffect(new StatusEffectInstance(LightEffects.LIGHT_FATIGUE, (int) (Config.COOLDOWN_MULTIPLIER*20*(component.getMaxCooldown()-component.getDuration())), 1));
        }
    }
}
