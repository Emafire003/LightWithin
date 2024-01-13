package me.emafire003.dev.lightwithin.status_effects;

import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.structureplacerapi.StructurePlacerAPI;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import static me.emafire003.dev.lightwithin.LightWithin.MOD_ID;

public class WaterSlideEffect extends StatusEffect {

    //LORE: Basicly your light after being used decays and well it needs to rechange so you can't use it for a while
    //it's a cool way to make a cooldown visible for the player too. As lot's have said, it's not a bug it's a feature
    //just look at it the right way
    //xD

    public WaterSlideEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xF3FF28);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the status effect every tick.
        return true;
    }

    // This method is called when it applies the status effect. We implement custom functionality here.
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(Config.STRUCTURE_GRIEFING && !entity.getWorld().isClient && !entity.getWorld().getBlockState(entity.getBlockPos()).isOf(Blocks.WATER)){
            /*StructurePlacerAPI placer = new StructurePlacerAPI((ServerWorld) entity.getWorld(), new Identifier(MOD_ID, "water_bit"), entity.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, true, 1f, new BlockPos(-1, 0, -1));
            placer.loadStructure();*/
            Direction facing = entity.getHorizontalFacing();
            //TODO move this out of the method
            BlockState water_layer = Fluids.FLOWING_WATER.getFlowing(3, false).getBlockState();
            BlockPos pos = entity.getBlockPos();

            for(int i = 1; i<=amplifier; i++){
                if(entity.getWorld().getBlockState(entity.getBlockPos().offset(facing, i)).equals(Blocks.AIR.getDefaultState())){
                    entity.getWorld().setBlockState(entity.getBlockPos().offset(facing, i), water_layer);
                }
            }
            if(entity.getWorld().getBlockState(pos).equals(Blocks.AIR.getDefaultState())){
                entity.getWorld().setBlockState(pos, water_layer);
            }

            //IF it's not bedrock etc
        }
    }
}
