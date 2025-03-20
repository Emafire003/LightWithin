package me.emafire003.dev.lightwithin.mixin.forest_aura_related;

import com.mojang.blaze3d.systems.RenderSystem;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRenderForestMixin {

    //Needed for the forest aura effect
    //TODO maybe make this color configurable?
    @Inject(
            method = "render",
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearColor(FFFF)V",
                    shift = At.Shift.AFTER,
                    ordinal = 0),
            cancellable = true)
    private static void injectFogColor(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness, CallbackInfo ci){
        if(camera.getFocusedEntity() instanceof LivingEntity){
            if(((LivingEntity) camera.getFocusedEntity()).hasStatusEffect(LightEffects.FOREST_AURA)){
                float r = 83 / 255f;
                float g = 140 / 255f;
                float b = 53 / 255f; //#5d9435
                float a = 0.1f;
                RenderSystem.clearColor(r,g,b,a);
                ci.cancel();
            }
        }

    }
}