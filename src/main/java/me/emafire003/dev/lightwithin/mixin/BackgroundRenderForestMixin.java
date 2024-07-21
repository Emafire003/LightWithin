package me.emafire003.dev.lightwithin.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRenderForestMixin {

    //Needed for the forest aura effect
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
                //I think i need to return here
            }
        }

    }

    /*
    //There probably is a better way of doing this, but for now (06.03.2024) it will suffice.
    @Inject(method = "applyFog", at = @At(value = "RETURN"))
    private static void injectFogDistance(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci){
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        Entity entity = camera.getFocusedEntity();
        BackgroundRenderer.FogData fogData = new BackgroundRenderer.FogData(fogType);
        //TODO move to player
        if (entity instanceof LivingEntity && entity.getBlockStateAtPos().isIn(LightWithin.FOREST_AURA_BLOCKS) && cameraSubmersionType == CameraSubmersionType.POWDER_SNOW) {
            //The def value
            if (entity.isSpectator()) {
                fogData.fogStart = -8.0f;
                fogData.fogEnd = viewDistance * 0.5f;
            } else {
                fogData.fogStart = -2.0f;
                fogData.fogEnd = viewDistance * 0.2f;
                fogData.fogShape = FogShape.SPHERE;
            }
            //TODO i should mixin into this, or before these.
            RenderSystem.setShaderFogStart(fogData.fogStart);
            RenderSystem.setShaderFogEnd(fogData.fogEnd);
            RenderSystem.setShaderFogShape(fogData.fogShape);
        }

    }*/
}