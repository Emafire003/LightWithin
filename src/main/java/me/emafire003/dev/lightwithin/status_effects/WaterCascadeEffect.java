package me.emafire003.dev.lightwithin.status_effects;

import me.emafire003.dev.lightwithin.config.Config;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;

public class WaterCascadeEffect extends StatusEffect {

    public WaterCascadeEffect() {
        super(StatusEffectCategory.HARMFUL, 0x0053FF);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the status effect every tick.
        return true;
    }

    HashMap<BlockPos, BlockState> block_map = new HashMap<>();

    BlockPos start_pos;

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(Config.STRUCTURE_GRIEFING){
            BlockPos pos = entity.getBlockPos();
            if(block_map.isEmpty()){
                start_pos = pos;
                block_map.put(pos, entity.getWorld().getBlockState(pos));
            }else{
                if(block_map.containsKey(pos.up())){
                    return;
                }
                block_map.put(pos.up(), entity.getWorld().getBlockState(pos.up()));
            }

            entity.getWorld().setBlockState(pos.up(), Fluids.WATER.getFlowing(7, true).getBlockState());
            Vec3d posc = pos.toCenterPos();
            entity.teleport(posc.getX(), posc.getY()+1, posc.getZ());


        }
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier){

        block_map.forEach(((blockPos, blockState) -> {
            if(blockPos.equals(start_pos)){
                entity.getWorld().setBlockState(start_pos, Blocks.SPONGE.getDefaultState());
            }
            entity.getWorld().setBlockState(blockPos, blockState);
        }));

        block_map.clear();
        super.onRemoved(entity, attributes, amplifier);
    }

}
