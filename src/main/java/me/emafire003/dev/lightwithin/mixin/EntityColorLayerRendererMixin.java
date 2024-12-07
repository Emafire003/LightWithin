package me.emafire003.dev.lightwithin.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

//TODO clean up the mess after this is confirmed working as it should
@Debug(export = true)
@Mixin(LivingEntityRenderer.class)
public abstract class EntityColorLayerRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {


    @Shadow protected M model;

    protected EntityColorLayerRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @ModifyArgs(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    public void applyColorGreen(Args args, @Local(argsOnly = true) LivingEntity livingEntity){
        // green color 199b12 rgb deciamls 0.098, 0.608, 0.071
        if(livingEntity.getType().equals(EntityType.PLAYER)){
            args.set(4, 0.098f);
            args.set(5, 0.608f);
            args.set(6, 0.071f);
            args.set(7, 0.5f);
        }
        //TODO for some reason the player renders transparent compleately. And it's the only one doing so
        if(livingEntity.getType().equals(EntityType.CREEPER)){
            args.set(4, 0.01f);
            args.set(5, 0.5f);
            args.set(6, 0.5f);
            args.set(7, 0.5f);
        }

    }

   /* private final NativeImageBackedTexture texture = new NativeImageBackedTexture(16, 16, false);


    //TODO well as the damage overlay it works :/
    public void setupImage(int a, int r, int g, int b) {
        NativeImage nativeImage = this.texture.getImage();

        for(int i = 0; i < 16; ++i) {
            for(int j = 0; j < 16; ++j) {
                /*if (i < 8) {
                    RGBA(255,0,0,178) Questo valore ABGR ed è rosso. Ora, coem cabbo lo applico al coso?
                    //nativeImage.setColor(j, i, -1308622593);
                } else {
                    int k = (int)((1.0F - (float)j / 15.0F * 0.75F) * 255.0F);
                    nativeImage.setColor(j, i, k << 24 | 16777215);
                }
                Color rgbColor = new Color(r,g,b,a);
                int color  = rgbColor.getRed() | rgbColor.getGreen() << 8 | rgbColor.getBlue() << 16 | Math.round(0xFF * (1 - (float) rgbColor.getAlpha() / 100)) << 24;
                nativeImage.setColor(j, i, ColorHelper.Abgr.getAbgr(a, b,g,r));
            }

        }


        RenderSystem.activeTexture(GlConst.GL_TEXTURE1);
        this.texture.bindTexture();
        nativeImage.upload(0, 0, 0, 0, 0, nativeImage.getWidth(), nativeImage.getHeight(), false, true, false, false);
        RenderSystem.activeTexture(GlConst.GL_TEXTURE0);
        RenderSystem.setupOverlayColor(this.texture::getGlId, 16);
    }*/

    /*@Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    public void applyColor(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
        if(livingEntity.getType().equals(EntityType.IRON_GOLEM)){
            //TODO ok this does not work
            //setupImage(50, 10, 200, 20);
            //TODO well the damage overlay works :/
            //RenderSystem.setShaderColor(0, 200, 0, 100);
            //TODO nope, Vertex it will need to be
            RenderSystem.setShaderTexture(0, new Identifier(LightWithin.MOD_ID, "textures/gui/overlay_test.png"));

        }
    }*/

    /**
     * FREEDINNER — 20/03/2024 20:07
     * Hey, sorry for pinging, but did you manage to find a solution?
     * I'm also having a similar problem, because I need a way to overlay any living entity with a color
     * Papierkorb2292 — 20/03/2024 20:13
     * When the color is supposed to be applied, you can wrap the VertexConsumerProvider given to the entity and return a also wrapped version of the  VertexConsumers returned by the original VertexConsumerProvider.
     * Your wrapper around the VertexConsumers can then multiply everything given to .color and then pass it through to the original.
     * FREEDINNER — 20/03/2024 20:22
     * Unfortunately I'm not very familiar with VertexConsumers
     * Is there another place where the color gets applied?
     * Except the LivingEntityRenderer
     * Papierkorb2292 — 20/03/2024 20:31
     * By doing this, you can make sure that everything the entity renderer renders with the given VertexConsumerProvider has the color applied to it.
     *
     * VertexConsumer is just an interface with a few methods that rendering code uses to define data for each vertex. Like .vertex for the position, .color for the color and .texture for the texture coordinate.
     * The goal would be to create your own class implementing VertexConsumer, which passess all this data along to the original modification, but can multiply the color before that. The VertexConsumers are given to the entity renderer through a VertexConsumerProvider, which is another interface with only one method: This method gets a RenderLayer (basically a RenderLayer defines various settings for what is about to be rendered) and returns a VertexConsumer. Vertices given to that VertexConsumer will then be rendered using the settings from the RenderLayer.
     * You can simply use a lambda to wrap the VertexConsumerProvider:
     * layer -> new MyVertexConsumerWrapper(original.getBuffer(layer)), where MyVertexConsumerWrapper takes VertexConsumer and gives the vertex data with the multiplied color to it.
     * FREEDINNER — 20/03/2024 20:39
     * Thank you for the detailed explanation
     * I will try to look into that now*/

    @ModifyReturnValue(method = "getOverlay", at = @At("RETURN"))
    private static int addAuraColorOverlay(int original, @Local(argsOnly = true) LivingEntity entity){
        //TODO modify into a proper check
        if(entity.getType().equals(EntityType.COW)){
            return ColorHelper.Argb.getArgb(200, 20, 255, 20);
        }
        //Normal color: 655360 | Hurt color (maybe green): 196608
        return original;
        /*if (entity.getType().equals(EntityType.COW)){
            //0 is the whiteProgress thingy
            //0,16 is black, as any 16. 5-10 sligtly whiter. 5,5 red

            //ok any 16 means black. todo 5-12 the cow is like frozen color
            //return OverlayTexture.packUv(-1, 15);
            try {
                ColoredOverlayTexture overlayTexture = new ColoredOverlayTexture(100, 0, 200, 20);
                overlayTexture.setupOverlayColor();
                int color = overlayTexture.packUv(0, 16);
                overlayTexture.close();
                //TODO may need a tear down
                return color;
            }catch (Exception e){
                LightWithin.LOGGER.error("It exploded. The error is:");
                e.printStackTrace();
            }
            //black
            //return ColorHelper.Abgr.getAbgr(100, 10, 200, 0);
            //should be red
            //TODO this means normal 655360
            //return 0xB000FF00;//blackColorHelper.Argb.getArgb(0, 0, 200, 10);
        }
        if (entity.getType().equals(EntityType.IRON_GOLEM)){
            Color rgbColor = new Color(0,200,10,50);
            return rgbColor.getRed() | rgbColor.getGreen() << 8 | rgbColor.getBlue() << 16 | Math.round(0xFF * (1 - (float) rgbColor.getAlpha() / 100)) << 24;

            //return OverlayTexture.packUv(1, 1);//blackColorHelper.Abgr.getAbgr(0, 10, 200, 0);
            //LightWithin.LOGGER.warn("The originale color would: " + original + "\n while mine is " + ColorHelper.Abgr.getAbgr(100, 10, 200, 0));
        }
        if (entity.getType().equals(EntityType.PLAYER)){
            ((IOverlayUpdater) MinecraftClient.getInstance().gameRenderer.getOverlayTexture()).lightWithin$updateOverlay(0,200,10,50);
            int color = OverlayTexture.packUv(2, 16);
            //((IOverlayUpdater) MinecraftClient.getInstance().gameRenderer.getOverlayTexture()).lightWithin$resetOverlay();
            return color;
        }*/
    }

    /*@Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("TAIL"))
    protected void injectColorAura(T entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
        //TODO move into a check later
        VertexConsumer buffer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(this.getTexture(entity)));
        buffer.color(0, 255, 0, 255);
        buffer.next();
        if(entity instanceof PlayerEntity){
            //TODO add config option
            //TODO warning we may be on client side only
            if(!entity.getWorld().isClient()){
                return;
            }
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(entity);
            //TODO move to has forest aura && is active
            if(component.getType().equals(InnerLightType.FOREST_AURA)){
                //maybe mixin into LivingEntityRenderer, or the one for the model
                //TODO will this work? Who knows!
                //TODO let's put it back here later
            }
        }
    }*/
}
