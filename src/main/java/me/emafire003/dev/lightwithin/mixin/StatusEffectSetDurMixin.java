package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.util.IStatusEffectWithSettableDuration;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StatusEffectInstance.class)
public class StatusEffectSetDurMixin implements IStatusEffectWithSettableDuration {

    @Shadow private int duration;

    @Override
    public void lightWithin$setDuration(int new_duration) {
        this.duration = new_duration;
    }
}
