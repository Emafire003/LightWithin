package me.emafire003.dev.lightwithin.status_effects;

import me.emafire003.dev.coloredglowlib.ColoredGlowLib;
import me.emafire003.dev.coloredglowlib.util.Color;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class LightFatigueEffect extends StatusEffect {

    public LightFatigueEffect() {
        super(StatusEffectCategory.HARMFUL, 0x9EC1BE);
    }

    private Color former_color = null;

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the status effect every tick.
        return true;
    }

    // This method is called when it applies the status effect. We implement custom functionality here.
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(amplifier > 10){
            former_color = ColoredGlowLib.getEntityColor(entity);
            ColoredGlowLib.setColorToEntity(entity, Color.translateFromHEX("9EC1BE)"));
        }
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier){
        if(former_color != null && former_color != Color.getWhiteColor()){
            ColoredGlowLib.setColorToEntity(entity, former_color);
        }
    }
}
