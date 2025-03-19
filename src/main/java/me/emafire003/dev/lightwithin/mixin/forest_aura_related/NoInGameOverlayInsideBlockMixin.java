package me.emafire003.dev.lightwithin.mixin.forest_aura_related;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InGameOverlayRenderer.class)
public class NoInGameOverlayInsideBlockMixin {
    @ModifyExpressionValue(
            method = "renderOverlays",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;noClip:Z")
    )
    private static boolean dontRenderOverlayIfForestAura(boolean original) {
        PlayerEntity p = MinecraftClient.getInstance().player;
        if(p != null && p.hasStatusEffect(LightEffects.FOREST_AURA)){
            return true;//it gets negated in the if thing
        }
        return original;
    }
}
