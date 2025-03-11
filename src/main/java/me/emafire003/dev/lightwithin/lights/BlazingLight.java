package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.compat.coloredglowlib.CGLCompat;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.BalanceConfig;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.CheckUtils;
import me.emafire003.dev.lightwithin.util.TargetType;
import me.emafire003.dev.structureplacerapi.StructurePlacerAPI;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.LIGHT_COMPONENT;

public class BlazingLight extends InnerLight {

    public static final Item INGREDIENT = Items.FIRE_CHARGE;

    public BlazingLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, String color, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.BLAZING;
    }

    public BlazingLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.BLAZING;
        color = "ea4610";
    }

    public BlazingLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.BLAZING;
        //color = "#ea4610";
        color = "blazing";
    }

    private double crit_multiplier = 1.5;
    private double r = 0.5;

    public static final TagKey<Block> BLAZING_TRIGGER_BLOCKS = TagKey.of(RegistryKeys.BLOCK, LightWithin.getIdentifier("blazing_trigger_blocks"));
    public static final TagKey<Item> BLAZING_TRIGGER_ITEMS = TagKey.of(RegistryKeys.ITEM, LightWithin.getIdentifier("blazing_trigger_items"));

    private void checkSafety(){
        if(this.power_multiplier > BalanceConfig.BLAZING_MAX_POWER){
            power_multiplier = BalanceConfig.BLAZING_MAX_POWER;
        }
        if(this.power_multiplier < BalanceConfig.BLAZING_MIN_POWER){
            power_multiplier = BalanceConfig.BLAZING_MIN_POWER;
        }
        int max_duration = BalanceConfig.BLAZING_MAX_DURATION;
        if(Config.MULTIPLY_DURATION_LIMIT){
            max_duration = (int) (BalanceConfig.BLAZING_MAX_DURATION * Config.DURATION_MULTIPLIER);
        }
        if(this.duration > max_duration){
            this.duration = max_duration;
        }
        if(this.duration < BalanceConfig.BLAZING_MIN_DURATION){
            this.duration = BalanceConfig.BLAZING_MIN_DURATION;
        }
        if(BalanceConfig.BLAZING_CRIT_MULTIPLIER > 1){
            crit_multiplier = BalanceConfig.BLAZING_CRIT_MULTIPLIER;
        }
    }

    @Override
    public void execute(){
        checkSafety();
        LightComponent component = LIGHT_COMPONENT.get(caster);

        String blazing_structure_id = "blazing_light";
        String fire_ring_id = "fire_ring";
        ParticleEffect flame_particle = ParticleTypes.FLAME;

        if(component.getTargets().equals(TargetType.VARIANT)){
            flame_particle = ParticleTypes.SOUL_FIRE_FLAME;
            fire_ring_id = "soulfire_ring";
            blazing_structure_id = "blazing_light_soul";

            this.color = "blazing_variant";
        }

        if(FabricLoader.getInstance().isModLoaded("coloredglowlib")){
            if(this.rainbow_col){
                CGLCompat.getLib().setRainbowColor(this.caster);
            }else{
                CGLCompat.getLib().setColor(this.caster, this.color);
            }
        }

        caster.getWorld().playSound(null, BlockPos.ofFloored(caster.getPos()), LightSounds.BLAZING_LIGHT, SoundCategory.PLAYERS, 1f, 1f);
        caster.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, caster.getStatusEffect(LightEffects. LIGHT_ACTIVE).getDuration(), 0, false, false));


        if(component.getTargets().equals(TargetType.ALL)){
            power_multiplier = power_multiplier + BalanceConfig.BLAZING_ALL_DAMAGE_BONUS;
        }
        if(!caster.getWorld().isClient && (CheckUtils.checkGriefable((ServerPlayerEntity) caster) || Config.NON_FUNDAMENTAL_STRUCTURE_GRIEFING)) {
            StructurePlacerAPI placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), LightWithin.getIdentifier(blazing_structure_id), caster.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, true, 1.0f, new BlockPos(-3, -4, -3));
            if(Config.REPLACEABLE_STRUCTURES){
                placer.loadAndRestoreStructureAnimated(caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), 2, true);
            }else{
                placer.loadStructure();
            }
        }

        if(!caster.getWorld().isClient) {
            LightParticlesUtil.spawnLightTypeParticle(LightParticles.BLAZINGLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());
        }
        for(LivingEntity target : this.targets){
            //target.playSound(LightSounds.BLAZING_LIGHT, 1, 1);

            if(!caster.getWorld().isClient){
                LightParticlesUtil.spawnLightTypeParticle(LightParticles.BLAZINGLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), target.getPos());
            }
            
            //TODO make the chance configable EDIT: Maybe not
            //it's a crit, unique for now to the blazing light Currently 10 percent
            if(caster.getRandom().nextInt(10) == 1){
                target.damage(caster.getWorld().getDamageSources().inFire(), (float) (BalanceConfig.BLAZING_DEFAULT_DAMAGE*this.power_multiplier*crit_multiplier));
                target.setOnFireFor(this.duration*BalanceConfig.BLAZING_CRIT_FIRE_MULTIPLIER);
                caster.getWorld().playSound(null, BlockPos.ofFloored(target.getPos()), LightSounds.LIGHT_CRIT, SoundCategory.PLAYERS, 1, 1f);
                LightParticlesUtil.spawnDescendingColumn((ServerPlayerEntity) caster, flame_particle, target.getPos().add(0,3,0));
                if(!caster.getWorld().isClient){
                    StructurePlacerAPI placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), LightWithin.getIdentifier(fire_ring_id), caster.getBlockPos());
                    placer.loadStructure();
                }
            }else{
                target.setOnFireFor(this.duration);
                target.damage(caster.getWorld().getDamageSources().inFire(), (float) (BalanceConfig.BLAZING_DEFAULT_DAMAGE*this.power_multiplier));
            }
        }

        //to spawn the expanding circle of particles
        ParticleEffect finalFlame_particle = flame_particle;
        ServerTickEvents.END_SERVER_TICK.register((server -> {
            if(r < LightWithin.getBoxExpansionAmount()){
                r = r + 0.5;
                LightParticlesUtil.spawnCircle(caster.getPos().add(0,0.7,0), r, 100, finalFlame_particle, (ServerWorld) caster.getWorld());
            }
        }));

    }
}
