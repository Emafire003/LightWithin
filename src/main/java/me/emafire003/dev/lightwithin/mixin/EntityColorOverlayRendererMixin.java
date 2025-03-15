package me.emafire003.dev.lightwithin.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.awt.Color;

@Mixin(LivingEntityRenderer.class)
public abstract class EntityColorOverlayRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {

    @Shadow @Final private static Logger LOGGER;

    protected EntityColorOverlayRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    /** This multiplies the values, so for the player the alpha makes them transparent. And also means the color
     * won't be exactly the same one as the hex value or whatever*/
    @ModifyArgs(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    public void applyColor(Args args, @Local(argsOnly = true) LivingEntity livingEntity){

        //for the Aura things, a bit of transparency i think it's cool, so i'll modify the alpha value too
        if(livingEntity.getType().equals(EntityType.PLAYER)){
            if(livingEntity.hasStatusEffect(LightEffects.LIGHT_ACTIVE)){
                if(LightWithin.LIGHT_COMPONENT.get(livingEntity).getType().equals(InnerLightType.FOREST_AURA)){
                    float or = args.get(4); //Original values, like OriginalRed
                    float og = args.get(5);
                    float ob = args.get(6);
                    float oa = args.get(7);
                    //This is done for compatibility with other changes
                    args.set(4, or*0.300f);
                    args.set(5, og*0.908f);
                    args.set(6, ob*0.300f);
                    args.set(7, oa*0.77f);//before it was 0.5 which is alright. It does work but is veeeery green
                }

                if(LightWithin.LIGHT_COMPONENT.get(livingEntity).getType().equals(InnerLightType.THUNDER_AURA)){
                    float or = args.get(4); //Original values, like OriginalRed
                    float og = args.get(5);
                    float ob = args.get(6);
                    float oa = args.get(7);
                    //This is done for compatibility with other changes
                    args.set(4, or*0.750f);
                    args.set(5, og*0.750f);
                    args.set(6, ob*0.150f);
                    args.set(7, oa*0.84f);
                }
                //Only triggers on April 1st
            }else if(LightWithin.AP1){
                float oa = args.get(7);

                String id_bits = livingEntity.getUuid().toString().split("-")[0];
                Color color = Color.decode("#"+id_bits.substring(0, 6));

                //This is done for compatibility with other changes
                args.set(4, (float) (color.getRed())/255);
                args.set(5, (float) (color.getBlue())/255);
                args.set(6, (float) (color.getGreen())/255);
                args.set(7, oa*0.75f);
            }
        }
        /*TEST STUFF
        if(livingEntity.getType().equals(EntityType.CREEPER)){

            args.set(4, 0.01f);
            args.set(5, 0.5f);
            args.set(6, 0.5f);
            args.set(7, 0.2f);
        }*/

    }
}
