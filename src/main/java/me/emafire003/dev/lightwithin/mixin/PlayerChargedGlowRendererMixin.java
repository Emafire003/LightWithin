package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.ClientConfig;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class PlayerChargedGlowRendererMixin<T extends Entity>   {

    @Inject(method = "getBlockLight", at = @At("TAIL"), cancellable = true)
    protected void injectLightGlow(T entity, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if(entity instanceof PlayerEntity){
            if(!entity.getWorld().isClient() || !ClientConfig.SHOW_CHARGED_PLAYER_GLOW){
                return;
            }
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(entity);
            if(component.getCurrentLightCharges() == 0){
                return;
            }
            double charge_fraction = (double) component.getCurrentLightCharges() /component.getMaxLightStack();
            if(charge_fraction == 1){
                cir.setReturnValue(15);
                Random random = ((PlayerEntity) entity).getRandom();
                if(random.nextInt(170) == 1){
                    int filp_x = -1;
                    if(random.nextBoolean()){
                        filp_x = 1;
                    }
                    int filp_z = -1;
                    if(random.nextBoolean()){
                        filp_z = 1;
                    }
                    entity.getWorld().addParticle(LightParticles.LIGHT_PARTICLE, false, entity.getX()+ (double) random.nextInt(15) /10*filp_x, entity.getY()+1, entity.getZ()+(double) random.nextInt(15)/10*filp_z, (double) random.nextInt(4) /100, (double) random.nextInt(4) /100, (double) random.nextInt(4) /100);
                }
                return;
            }
            if(charge_fraction > 0.5){
                cir.setReturnValue(10);
                return;
            }
            cir.setReturnValue(5);
        }
    }
}
