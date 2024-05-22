package me.emafire003.dev.lightwithin.status_effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class LightFatigueEffect extends StatusEffect {

    //LORE: Basically your light after being used decays and well it needs to rechange so you can't use it for a while
    //it's a cool way to make a cooldown visible for the player too. As lot's have said, it's not a bug it's a feature
    //just look at it the right way
    //xD

    public LightFatigueEffect() {
        super(StatusEffectCategory.HARMFUL, 0x9EC1BE);
    }

}
