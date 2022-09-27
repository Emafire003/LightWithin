package me.emafire003.dev.lightwithin.status_effects;

import me.emafire003.dev.coloredglowlib.ColoredGlowLib;
import me.emafire003.dev.coloredglowlib.util.Color;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.lights.InnerLight;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.util.TargetType;
import me.emafire003.dev.structureplacerapi.StructurePlacerAPI;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static me.emafire003.dev.lightwithin.LightWithin.LIGHT_COMPONENT;
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
            StructurePlacerAPI placer = new StructurePlacerAPI((ServerWorld) entity.getWorld(), new Identifier(MOD_ID, "water_bit"), entity.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, true, 1f, new BlockPos(-1, 0, -1));
            placer.loadStructure();
        }
    }
}
