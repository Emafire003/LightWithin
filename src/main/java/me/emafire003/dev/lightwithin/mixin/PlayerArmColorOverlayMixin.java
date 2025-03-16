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
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.awt.Color;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerArmColorOverlayMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    @Shadow public abstract void render(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i);

    public PlayerArmColorOverlayMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @WrapOperation(
            method = "renderArm",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V", ordinal = 0)
    )
    public void applyArmColor(ModelPart instance, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, Operation<Void> original, @Local(ordinal = 0, argsOnly = true) ModelPart arm, @Local(ordinal = 1, argsOnly = true) ModelPart sleeve){
        PlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null){
            original.call(instance, matrices, vertices, light, overlay);
            return;
        }
        if(player.hasStatusEffect(LightEffects.LIGHT_ACTIVE)){
            if(LightWithin.LIGHT_COMPONENT.get(player).getType().equals(InnerLightType.FOREST_AURA)){
                arm.render(matrices, vertices, light, overlay, 0.4f, 0.9f, 0.4f, 0.4f);
                return;
            }
            if(LightWithin.LIGHT_COMPONENT.get(player).getType().equals(InnerLightType.THUNDER_AURA)){
                arm.render(matrices, vertices, light, overlay, 0.8f, 0.8f, 0.15f, 0.4f);
                return;
            }
        }
        else if(LightWithin.AP1){

            String id_bits = MinecraftClient.getInstance().player.getUuid().toString().split("-")[0];
            Color color = Color.decode("#"+id_bits.substring(0, 6));
            arm.render(matrices, vertices, light, overlay, (float) (color.getRed())/255, (float) (color.getBlue())/255, (float) (color.getGreen())/255, 0.4f);
        }
        //else
        original.call(instance, matrices, vertices, light, overlay);
    }

    @WrapOperation(
            method = "renderArm",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V", ordinal = 1)
    )
    public void applySleeveColor(ModelPart instance, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, Operation<Void> original, @Local(ordinal = 0, argsOnly = true) ModelPart arm, @Local(ordinal = 1, argsOnly = true) ModelPart sleeve){
        PlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null){
            original.call(instance, matrices, vertices, light, overlay);
            return;
        }
        if(player.hasStatusEffect(LightEffects.LIGHT_ACTIVE)){
            if(LightWithin.LIGHT_COMPONENT.get(player).getType().equals(InnerLightType.FOREST_AURA)){
                sleeve.render(matrices, vertices, light, overlay, 0.4f, 0.9f, 0.4f, 0.7f);
                return;
            }
            if(LightWithin.LIGHT_COMPONENT.get(player).getType().equals(InnerLightType.THUNDER_AURA)){
                sleeve.render(matrices, vertices, light, overlay, 0.8f, 0.8f, 0.15f, 0.7f);
                return;
            }
        }
        else if(LightWithin.AP1){

            String id_bits = MinecraftClient.getInstance().player.getUuid().toString().split("-")[0];
            Color color = Color.decode("#"+id_bits.substring(0, 6));
            sleeve.render(matrices, vertices, light, overlay, (float) (color.getRed())/255, (float) (color.getBlue())/255, (float) (color.getGreen())/255, 0.7f);
        }
        //else
        original.call(instance, matrices, vertices, light, overlay);
    }
}
