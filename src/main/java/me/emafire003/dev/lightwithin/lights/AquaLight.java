package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.coloredglowlib.ColoredGlowLib;
import me.emafire003.dev.coloredglowlib.util.Color;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.FreezeResistanceEffect;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.TargetType;
import me.emafire003.dev.lightwithin.util.fabridash.FabriDash;
import me.emafire003.dev.structureplacerapi.StructurePlacerAPI;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TridentItem;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.command.ScheduleCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.LIGHT_COMPONENT;
import static me.emafire003.dev.lightwithin.LightWithin.MOD_ID;

public class AquaLight extends InnerLight {

    /*Possible triggers:
       - self low health
       - allies low health
       - surrounded+++
       - NEEDS to have in hand dirt/rock or be around them.
     */

    /*Possible targets:
    Always apply conduit effect
    * - self, ->
    * - ally/self -> spawn water under the player's feet, and give dolphin grace
    * - enemies -> encase them in a water bubble and apply veeeery high slowness to them + damage. see below
    * - enemies v2 -> throw a lot of tridents to them, putting their velocity to collide with the immobile player
    * - ALL -> summon armored drowneds that don't attack the summoner (mixin) (this could also be self)
    * (if not self maybe the player could be not attacked if they have the conduit effect. Or dolphin grace.
    * - ALL MAYBE, but not sure. -> */

