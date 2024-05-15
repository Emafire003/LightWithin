package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.items.LightItems;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(FallingBlockEntity.class)
public abstract class AnvilItemCrushMixin extends Entity {


    @Shadow public abstract BlockState getBlockState();

    public AnvilItemCrushMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "handleFallDamage",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getOtherEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;)Ljava/util/List;",
                    shift = At.Shift.BEFORE))
    private void checkAnvilDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        List<ItemEntity> items = world.getEntitiesByClass(ItemEntity.class, this.getBoundingBox(), (entity1 -> entity1.getStack().isOf(LightItems.LUXINTUS_BERRY)));
        BlockPos pos = this.getBlockPos();
        int luxintus_exploding = (int) (10+(fallDistance/10));
        boolean explosion = false;

        for(ItemEntity entity : items){
            entity.damage(DamageSource.ANVIL, 20);
            //Luxintus berry chances: 75% you get the powder, 15%, too crushed the item disappears, 10% way to crushed the item explodes
            //For every 5 blocks the % of exploding is increased by one
            for(int i = 0; i < entity.getStack().getCount(); i++){
                int chance = entity.getWorld().getRandom().nextInt(100);
                //Explodes
                if(chance < luxintus_exploding){
                    explosion = true;
                    break;
                }else
                //10+15=25, 100-25 = 75%.
                //Chance of getting the item
                if(chance > luxintus_exploding+15){
                    ItemEntity powder = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(LightItems.LUXINTUS_BERRY_POWDER));
                    world.spawnEntity(powder);
                    world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_AMETHYST_BLOCK_PLACE, SoundCategory.BLOCKS, 1f, 1.5f, true);
                    if(!entity.getWorld().isClient()){
                        ((ServerWorld)entity.getWorld()).spawnParticles(LightParticles.LIGHT_PARTICLE, pos.getX(), pos.getY(), pos.getZ(), 30,0.01,0.01, 0.01, 0.1);
                    }
                }else{
                    world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1f, 1.7f, true);
                    //If nothing triggered, then it just disappears! Woo!
                }

            }
            if(explosion){
                ExplosionBehavior explosionBehavior = new ExplosionBehavior() {
                    @Override
                    public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
                        if(blockState.isAir() && fluidState.isEmpty()){
                            return Optional.empty();
                        }else if(blockState.isOf(Blocks.ANVIL)){
                            return Optional.of(Math.max(Blocks.OAK_LOG.getBlastResistance(), fluidState.getBlastResistance()));
                        }
                        return Optional.of(Math.max(blockState.getBlock().getBlastResistance(), fluidState.getBlastResistance()));
                    }
                };

                if(!entity.getWorld().isClient()){
                    ((ServerWorld)entity.getWorld()).spawnParticles(ParticleTypes.FLASH, pos.getX(), pos.getY(), pos.getZ(), 1,0,0, 0, 0.1);
                }
                //entity.getWorld().addParticle(ParticleTypes.FLASH, pos.getX(), pos.getY(), pos.getZ(), 0,0,0);
                entity.getWorld().createExplosion(entity, DamageSource.GENERIC, explosionBehavior, entity.getX(), entity.getY(), entity.getZ(), 2f, true, Explosion.DestructionType.DESTROY);
            }
        }

    }
}
