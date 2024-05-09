package me.emafire003.dev.lightwithin.mixin;

// Copyright: https://github.com/0x3C50/Renderer/blob/master/LICENSE
// Only needed the render events

import me.x150.renderer.RenderEvents;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Inject(method = "render", at = @At("RETURN"))
    void renderer_postHud(MatrixStack context, float tickDelta, CallbackInfo ci) {
        RenderEvents.HUD.invoker().rendered(context);
    }

}