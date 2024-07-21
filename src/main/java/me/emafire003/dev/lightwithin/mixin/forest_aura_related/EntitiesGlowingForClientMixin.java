package me.emafire003.dev.lightwithin.mixin.forest_aura_related;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.emafire003.dev.lightwithin.client.LightWithinClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftClient.class)
public abstract class EntitiesGlowingForClientMixin {

    @ModifyExpressionValue(
            method = "hasOutline",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isGlowing()Z")
    )
    private boolean seenByEyeOfTheForestThenGlow(boolean original, Entity entity) {
        return original || LightWithinClient.getEntitiesGlowingForPlayer().contains(entity.getUuid());
    }

}
