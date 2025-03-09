package me.emafire003.dev.lightwithin.mixin;


import me.emafire003.dev.lightwithin.util.IRenderEffectsEntity;
import me.emafire003.dev.lightwithin.util.RenderEffect;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class CustomEffectsLivingRendererMixin<T extends Entity> {

    @Unique
    private short shouldRenderTicks = 0;
    @Inject(method = "render*", at = @At("HEAD"))
    protected void injectRendering(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci){
        IRenderEffectsEntity renderEntity = (IRenderEffectsEntity) entity;
        if(renderEntity.lightWithin$shouldRender(RenderEffect.LIGHT_RAYS)){
            renderLightRaysEffect(vertexConsumers, matrices, tickDelta, entity);
            shouldRenderTicks++;
            if(shouldRenderTicks > renderEntity.lightWithin$getRenderTicks(RenderEffect.LIGHT_RAYS)){
                shouldRenderTicks = 0;
                renderEntity.lightWithin$stopEffect(RenderEffect.LIGHT_RAYS);
            }
        }else if(renderEntity.lightWithin$shouldRender(RenderEffect.FORCED_LIGHT_RAYS)){
            renderForcedLightRaysEffect(vertexConsumers, matrices, tickDelta, entity);
            shouldRenderTicks++;
            if(shouldRenderTicks > renderEntity.lightWithin$getRenderTicks(RenderEffect.FORCED_LIGHT_RAYS)){
                shouldRenderTicks = 0;
                renderEntity.lightWithin$stopEffect(RenderEffect.FORCED_LIGHT_RAYS);
            }
        }
    }

    @Unique
    public void renderLightRaysEffect(VertexConsumerProvider vertexConsumerProvider, MatrixStack matrixStack, float tickDelta, T entity){
        float f = ((float)shouldRenderTicks + tickDelta) / 200.0F;
        float m = Math.min(f > 0.8F ? (f - 0.8F) / 0.2F : 0.0F, 1.0F);
        Random random = Random.create(432L);
        VertexConsumer vertexConsumer4 = vertexConsumerProvider.getBuffer(RenderLayer.getLightning());
        matrixStack.push();

        //This is an offset to the center of the player.
        //matrixStack.translate(0.0f, 1.0f, 0.0f);
        matrixStack.translate(0.0f, entity.getDimensions(entity.getPose()).height()/2, 0.0f);

        for(int n = 0; (float)n < (f + f * f) / 2.0F * 60.0F; ++n) {
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(random.nextFloat() * 360.0F));
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(random.nextFloat() * 360.0F));
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(random.nextFloat() * 360.0F));
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(random.nextFloat() * 360.0F));
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(random.nextFloat() * 360.0F));
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(random.nextFloat() * 360.0F + f * 90.0F));

            float radius = 2f;
            float width = 0.2f;

            Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
            int alpha = (int)(255.0F * (1.0F - m));
            if(random.nextInt(2) == 1){
                putLightSourceVertex(vertexConsumer4, matrix4f, alpha, rays_color_x);
            }else{
                putLightSourceVertex(vertexConsumer4, matrix4f, alpha, rays_color_y);
            }

            putLightNegativeXTerminalVertex(vertexConsumer4, matrix4f, radius, width, rays_color_y);
            putLightPositiveXTerminalVertex(vertexConsumer4, matrix4f, radius, width/2, rays_color_x);
            putLightPositiveZTerminalVertex(vertexConsumer4, matrix4f, radius, width, rays_color_y);
        }

        matrixStack.pop();
    }

    @Unique
    public void renderForcedLightRaysEffect(VertexConsumerProvider vertexConsumerProvider, MatrixStack matrixStack, float tickDelta, T entity){
        float f = ((float)shouldRenderTicks + tickDelta) / 200.0F;
        float m = Math.min(f > 0.8F ? (f - 0.8F) / 0.2F : 0.0F, 1.0F);
        Random random = Random.create(432L);
        VertexConsumer vertexConsumer4 = vertexConsumerProvider.getBuffer(RenderLayer.getLightning());
        matrixStack.push();

        //This is an offset to the center of the player.
        //matrixStack.translate(0.0f, 1.0f, 0.0f);
        matrixStack.translate(0.0f, entity.getDimensions(entity.getPose()).height/2, 0.0f);

        for(int n = 0; (float)n < (f + f * f) / 2.0F * 60.0F; ++n) {
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(random.nextFloat() * 360.0F));
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(random.nextFloat() * 360.0F));
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(random.nextFloat() * 360.0F));
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(random.nextFloat() * 360.0F));
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(random.nextFloat() * 360.0F));
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(random.nextFloat() * 360.0F + f * 90.0F));

            float radius = 2f;
            float width = 0.2f;

            Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
            int alpha = (int)(255.0F * (1.0F - m));
            if(random.nextInt(2) == 1){
                putLightSourceVertex(vertexConsumer4, matrix4f, alpha, rays_color_x);
            }else{
                putLightSourceVertex(vertexConsumer4, matrix4f, alpha, corrupted_rays_color_y);
            }

            putLightNegativeXTerminalVertex(vertexConsumer4, matrix4f, radius, width, corrupted_rays_color_y);
            putLightPositiveXTerminalVertex(vertexConsumer4, matrix4f, radius, width/2, corrupted_rays_color_y);
            putLightPositiveZTerminalVertex(vertexConsumer4, matrix4f, radius, width, corrupted_rays_color_y);
        }

        matrixStack.pop();
    }

    @Unique
    private static final float HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);

    /**Light blue rays color (the primary color)*/
    @Unique
    private static final Vec3i rays_color_x = new Vec3i(104, 240, 210);

    /**Yellow rays color (the secondary color)*/
    @Unique
    private static final Vec3i rays_color_y = new Vec3i(200, 224, 90);

    //Alternativa: 4c0b10 76, 11, 16
    @Unique
    private static final Vec3i corrupted_rays_color_y = new Vec3i(76, 11, 49);//4c0b31

    //A slightly more gray blue rgb(129, 217, 197)
    //Yellowier yellow rgb(244, 232, 73)
    @Unique
    private static void putLightSourceVertex(VertexConsumer buffer, Matrix4f matrix, int alpha, Vec3i color) {
        //The color seems to be the one of the border
        //Modifying the first values stretches the lines
        //it seems this is the primary/source color
        buffer.vertex(matrix, 0.0F, 0.0F, 0.0F).color(color.getX(), color.getY(), color.getZ(), alpha);
    }

    @Unique
    private static void putLightNegativeXTerminalVertex(VertexConsumer buffer, Matrix4f matrix, float radius, float width, Vec3i color) {
        buffer.vertex(matrix, -HALF_SQRT_3 * width, radius, -0.5F * width).color(color.getX(), color.getY(), color.getZ(), 0);
        //buffer.vertex(matrix, width, radius, -0.5F * width).color(red_c, green_c, blue_c, 0);

    }

    @Unique
    private static void putLightPositiveXTerminalVertex(VertexConsumer buffer, Matrix4f matrix, float radius, float width, Vec3i color) {
        buffer.vertex(matrix, HALF_SQRT_3 * width, radius, -0.5F * width).color(color.getX(), color.getY(), color.getZ(), 0);
        //buffer.vertex(matrix, width, radius, -0.5F * width).color(red_c, green_c, blue_c, 0);

    }
    @Unique

    private static void putLightPositiveZTerminalVertex(VertexConsumer buffer, Matrix4f matrix, float radius, float width, Vec3i color) {
        buffer.vertex(matrix, 0.0F, radius, width).color(color.getX(), color.getY(), color.getZ(), 0);
        //buffer.vertex(matrix, 0.0f, radius, width).color(red_c, green_c, blue_c, 0).next();
    }
}
