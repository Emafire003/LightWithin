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
import net.minecraft.enchantment.ChannelingEnchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.*;
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

                target.addStatusEffect(new StatusEffectInstance(LightEffects.WATER_SLIDE, this.duration*20, 0, false, false));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, this.duration*20, (int) (this.power_multiplier), false, false));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, this.duration*20, (int) (this.power_multiplier), false, false));

                LightParticlesUtil.spawnLightTypeParticle(LightParticles.AQUALIGHT_PARTICLE, (ServerWorld) target.getWorld(), target.getPos());
            }
            //Depending on the level it will spawn a small moat and pillar around the user, a big pillar only and a big pillar with a big moat.
            //And will also give Solid Rock effect to self, making the player more resistant to knokback
        }

        //enemies section (water cage & tridents)
        else if(component.getTargets().equals(TargetType.ENEMIES)) {
            LightParticlesUtil.spawnLightTypeParticle(LightParticles.AQUALIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());

            for(LivingEntity target : this.targets){
                //TODO change to aqua
                target.playSound(LightSounds.EARTHEN_LIGHT, 0.9f, 1);
                if(Config.STRUCTURE_GRIEFING && !caster.getWorld().isClient) {
                    StructurePlacerAPI placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), new Identifier(MOD_ID, "water_cage"), target.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, true, 1f, new BlockPos(-1, 0, -1));
                    placer.loadStructure();

                    target.setAir(2);
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, this.duration*10, (int) (this.power_multiplier*20), false, false));

                    if(this.power_multiplier >= 4){
                        ItemStack trident = new ItemStack(Items.TRIDENT);
                        trident.addEnchantment(Enchantments.CHANNELING, 1);
                        TridentEntity tridentEntity = new TridentEntity(caster.world, caster, trident);
                        tridentEntity.setPos(target.getX(), target.getY()+10, target.getZ());
                        tridentEntity.addVelocity(0, -1, 0);
                        if(this.power_multiplier >= 7){
                            LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, caster.getWorld());
                            lightning.setPos(target.getX(), target.getY(), target.getZ());
                            target.getWorld().spawnEntity(lightning);
                        }
                        target.playSound(SoundEvents.ITEM_TRIDENT_RETURN, 1, 0.7f);
                        target.getWorld().spawnEntity(tridentEntity);
                    }
                }
            }
        }

    }

}
