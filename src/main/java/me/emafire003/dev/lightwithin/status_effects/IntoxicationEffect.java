package me.emafire003.dev.lightwithin.status_effects;

import me.emafire003.dev.lightwithin.mixin.forest_aura_related.LivingEntityJumpInvoker;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

/**To see how this is implemented go check {@link me.emafire003.dev.lightwithin.mixin.forest_aura_related.RandomizeMovementPlayerEntityMixin}
 * and {@link me.emafire003.dev.lightwithin.client.shaders.LightShaders}
 *
 * Anyways, it makes the player see wierdly and switches up the movement controls*/
public class IntoxicationEffect extends StatusEffect {

    /**Amplifier 0-1: inverted controls (forward -> backwards)
     * Amplifier 2-3: swapped X-Z
     * Amplifier 4-5: swapped X-Z, inverted X which is now Z ^^^------ DECONVERGE+PHOSPHOR SHADER ------^^^
     * Amplifier 6-7: swapped X-Z, inverted Z which is now X vvv------ WOBBLE SHADER ------vvv
     * Amplifier 8-9: swapped X-Z, inverted both
     * Amplifier 10+: inverted controls
     * <p>
     * Random jumps, with (amplifier/1.5)% chance jump each tick when on ground
     * <p>
     * {@link me.emafire003.dev.lightwithin.mixin.forest_aura_related.RandomizeMovementPlayerEntityMixin}*/
    public IntoxicationEffect() {
        super(StatusEffectCategory.HARMFUL, 0x7E1291);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the status effect every tick.
        return true;
    }

    // This method is called when it applies the status effect. We implement custom functionality here.
    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        super.applyUpdateEffect(entity, amplifier);
        //amplifier/1.5% chance
        if(entity.isOnGround() && entity.getRandom().nextBetween(1, 100) <= (double) amplifier /1.5){
            ((LivingEntityJumpInvoker) entity).jumpInvoker();
        }
        return true;
    }

}