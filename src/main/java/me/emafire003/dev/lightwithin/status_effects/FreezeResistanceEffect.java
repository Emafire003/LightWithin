package me.emafire003.dev.lightwithin.status_effects;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class FreezeResistanceEffect extends StatusEffect {


    private float default_yaw;
    private float default_pitch;
    private boolean initialized = false;

    public FreezeResistanceEffect() {
        super(StatusEffectCategory.HARMFUL, 0x8DFBFF);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the status effect every tick.
        return true;
    }

    // This method is called when it applies the status effect. We implement custom functionality here.
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.setFrozenTicks(150);
        entity.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, entity.getPos().add(0,0,0));
        //entity.changeLookDirection(0,0);
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier){
        entity.setFrozenTicks(0);
    }
}
