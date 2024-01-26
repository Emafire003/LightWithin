package me.emafire003.dev.lightwithin.status_effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class FrostEffect extends StatusEffect {

    public FrostEffect() {
        super(StatusEffectCategory.HARMFUL, 0x8CB1FF);
    }

    int frozenTicks = 0;
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the status effect every tick.
        return true;
    }
    boolean run = false;
    Entity entity;
    // This method is called when it applies the status effect. We implement custom functionality here.
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(!run){
            run = true;
            this.entity = entity;
        }
        entity.setFrozenTicks(frozenTicks);
        this.frozenTicks = this.frozenTicks + 5;
        //entity.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, entity.getPos().add(0,0,0));
    }

    @Override
    public void onRemoved(AttributeContainer attributes){
        entity.setFrozenTicks(0);
        super.onRemoved(attributes);
    }
}
