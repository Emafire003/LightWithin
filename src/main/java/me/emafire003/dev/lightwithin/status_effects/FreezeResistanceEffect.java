package me.emafire003.dev.lightwithin.status_effects;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class FreezeResistanceEffect extends StatusEffect {


    public FreezeResistanceEffect() {
        super(StatusEffectCategory.HARMFUL, 0x8DFBFF);
    }


}
