package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.blocks.LightBlocks;
import me.emafire003.dev.lightwithin.compat.coloredglowlib.CGLCompat;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.TargetType;
import me.emafire003.dev.structureplacerapi.StructurePlacerAPI;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.stream.Stream;

import static me.emafire003.dev.lightwithin.LightWithin.*;

public class FrostLight extends InnerLight {

    /*Possible triggers:
       - self low health
       - allies low health (checkable like this if type = Heal && target = allies do stuff
       - surrounded++
       - Ally dying?
     */

    /*Triggers:
    * - Snowing & HP < 75% (not ally)
    * - Freezing & HP < 50%
    * - HP < 25% (&& freeze stuff in hand/around)
    * - Ally dying
    *
    * - When attacked by a snowball && HP <75%
    * */

    /*Possible targets:
    * - enemies
    * - all
    * - ally/self -> protective wall and other things. Maybe a buff of some sort.*/

    public FrostLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, String color, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.FROST;
    }

    public FrostLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.FROST;
        color = "96fbff";
    }

    public FrostLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.FROST;
        color = "96fbff";
    }

    private void checkSafety(){
        if(this.power_multiplier > Config.FROST_MAX_POWER){
            power_multiplier = Config.FROST_MAX_POWER;
        }
        if(this.power_multiplier < Config.FROST_MIN_POWER){
            power_multiplier = Config.FROST_MIN_POWER;
        }
        int max_duration = Config.FROST_MAX_DURATION;
        if(Config.MULTIPLY_DURATION_LIMIT){
            max_duration = (int) (Config.FROST_MAX_DURATION * Config.DURATION_MULTIPLIER);
        }
        if(this.duration > max_duration){
            this.duration = max_duration;
        }
        if(this.duration < Config.FROST_MIN_DURATION){
            this.duration = Config.FROST_MIN_DURATION;
        }
    }

    @Override
    public void execute(){
        checkSafety();

        LightComponent component = LightWithin.LIGHT_COMPONENT.get(caster);
        if(FabricLoader.getInstance().isModLoaded("coloredglowlib")){
            if(this.rainbow_col){
                CGLCompat.getLib().setRainbowColorToEntity(this.caster, true);
            }else{
                CGLCompat.getLib().setColorToEntity(this.caster, CGLCompat.fromHex(this.color));
            }
        }


        caster.getWorld().playSound(caster.getX(), caster.getY(), caster.getZ(), LightSounds.FROST_LIGHT, SoundCategory.AMBIENT, 1, 1, true);
        if((Config.STRUCTURE_GRIEFING || Config.NON_FUNDAMENTAL_STRUCTURE_GRIEFING) && !caster.getWorld().isClient && (component.getTargets().equals(TargetType.ALL) || component.getTargets().equals(TargetType.ENEMIES))) {
            StructurePlacerAPI placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), new Identifier(MOD_ID, "frost_light"), caster.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, true, 1.0f, new BlockPos(-4, -3, -3));
            placer.loadStructure();
        }

        LightParticlesUtil.spawnSnowflake((ServerPlayerEntity) caster, caster.getPos().add(0, 2, 0));

        for(LivingEntity target : this.targets){
            target.playSound(LightSounds.FROST_LIGHT, 1, 1);

            if((component.getTargets().equals(TargetType.SELF) || component.getTargets().equals(TargetType.ALLIES))){
                if(target.equals(caster) && component.getTargets().equals(TargetType.ALLIES)){
                    target.addStatusEffect(new StatusEffectInstance(LightEffects.FREEZE_RESISTANCE, (int) (this.duration/Config.DIV_SELF*Config.FROST_FREEZE_RES_DURATION_MULTIPLIER)*20));
                }else{
                    target.addStatusEffect(new StatusEffectInstance(LightEffects.FREEZE_RESISTANCE, (int) (this.duration*Config.FROST_FREEZE_RES_DURATION_MULTIPLIER)*20));
                }

                Direction facing = target.getHorizontalFacing();
                if(Config.STRUCTURE_GRIEFING && !caster.getWorld().isClient){
                    StructurePlacerAPI placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), new Identifier(MOD_ID, "frost_wall"), target.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, true, 1.0f, new BlockPos(-2, -1, -2));
                    if(facing.equals(Direction.EAST)){
                        placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), new Identifier(MOD_ID, "frost_wall"), target.getBlockPos(), BlockMirror.NONE, BlockRotation.CLOCKWISE_90, true, 1.0f, new BlockPos(2, -1, -2));
                    }else if(facing.equals(Direction.SOUTH)){
                        placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), new Identifier(MOD_ID, "frost_wall"), target.getBlockPos(), BlockMirror.NONE, BlockRotation.CLOCKWISE_180, true, 1.0f, new BlockPos(2, -1, 2));
                    }else if(facing.equals(Direction.WEST)){
                        placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), new Identifier(MOD_ID, "frost_wall"), target.getBlockPos(), BlockMirror.NONE, BlockRotation.COUNTERCLOCKWISE_90, true, 1.0f, new BlockPos(-2, -1, 2));
                    }
                    placer.loadStructure();
                }
            }else{
                if(!caster.getWorld().isClient){
                    LightParticlesUtil.spawnLightTypeParticle(LightParticles.FROSTLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), target.getPos());
                    LightParticlesUtil.spawnLightTypeParticle(LightParticles.FROSTLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());

                    target.addStatusEffect(new StatusEffectInstance(LightEffects.FROST, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), 0, false, false));

                    target.damage(caster.getWorld().getDamageSources().freeze(), (float) this.power_multiplier);

                    Box box =  target.getDimensions(target.getPose()).getBoxAt(target.getPos());
                    box.expand(1);
                    Stream<BlockPos> stream_pos = BlockPos.stream(box);
                    stream_pos.forEach( (pos) -> {
                        caster.getWorld().setBlockState(pos, LightBlocks.CLEAR_ICE.getDefaultState());
                    });
                }
            }

        }

    }
}
