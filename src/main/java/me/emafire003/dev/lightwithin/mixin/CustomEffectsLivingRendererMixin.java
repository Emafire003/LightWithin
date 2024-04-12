package me.emafire003.dev.lightwithin.mixin;


import me.emafire003.dev.lightwithin.util.IRenderEffectsEntity;
import me.emafire003.dev.lightwithin.util.RenderEffect;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class CustomEffectsLivingRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {

    @Shadow @Final private static Logger LOGGER;

    protected CustomEffectsLivingRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Unique
    private short shouldRenderTicks = 0;
    @Inject(method = "render*", at = @At("HEAD"))
    protected void injectRendering(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci){
        IRenderEffectsEntity renderEntity = (IRenderEffectsEntity) entity;
        if(renderEntity.lightWithin$shouldRender(RenderEffect.LIGHT_EXPLOSION_EFFECT)){
            renderLightEffect(vertexConsumers, matrices, tickDelta);
            shouldRenderTicks++;
            if(shouldRenderTicks > 100){
                shouldRenderTicks = 0;
                renderEntity.lightWithin$stopEffect(RenderEffect.LIGHT_EXPLOSION_EFFECT);
            }
        }
    }

    @Unique
    public void renderLightEffect(VertexConsumerProvider vertexConsumerProvider, MatrixStack matrixStack, float tickDelta){
        float f = ((float)shouldRenderTicks + tickDelta) / 200.0F;
        float m = Math.min(f > 0.8F ? (f - 0.8F) / 0.2F : 0.0F, 1.0F);
        Random random = Random.create(432L);
        VertexConsumer vertexConsumer4 = vertexConsumerProvider.getBuffer(RenderLayer.getLightning());
        matrixStack.push();
        //matrixStack.translate(0.0F, -1.0F, -2.0F);
        matrixStack.translate(0.0f, 1.0f, 0.0f);

        for(int n = 0; (float)n < (f + f * f) / 2.0F * 60.0F; ++n) {
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(random.nextFloat() * 360.0F));
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(random.nextFloat() * 360.0F));
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(random.nextFloat() * 360.0F));
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(random.nextFloat() * 360.0F));
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(random.nextFloat() * 360.0F));
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(random.nextFloat() * 360.0F + f * 90.0F));
            float radius = random.nextFloat() * 20.0F + 5.0F + m * 10.0F;
            float width = random.nextFloat() * 2.0F + 1.0F + m * 2.0F;
            LOGGER.info("The radius is: " + radius + " the width is: " + width);
            //float radius = 1f;
            //float width = 1f;
            radius = radius/5.7f;
            width = width/5.3f;
            LOGGER.info("The edited radius is: " + radius + " the width is: " + width);
            Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
            int alpha = (int)(255.0F * (1.0F - m));
            putLightSourceVertex(vertexConsumer4, matrix4f, alpha);
            putLightNegativeXTerminalVertex(vertexConsumer4, matrix4f, radius, width);
            putLightPositiveXTerminalVertex(vertexConsumer4, matrix4f, radius, width);
            putLightSourceVertex(vertexConsumer4, matrix4f, alpha);
            putLightPositiveXTerminalVertex(vertexConsumer4, matrix4f, radius, width);
            putLightPositiveZTerminalVertex(vertexConsumer4, matrix4f, radius, width);
            putLightSourceVertex(vertexConsumer4, matrix4f, alpha);
            putLightPositiveZTerminalVertex(vertexConsumer4, matrix4f, radius, width);
            putLightNegativeXTerminalVertex(vertexConsumer4, matrix4f, radius, width);
        }

        matrixStack.pop();
    }

    @Unique
    private static final float HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);

    @Unique
    private static final int red_c = 200;//104;

    @Unique
    private static final int green_c = 224;//240;

    @Unique
    private static final int blue_c = 90;//210;

    @Unique
    private static void putLightSourceVertex(VertexConsumer buffer, Matrix4f matrix, int alpha) {
        //The color seems to be the one of the border
        //Modifiing the first values streches the lines
        //TODO also it seems this is the primary/source color
        buffer.vertex(matrix, 0.0F, 0.0F, 0.0F).color(200, 224, 90, alpha).next();
    }

    @Unique
    private static void putLightNegativeXTerminalVertex(VertexConsumer buffer, Matrix4f matrix, float radius, float width) {
        //TODO i think all the other ones apart from the one above are connected, so i need to moidify them toghether. Putting the above one to 0 0 0 makes this one go blueish with still some purple, if a remove the 255 red
        buffer.vertex(matrix, -HALF_SQRT_3 * width, radius, -0.5F * width).color(red_c, green_c, blue_c, 0).next();
        //buffer.vertex(matrix, width, radius, -0.5F * width).color(red_c, green_c, blue_c, 0).next();

    }

    @Unique
    private static void putLightPositiveXTerminalVertex(VertexConsumer buffer, Matrix4f matrix, float radius, float width) {
        buffer.vertex(matrix, HALF_SQRT_3 * width, radius, -0.5F * width).color(red_c, green_c, blue_c, 0).next();
        //buffer.vertex(matrix, width, radius, -0.5F * width).color(red_c, green_c, blue_c, 0).next();

    }
    @Unique

    private static void putLightPositiveZTerminalVertex(VertexConsumer buffer, Matrix4f matrix, float radius, float width) {
        buffer.vertex(matrix, 0.0F, radius, width).color(red_c, green_c, blue_c, 0).next();
        //buffer.vertex(matrix, 0.0f, radius, width).color(red_c, green_c, blue_c, 0).next();
    }
}
