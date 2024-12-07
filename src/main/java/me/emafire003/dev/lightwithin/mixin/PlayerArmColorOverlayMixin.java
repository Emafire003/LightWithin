package me.emafire003.dev.lightwithin.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerArmColorOverlayMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    public PlayerArmColorOverlayMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @WrapOperation(
            method = "renderArm",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V")
    )
    public void applyArmColor(ModelPart instance, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, Operation<Void> original, @Local(ordinal = 0, argsOnly = true) ModelPart arm){
        PlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null){
            //TODO it doensn't render at all now
            original.call(instance, matrices, vertices, light, overlay);
            return;
        }
        if(player.hasStatusEffect(LightEffects.LIGHT_ACTIVE)){
            if(LightWithin.LIGHT_COMPONENT.get(player).getType().equals(InnerLightType.FOREST_AURA)){
                arm.render(matrices, vertices, light, overlay, 0.2f, 0.9f, 0.2f, 0.75f);
            }
        }
    }

    @WrapOperation(
            method = "renderArm",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V")
    )
    public void applySleeveColor(ModelPart instance, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, Operation<Void> original, @Local(ordinal = 1, argsOnly = true) ModelPart sleeve){
        PlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null){
            original.call(instance, matrices, vertices, light, overlay);
            return;
        }
        if(player.hasStatusEffect(LightEffects.LIGHT_ACTIVE)){
            if(LightWithin.LIGHT_COMPONENT.get(player).getType().equals(InnerLightType.FOREST_AURA)){
                sleeve.render(matrices, vertices, light, overlay, 0.2f, 0.9f, 0.2f, 0.75f);
            }
        }
    }

}
