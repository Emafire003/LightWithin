package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.lightwithin.compat.coloredglowlib.CGLCompat;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.BalanceConfig;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.CheckUtils;
import me.emafire003.dev.lightwithin.util.SpawnUtils;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static me.emafire003.dev.lightwithin.LightWithin.*;

public class AquaLight extends InnerLight {

    public static final Item INGREDIENT = Items.SEAGRASS;

    public AquaLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, String color, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.AQUA;
    }

    public AquaLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.AQUA;
        color = "35f4d1";
    }

    public AquaLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.AQUA;
        //color = "#35f4d1";
        color = "aqua";
    }

    private void checkSafety(){
        if(this.power_multiplier > BalanceConfig.AQUA_MAX_POWER){
            power_multiplier = BalanceConfig.AQUA_MAX_POWER;
        }
        if(this.power_multiplier < BalanceConfig.AQUA_MIN_POWER){
            power_multiplier = BalanceConfig.AQUA_MIN_POWER;
        }
        int max_duration = BalanceConfig.AQUA_MAX_DURATION;
        if(Config.MULTIPLY_DURATION_LIMIT){
            max_duration = (int) (BalanceConfig.AQUA_MAX_DURATION * Config.DURATION_MULTIPLIER);
        }
        if(this.duration > max_duration){
            this.duration = max_duration;
        }
        if(this.duration < BalanceConfig.AQUA_MIN_DURATION){
            this.duration = BalanceConfig.AQUA_MIN_DURATION;
        }
    }

    @Override
    public void execute(){
        checkSafety();
        if(FabricLoader.getInstance().isModLoaded("coloredglowlib")){
            if(this.rainbow_col){
                CGLCompat.getLib().setRainbowColor(this.caster);
            }else{
                CGLCompat.getLib().setColor(this.caster, this.color);
            }
        }


        caster.getWorld().playSound(caster, caster.getBlockPos(), LightSounds.AQUA_LIGHT, SoundCategory.PLAYERS, 1, 1);
        LightComponent component = LIGHT_COMPONENT.get(caster);

        //ALL section (drowneds)
        if(component.getTargets().equals(TargetType.ALL)){
            LightParticlesUtil.spawnLightTypeParticle(LightParticles.AQUALIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());

            for(int i = 0; i<power_multiplier; i++){
                DrownedEntity drowned = new DrownedEntity(EntityType.DROWNED, caster.getWorld());
                
                
                ItemStack iron_chest = new ItemStack(Items.CHAINMAIL_CHESTPLATE);
                //iron_chest.addEnchantment(Enchantments.PROTECTION, caster.getRandom().nextBetween(1, 3));

                Optional<RegistryEntry.Reference<Enchantment>> protection = caster.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.PROTECTION);

                if(protection.isPresent()){
                    iron_chest.addEnchantment(protection.get(), caster.getRandom().nextBetween(1, 3));
                }else{
                    LOGGER.info("Who the heck removed protection from the enchants list? (The drowneds spawned with AquaLight won't be equipped with it, but it's not a critical issue");
                }

                ItemStack turtle_helmet = new ItemStack(Items.TURTLE_HELMET);
                /*turtle_helmet.addEnchantment(Enchantments.BINDING_CURSE, 1);
                turtle_helmet.addEnchantment(Enchantments.PROJECTILE_PROTECTION, 3);*/
                Optional<RegistryEntry.Reference<Enchantment>> binding = caster.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.BINDING_CURSE);

                if(binding.isPresent()){
                    turtle_helmet.addEnchantment(binding.get(), 1);
                }else{
                    LOGGER.info("Who the heck removed curse of binding? from the enchants list? (The drowneds spawned with AquaLight won't be equipped with it, but it's not a critical issue");
                }

                Optional<RegistryEntry.Reference<Enchantment>> projectile_prot = caster.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.PROJECTILE_PROTECTION);

                if(projectile_prot.isPresent()){
                    turtle_helmet.addEnchantment(projectile_prot.get(), 1);
                }else{
                    LOGGER.info("Who the heck removed projectile_protection? from the enchants list? (The drowneds spawned with AquaLight won't be equipped with it, but it's not a critical issue");
                }

                ItemStack trident = new ItemStack(Items.TRIDENT);
                if(this.power_multiplier > 5){
                    /*trident.addEnchantment(Enchantments.CHANNELING, 1);
                    turtle_helmet.addEnchantment(Enchantments.THORNS, caster.getRandom().nextBetween(1, 2));
                    iron_chest.addEnchantment(Enchantments.THORNS, caster.getRandom().nextBetween(1, 2));*/
                    Optional<RegistryEntry.Reference<Enchantment>> channeling = caster.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.CHANNELING);
                    if(channeling.isPresent()){
                        trident.addEnchantment(channeling.get(), 1);
                    }else{
                        LOGGER.info("Who the heck removed channeling from the enchants list? (The drowneds spawned with AquaLight won't be equipped with it, but it's not a critical issue");
                    }

                    Optional<RegistryEntry.Reference<Enchantment>> thorns = caster.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.THORNS);
                    if(thorns.isPresent()){
                        turtle_helmet.addEnchantment(thorns.get(), caster.getRandom().nextBetween(1, 2));
                        iron_chest.addEnchantment(thorns.get(), caster.getRandom().nextBetween(1, 2));
                    }else{
                        LOGGER.info("Who the heck removed thorns from the enchants list? (The drowneds spawned with AquaLight won't be equipped with it, but it's not a critical issue");
                    }
                    
                }
                //trident.addEnchantment(Enchantments.IMPALING, caster.getRandom().nextBetween(1,3));
                Optional<RegistryEntry.Reference<Enchantment>> impaling = caster.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.IMPALING);
                if(impaling.isPresent()){
                    trident.addEnchantment(impaling.get(), caster.getRandom().nextBetween(1,3));
                }else{
                    LOGGER.info("Who the heck removed impaling from the enchants list? (The drowneds spawned with AquaLight won't be equipped with it, but it's not a critical issue");
                }

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
                SUMMONED_BY_COMPONENT.get(drowned).setSummonerUUID(caster.getUuid());
                SUMMONED_BY_COMPONENT.get(drowned).setIsSummoned(true);

                boolean b = SpawnUtils.spawnAround(caster, 1, 5, drowned, (ServerWorld) caster.getWorld(), SpawnLocationTypes.UNRESTRICTED);
                LightParticlesUtil.spawnLightTypeParticle(LightParticles.AQUALIGHT_PARTICLE, (ServerWorld) drowned.getWorld(), drowned.getPos());
            }

        }

        //Allies/self section (boosts & water slide)
        if(component.getTargets().equals(TargetType.ALLIES) || component.getTargets().equals(TargetType.SELF)){
            for(LivingEntity target : this.targets){
                //target.playSound(LightSounds.AQUA_LIGHT, 0.9f, 1);
                if(this.power_multiplier < 4){
                    target.addStatusEffect(new StatusEffectInstance(LightEffects.WATER_SLIDE, this.duration*20, 2, false, false));
                }else{
                    target.addStatusEffect(new StatusEffectInstance(LightEffects.WATER_SLIDE, this.duration*20, 3, false, false));
                }
                if(target.equals(caster) && component.getTargets().equals(TargetType.ALLIES)){
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, this.duration*20, (int) (this.power_multiplier/Config.DIV_SELF), false, false));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, this.duration*20, (int) (this.power_multiplier/Config.DIV_SELF), false, false));
                }else{
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, this.duration*20, (int) (this.power_multiplier), false, false));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, this.duration*20, (int) (this.power_multiplier), false, false));
                }

                LightParticlesUtil.spawnLightTypeParticle(LightParticles.AQUALIGHT_PARTICLE, (ServerWorld) target.getWorld(), target.getPos());
            }
            //Depending on the level it will spawn a small moat and pillar around the user, a big pillar only and a big pillar with a big moat.
            //And will also give Solid Rock effect to self, making the player more resistant to knokback
        }

        //enemies section (water cage & tridents)
        else if(component.getTargets().equals(TargetType.ENEMIES)) {
            LightParticlesUtil.spawnLightTypeParticle(LightParticles.AQUALIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());

            for(LivingEntity target : this.targets){
                //TODO should I play the sound for every enemy? Nah
                //target.playSound(LightSounds.AQUA_LIGHT, 0.9f, 1);

                if(!caster.getWorld().isClient && CheckUtils.checkGriefable((ServerPlayerEntity) caster)) {
                    if(target instanceof PlayerEntity){
                        applyWaterCascade(target, (int) (this.power_multiplier*3));
                    }else{
                        target.addStatusEffect(new StatusEffectInstance(LightEffects.WATER_CASCADE, (int) (this.power_multiplier*3), 0, false, false));
                    }
                    if(this.power_multiplier >= 5){
                        ItemStack trident = new ItemStack(Items.TRIDENT);

                        //<1.21 code
                        //trident.addEnchantment(Enchantments.CHANNELING, 1);
                        Optional<RegistryEntry.Reference<Enchantment>> channeling = caster.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.CHANNELING);

                        if(channeling.isPresent()){
                            trident.addEnchantment(channeling.get(), 1);
                        }else{
                            LOGGER.info("Who the heck removed channeling from the enchants list? (The drowneds spawned with AquaLight won't be equipped with it, but it's not a critical issue");
                        }


                        TridentEntity tridentEntity = new TridentEntity(caster.getWorld(), caster, trident);
                        tridentEntity.setPos(target.getX(), target.getY()+10, target.getZ());
                        tridentEntity.addVelocity(0, -1, 0);
                        if(this.power_multiplier >= 8){
                            LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, caster.getWorld());
                            lightning.setPos(target.getX(), target.getY(), target.getZ());
                            target.getWorld().spawnEntity(lightning);
                        }
                        tridentEntity.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
                        target.playSound(SoundEvents.ITEM_TRIDENT_RETURN, 1, 0.7f);
                        target.getWorld().spawnEntity(tridentEntity);
                    }
                }
            }
        }

    }

    HashMap<BlockPos, BlockState> block_map = new HashMap<>();

    BlockPos start_pos;
    int tickCounter = 0;
    int blockCounter = 0;

    public void applyWaterCascade(LivingEntity entity, int blocks){
        blockCounter = blocks;
        ServerTickEvents.END_SERVER_TICK.register((server -> {
            if(tickCounter > blocks || tickCounter == -1){
                if(tickCounter == -1){
                    return;
                }
                tickCounter = -1;
                block_map.forEach(((blockPos, blockState) -> {
                    if(blockPos.equals(start_pos)){
                        entity.getWorld().setBlockState(start_pos, Blocks.SPONGE.getDefaultState());
                    }
                    entity.getWorld().setBlockState(blockPos, blockState);
                }));
                block_map.clear();
                return;
            }
            tickCounter++;
            //Skips a few ticks so it goes a little slower and is cooler. Hopefully
            if(tickCounter%4 == 0){
                blockCounter++;
                return;
            }
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
                entity.teleport(posc.getX(), posc.getY()+1, posc.getZ(), false);
            }
        }));
    }

}
