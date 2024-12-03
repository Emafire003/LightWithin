package me.emafire003.dev.lightwithin.mixin.forest_aura_related;

import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Objects;


@Mixin(PlayerEntity.class)
public abstract class RandomizeMovementPlayerEntityMixin extends LivingEntity {

    //TODO add a RandomizeJump in the LivingEntity class

    protected RandomizeMovementPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    /**Amplifier 0-1: inverted controls (forward -> backwards)
     * Amplifier 2-3: swapped X-Z
     * Amplifier 4-5: swapped X-Z, inverted X which is now Z
     * Amplifier 6-7: swapped X-Z, inverted Z which is now X
     * Amplifier 8-9: swapped X-Z, inverted both
     * Amplifier 10+: inverted controls*/
    @ModifyArg(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;travel(Lnet/minecraft/util/math/Vec3d;)V"), index = 0)
    public Vec3d invertControls(Vec3d movementInput) {
        if(this.hasStatusEffect(LightEffects.INTOXICATION)){
            int amplifier = Objects.requireNonNull(this.getStatusEffect(LightEffects.INTOXICATION)).getAmplifier();
            if(amplifier == 2 || amplifier == 3){
                movementInput = new Vec3d(movementInput.getZ(), movementInput.getY(), movementInput.getX());
            }else if (amplifier == 4 || amplifier == 5) {
                movementInput = new Vec3d(movementInput.getZ(), movementInput.getY(), movementInput.getX() * -1);
            }else if (amplifier == 6 || amplifier == 7) {
                movementInput = new Vec3d(movementInput.getZ() * -1, movementInput.getY(), movementInput.getX());
            }else if (amplifier == 8 || amplifier == 9) {
                movementInput = new Vec3d(movementInput.getZ() * -1, movementInput.getY(), movementInput.getX() * -1);
            }
            else{
                //Normal inverted controls
                movementInput = new Vec3d(movementInput.getX() * -1, movementInput.getY(), movementInput.getZ() * -1);
            }

        }
        //If the player does not have the status effect it will return the unmodified status effect
        return movementInput;
    }
}
