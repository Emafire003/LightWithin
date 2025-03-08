package me.emafire003.dev.lightwithin.mixin.forest_aura_related;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityJumpInvoker {
    @Invoker("jump")
    void jumpInvoker();
}
