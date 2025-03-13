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
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

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
                if(LightWithin.LIGHT_COMPONENT.get(livingEntity).getType().equals(InnerLightType.FOREST_AURA)){
                    int og = args.get(4);
                    int color = ColorHelper.Argb.mixColor(og, ColorHelper.Argb.fromFloats(0.84F, 0.300f, 0.908f, 0.300f));
                    args.set(4, color);
                }
                if(LightWithin.LIGHT_COMPONENT.get(livingEntity).getType().equals(InnerLightType.THUNDER_AURA)){
                    int og = args.get(4);
                    int color = ColorHelper.Argb.mixColor(og, ColorHelper.Argb.fromFloats(0.84F, 0.750f, 0.750f, 0.150f));
                    args.set(4, color);
                }
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
