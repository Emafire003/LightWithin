package me.emafire003.dev.lightwithin.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.lights.ForestAuraLight;
import me.emafire003.dev.lightwithin.lights.ThunderAuraLight;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.awt.Color;

@Mixin(LivingEntityRenderer.class)
public abstract class EntityColorOverlayRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {

    protected EntityColorOverlayRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    /** This multiplies the values, so for the player the alpha makes them transparent. And also means the color
     * won't be exactly the same one as the hex value or whatever*/
    @ModifyArgs(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"))
    public void applyColor(Args args, @Local(argsOnly = true) T livingEntity){
        //for the Aura things, a bit of transparency i think it's cool, so i'll modify the alpha value too
        if(livingEntity.getType().equals(EntityType.PLAYER)){
            if(livingEntity.hasStatusEffect(LightEffects.LIGHT_ACTIVE)){
                if(LightWithin.LIGHT_COMPONENT.get(livingEntity).getType() instanceof ForestAuraLight){
                    int og = args.get(4);
                    int color = ColorHelper.Argb.mixColor(og, ColorHelper.Argb.fromFloats(0.84F, 0.300f, 0.908f, 0.300f));
                    args.set(4, color);
                }
                if(LightWithin.LIGHT_COMPONENT.get(livingEntity).getType() instanceof ThunderAuraLight){
                    int og = args.get(4);
                    int color = ColorHelper.Argb.mixColor(og, ColorHelper.Argb.fromFloats(0.84F, 0.750f, 0.750f, 0.150f));
                    args.set(4, color);
                }
                //Only triggers on April 1st
            }else if(LightWithin.AP1){
                int og = args.get(4);

                String id_bits = livingEntity.getUuid().toString().split("-")[0];
                Color color1 = Color.decode("#"+id_bits.substring(0, 6));

                int color = ColorHelper.Argb.mixColor(og, ColorHelper.Argb.fromFloats(0.84F, (float) color1.getRed()/255, (float) color1.getGreen()/255, (float) color1.getBlue()/255));
                args.set(4, color);
            }else if(livingEntity.hasStatusEffect(LightEffects.LUXCOGNITA_DREAM)){
                int og = args.get(4);
                int color = ColorHelper.Argb.mixColor(og, ColorHelper.Argb.fromFloats(0.4F, 0.318f, 0.859f, 0.655f));
                args.set(4, color);

                Random random = livingEntity.getRandom();
                if(random.nextInt(170) == 1){
                    int filp_x = -1;
                    if(random.nextBoolean()){
                        filp_x = 1;
                    }
                    int filp_z = -1;
                    if(random.nextBoolean()){
                        filp_z = 1;
                    }
                    livingEntity.getWorld().addParticle(LightParticles.SHINE_PARTICLE, false, livingEntity.getX()+ (double) random.nextInt(15) /10*filp_x, livingEntity.getY()+1, livingEntity.getZ()+(double) random.nextInt(15)/10*filp_z, (double) random.nextInt(4) /100, (double) random.nextInt(4) /100, (double) random.nextInt(4) /100);
                }
            }
        }

    }
}
