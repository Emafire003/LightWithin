package me.emafire003.dev.lightwithin.status_effects;

import me.emafire003.dev.coloredglowlib.ColoredGlowLib;
import me.emafire003.dev.coloredglowlib.util.Color;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class LightFatigueEffect extends StatusEffect {

    //LORE: Basicly your light after being used decays and well it needs to rechange so you can't use it for a while
    //it's a cool way to make a cooldown visible for the player too. As lot's have said, it's not a bug it's a feature
    //just look at it the right way
    //xD
    //TODO mixin into the GlowingEffect and make it so it can clear the ColoredGlowLib color

    public LightFatigueEffect() {
        super(StatusEffectCategory.HARMFUL, 0x9EC1BE);
    }

    private Color former_color = null;
    private boolean rainbow;

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the status effect every tick.
        return true;
    }

    // This method is called when it applies the status effect. We implement custom functionality here.
    //TODO not setting the correct "discharged" color
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(amplifier > 10){
            former_color = ColoredGlowLib.getEntityColor(entity);
            rainbow = ColoredGlowLib.getEntityRainbowColor(entity);
            if(rainbow){
                ColoredGlowLib.setRainbowColorToEntity(entity, false);
            }
            ColoredGlowLib.setColorToEntity(entity, Color.translateFromHEX("9EC1BE)"));
        }
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier){
        if(former_color != null ){
            if(!former_color.equals(Color.getWhiteColor())){
                ColoredGlowLib.setColorToEntity(entity, former_color);
            }
            if(rainbow){
                ColoredGlowLib.setRainbowColorToEntity(entity, true);
            }
        }
    }
}
