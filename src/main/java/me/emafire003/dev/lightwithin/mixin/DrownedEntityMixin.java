package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.SummonedByComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DrownedEntity.class)
public abstract class DrownedEntityMixin extends ZombieEntity implements RangedAttackMob {

    public DrownedEntityMixin(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "canDrownedAttackTarget", at = @At("HEAD"), cancellable = true)
    public void canAttackIfNotAquaLight(LivingEntity target, CallbackInfoReturnable<Boolean> cir){
        SummonedByComponent component = LightWithin.SUMMONED_BY_COMPONENT.get(((DrownedEntity)(Object)this));
        if(target != null && component.getIsSummoned()){
            //TODO if I will implement levels, maybe at low levels once the light has been triggered the drowned will get hostile
            if(component.getSummonerUUID().equals(target.getUuid())){
                cir.setReturnValue(false);
            }else{
                cir.setReturnValue(true);
            }
        }
    }


}
