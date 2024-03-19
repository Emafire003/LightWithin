package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.items.LightItems;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(AnvilBlock.class)
public abstract class AnvilItemCrushMixin extends FallingBlock {

    public AnvilItemCrushMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onLanding", at = @At("HEAD"))
    private void checkAnvilDamage(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, FallingBlockEntity fallingBlockEntity, CallbackInfo ci) {
        List<ItemEntity> items = world.getEntitiesByClass(ItemEntity.class, new Box(pos), (entity1 -> {
            return entity1.getStack().isOf(LightItems.LUXINTUS_BERRY);
        }));
        int luxintus_exploding = (int) (10+(fallingBlockEntity.timeFalling/10));
        boolean explosion = false;

        for(ItemEntity entity : items){
            entity.damage(entity.getDamageSources().fallingAnvil(fallingBlockEntity), 20);
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
                    world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_AMETHYST_BLOCK_PLACE, SoundCategory.BLOCKS, 1f, 1.5f, true);
                }else{
                    world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1f, 1.7f, true);
                    //If nothing triggered, then it just disappears! Woo!
                }

            }
            if(explosion){
                ExplosionBehavior explosionBehavior = new ExplosionBehavior() {
                    @Override
                    public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
                        return Optional.of(0.5f);
                        //return blockState.isAir() && fluidState.isEmpty() ? Optional.empty() : Optional.of(Math.max(blockState.getBlock().getBlastResistance(), fluidState.getBlastResistance()));
                    }
                };

                entity.getWorld().addParticle(ParticleTypes.FLASH, pos.getX(), pos.getY(), pos.getZ(), 0,0,0);
                //TODO maybe don't let it make explode obsidian and bedrock
                entity.getWorld().createExplosion(entity, entity.getDamageSources().explosion(entity, fallingBlockEntity), explosionBehavior, entity.getPos(), 2f, true, World.ExplosionSourceType.BLOCK);
            }
        }

    }
}