    public AquaLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, Color color, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.AQUA;
    }

    public AquaLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.AQUA;
        color = new Color(53, 246, 211);
    }

    public AquaLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.AQUA;
        color = new Color(53, 246, 211);
    }

    private void checkSafety(){
        if(this.power_multiplier > Config.WIND_MAX_POWER){
            power_multiplier = Config.WIND_MAX_POWER;
        }
        if(this.power_multiplier < Config.WIND_MIN_POWER){
            power_multiplier = Config.WIND_MIN_POWER;
        }
        if(this.duration > Config.WIND_MAX_DURATION){
            this.duration = Config.WIND_MAX_DURATION;
        }
        if(this.duration < Config.WIND_MIN_DURATION){
            this.duration = Config.WIND_MIN_DURATION;
        }
    }

    @Override
    public void execute(){
        checkSafety();
        if(this.rainbow_col){
            ColoredGlowLib.setRainbowColorToEntity(this.caster, true);
        }else{
            ColoredGlowLib.setColorToEntity(this.caster, this.color);
        }

        caster.getWorld().playSound(caster, caster.getBlockPos(), LightSounds.WIND_LIGHT, SoundCategory.AMBIENT, 1, 1);
        LightComponent component = LIGHT_COMPONENT.get(caster);

        //ALL section (drowneds)
        if(component.getTargets().equals(TargetType.ALL)){
            LightParticlesUtil.spawnLightTypeParticle(LightParticles.AQUALIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());

            for(int i = 0; i<power_multiplier; i++){
                DrownedEntity drowned = new DrownedEntity(EntityType.DROWNED, caster.getWorld());

                ItemStack iron_chest = new ItemStack(Items.IRON_CHESTPLATE);
                iron_chest.addEnchantment(Enchantments.PROTECTION, caster.getRandom().nextBetween(1, 3));
                if(this.power_multiplier > 6){
                    iron_chest.addEnchantment(Enchantments.THORNS, caster.getRandom().nextBetween(1, 2));
                }

                ItemStack turtle_helmet = new ItemStack(Items.TURTLE_HELMET);
                turtle_helmet.addEnchantment(Enchantments.BINDING_CURSE, 1);
                turtle_helmet.addEnchantment(Enchantments.PROJECTILE_PROTECTION, 3);
                if(this.power_multiplier > 6){
                    turtle_helmet.addEnchantment(Enchantments.THORNS, caster.getRandom().nextBetween(1, 2));
                }

                ItemStack trident = new ItemStack(Items.TRIDENT);
                if(this.power_multiplier > 5){
                    trident.addEnchantment(Enchantments.CHANNELING, 1);
                }
                trident.addEnchantment(Enchantments.IMPALING, caster.getRandom().nextBetween(1,3));

                drowned.equipStack(EquipmentSlot.CHEST, iron_chest);
                drowned.equipStack(EquipmentSlot.HEAD, turtle_helmet);
                if(caster.getRandom().nextBetween(1, 100) > 12){
                    drowned.equipStack(EquipmentSlot.OFFHAND, trident);
                }else{
                    drowned.equipStack(EquipmentSlot.MAINHAND, trident);
                }

                if(this.power_multiplier > 5 && caster.getRandom().nextBetween(1, 100) == 1){
                    drowned.equipStack(EquipmentSlot.OFFHAND, trident);
                    drowned.equipStack(EquipmentSlot.MAINHAND, trident);
                }

                drowned.setPos(caster.getX()+caster.getRandom().nextBetween(-3,3), caster.getY(), caster.getZ()+caster.getRandom().nextBetween(-3,3));
                caster.getWorld().spawnEntity(drowned);
                LightParticlesUtil.spawnLightTypeParticle(LightParticles.AQUALIGHT_PARTICLE, (ServerWorld) drowned.getWorld(), drowned.getPos());
            }

        }

        //Allies/self section (boosts & water slide)
        if(component.getTargets().equals(TargetType.ALLIES) || component.getTargets().equals(TargetType.SELF)){
            for(LivingEntity target : this.targets){
                //TODO change to aqua sound effects
                target.playSound(LightSounds.WIND_LIGHT, 0.9f, 1);

                target.addStatusEffect(new StatusEffectInstance(LightEffects.WATER_SLIDE, this.duration*20, (int) (this.power_multiplier), false, false));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, this.duration*20, (int) (this.power_multiplier), false, false));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, this.duration*20, (int) (this.power_multiplier), false, false));

                LightParticlesUtil.spawnLightTypeParticle(LightParticles.AQUALIGHT_PARTICLE, (ServerWorld) target.getWorld(), target.getPos());
            }
            //Depending on the level it will spawn a small moat and pillar around the user, a big pillar only and a big pillar with a big moat.
            //And will also give Solid Rock effect to self, making the player more resistant to knokback
        }

        //enemies section (water ball & tridents)
        else if(component.getTargets().equals(TargetType.ENEMIES)) {
            LightParticlesUtil.spawnLightTypeParticle(LightParticles.AQUALIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());
            //oldtarget and stuuf prevent generating multiple structures in the same area
            LivingEntity oldtarget = null;
            for(LivingEntity target : this.targets){
                //TODO change to aqua
                target.playSound(LightSounds.EARTHEN_LIGHT, 0.9f, 1);
                if(Config.STRUCTURE_GRIEFING && !caster.getWorld().isClient) {
                    if(oldtarget == null || oldtarget.distanceTo(target) > 3){
                        StructurePlacerAPI placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), new Identifier(MOD_ID, "water_ball"), target.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, true, 1f, new BlockPos(-3, 0, -3));
                        placer.loadStructure();
                        //target.teleport(target.getX(), target.getY()+1, target.getZ());
                    }
                    target.setAir(0);
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, this.duration*20, (int) (this.power_multiplier*10), false, false));
                    if(this.power_multiplier >= 4){
                        if(this.power_multiplier < 5){
                            launchTrident(target, caster);
                        }else if(this.power_multiplier < 6){
                            launchMultipleTridents(target, caster, Arrays.asList(target.getPos().add(4.5, 2, 0), target.getPos().add(-4.5, 2, 0)));
                        }else if(this.power_multiplier < 7){
                            launchMultipleTridents(target, caster, Arrays.asList(
                                    target.getPos().add(0, 0, 4.5),
                                    target.getPos().add(0, 4.5, 0),
                                    target.getPos().add(0, 0, -4.5)));
                        }else if(this.power_multiplier < 8){
                            launchMultipleTridents(target, caster, Arrays.asList(
                                    target.getPos().add(0, 0, 4.5),
                                    target.getPos().add(4.5, 0, 0),
                                    target.getPos().add(-4.5, 0, 0),
                                    target.getPos().add(0, 0, -4.5)));
                        }else if(this.power_multiplier < 9){
                            launchMultipleTridents(target, caster, Arrays.asList(
                                    target.getPos().add(0, 0, 4.5),
                                    target.getPos().add(4.5, 0, 0),
                                    target.getPos().add(0, 4.5, 0),
                                    target.getPos().add(-4.5, 0, 0),
                                    target.getPos().add(0, 0, -4.5)));
                        }else if(this.power_multiplier == 9){
                            launchMultipleTridents(target, caster, Arrays.asList(
                                    target.getPos().add(0, 0, 4.5),
                                    target.getPos().add(4.5, 0, 0),
                                    target.getPos().add(0, 4.5, 0),
                                    target.getPos().add(0, -4.5, 0),
                                    target.getPos().add(-4.5, 0, 0),
                                    target.getPos().add(0, 0, -4.5)));
                        }
                    }
                    oldtarget = target;
                }
            }
        }

    }

    public void launchMultipleTridents(LivingEntity target, PlayerEntity caster, List<Vec3d> positions){
        for(Vec3d pos : positions){
            this.launchTrident(target, caster, pos);
        }
    }

    //TODO make them stay airborn for a bit an THEN shoot.
    private int ticks = 0;
    public void launchTrident(LivingEntity target, PlayerEntity caster) {
        TridentEntity tridentEntity = new TridentEntity(caster.world, caster, new ItemStack(Items.TRIDENT));
        double d = target.getX() - caster.getX();
        double e = target.getBodyY(0.3333333333333333D) - tridentEntity.getY();
        double f = target.getZ() - caster.getZ();
        double g = Math.sqrt(d * d + f * f);
        tridentEntity.setVelocity(d, e + g * 0.20000000298023224D, f, 1.6F, (float)(14 - caster.world.getDifficulty().getId() * 4));
        caster.playSound(SoundEvents.ENTITY_DROWNED_SHOOT, 1.0F, 1.0F / (caster.getRandom().nextFloat() * 0.4F + 0.8F));
        caster.world.spawnEntity(tridentEntity);
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            caster.sendMessage(Text.literal("Tic(helo?) : " + ticks));
            if(ticks == -1){
                caster.sendMessage(Text.literal("Ticks : " + ticks));
                return;
            }
            ticks++;
            if(ticks == 5){
                tridentEntity.setNoGravity(true);
                caster.sendMessage(Text.literal("Ticks : " + ticks));
            }
            if(ticks == 30){
                caster.sendMessage(Text.literal("Ticks : " + ticks));
                tridentEntity.setNoGravity(false);
                ticks = -1;
            }

        });
    }

    //TODO make them stay airborn for a bit an THEN shoot.
    public void launchTrident(LivingEntity target, PlayerEntity caster, Vec3d pos) {
        ItemStack trident = new ItemStack(Items.TRIDENT);
        trident.addEnchantment(Enchantments.CHANNELING, 1);
        TridentEntity tridentEntity = new TridentEntity(caster.world, caster, trident);
        tridentEntity.setPos(pos.x, pos.y, pos.z);
        double d = target.getX() - caster.getX();
        double e = target.getBodyY(0.3333333333333333D) - tridentEntity.getY();
        double f = target.getZ() - caster.getZ();
        double g = Math.sqrt(d * d + f * f);
        tridentEntity.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getPos());
        tridentEntity.setNoGravity(true);
        //tridentEntity.setVelocity(d, e + g * 0.20000000298023224D, f, 1.6F, (float)(14 - caster.world.getDifficulty().getId() * 4));

        caster.playSound(SoundEvents.ENTITY_DROWNED_SHOOT, 1.0F, 1.0F / (caster.getRandom().nextFloat() * 0.4F + 0.8F));
        caster.world.spawnEntity(tridentEntity);
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if(ticks == -1){
                return;
            }
            ticks++;
            if(ticks == 5){
                tridentEntity.setNoGravity(true);
            }
            if(ticks == 30){
                tridentEntity.setNoGravity(false);
                ticks = -1;
            }

        });
    }

    private float changeAngle(float from, float to, float max) {
        float f = MathHelper.wrapDegrees(to - from);
        if (f > max) {
            f = max;
        }

        if (f < -max) {
            f = -max;
        }

        return from + f;
    }

}
