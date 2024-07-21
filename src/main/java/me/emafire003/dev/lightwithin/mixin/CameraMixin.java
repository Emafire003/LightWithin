package me.emafire003.dev.lightwithin.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.emafire003.dev.lightwithin.lights.ForestAuraLight;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Debug(export = true)
@Mixin(Camera.class)
public abstract class CameraMixin {

    //Needed for the forest aura effect
    @ModifyExpressionValue(
            method = "getSubmersionType",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z")
    )
    public boolean injectForestAuraCameraVision(boolean original, @Local BlockState state){
        if (state.isIn(ForestAuraLight.FOREST_AURA_BLOCKS)){
            return true;
        }
        return original;
    }
}