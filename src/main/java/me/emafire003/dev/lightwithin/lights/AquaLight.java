package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.lightwithin.LightWithin;
import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.compat.coloredglowlib.CGLCompat;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.BalanceConfig;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.config.TriggerConfig;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.CheckUtils;
import me.emafire003.dev.lightwithin.util.SpawnUtils;
import me.emafire003.dev.lightwithin.util.TargetType;
import me.emafire003.dev.lightwithin.util.TriggerChecks;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static me.emafire003.dev.lightwithin.LightWithin.*;
import static me.emafire003.dev.lightwithin.util.CheckUtils.checkWaterLoggedOrTag;
import static me.emafire003.dev.lightwithin.util.LightTriggerChecks.getMinTrigger;
import static me.emafire003.dev.lightwithin.util.LightTriggerChecks.sendLightTriggered;

public class AquaLight extends InnerLight {

    public static final Item INGREDIENT = Items.SEAGRASS;

    public static final TagKey<Block> AQUA_TRIGGER_BLOCKS = TagKey.of(RegistryKeys.BLOCK, LightWithin.getIdentifier("aqua_trigger_blocks"));
    public static final TagKey<Item> AQUA_TRIGGER_ITEMS = TagKey.of(RegistryKeys.ITEM, LightWithin.getIdentifier("aqua_trigger_items"));

    private final List<TargetType> possibleTargetTypes = Arrays.asList(TargetType.SELF, TargetType.ENEMIES, TargetType.ALLIES,  TargetType.ALL);
    private final List<TriggerChecks> triggerChecks = List.of(TriggerChecks.ENTITY_ATTACKS_ENTITY, TriggerChecks.ALLY_ATTACKED, TriggerChecks.ALLY_DIES, TriggerChecks.ENTITY_DROWNING);
    private final Identifier lightId = LightWithin.getIdentifier("aqua");

    /**
     * Creates an instance of this InnerLight. Remember to register it!
     *
     * @param regex A lambda function, which provides you with a string representing the UUID portion dedicated to type determination.
     *              You have to provide a check based on the string for which the player will have that particular light.
     *              Remember that the order with which the lights are registered matters a lot!
     */
    public AquaLight(TypeCreationRegex regex) {
        super(regex);
        this.color = "aqua";
    }

    @Override
    public List<TargetType> getPossibleTargetTypes() {
        return possibleTargetTypes;
    }

    @Override
    public List<TriggerChecks> getTriggerChecks() {
        return triggerChecks;
    }

    @Override
    public Item getIngredient() {
        return INGREDIENT;
    }

    @Override
    public Identifier getLightId() {
        return lightId;
    }


    @Override
    protected Pair<Double, Integer>  checkSafety(double power_multiplier, int duration){
        if(power_multiplier > BalanceConfig.AQUA_MAX_POWER){
            power_multiplier = BalanceConfig.AQUA_MAX_POWER;
        }
        if(power_multiplier < BalanceConfig.AQUA_MIN_POWER){
            power_multiplier = BalanceConfig.AQUA_MIN_POWER;
        }
        int max_duration = BalanceConfig.AQUA_MAX_DURATION;
        if(Config.MULTIPLY_DURATION_LIMIT){
            max_duration = (int) (BalanceConfig.AQUA_MAX_DURATION * Config.DURATION_MULTIPLIER);
        }
        if(duration > max_duration){
            duration = max_duration;
        }
        if(duration < BalanceConfig.AQUA_MIN_DURATION){
            duration = BalanceConfig.AQUA_MIN_DURATION;
        }
        return new Pair<Double, Integer>(power_multiplier, duration);
    }

    @Override
    public void startActivation(LightComponent component, PlayerEntity player) {
        List<LivingEntity> targets = new ArrayList<>();
        if(component.getTargets().equals(TargetType.ALL)){
            targets.addAll(player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(getBoxExpansionAmount()), (entity1 -> true)));
            targets.remove(player);
            player.sendMessage(Text.translatable("light.description.activation.aqua.all"), true);
        }

        else if(component.getTargets().equals(TargetType.ENEMIES)){
            targets.addAll(getEnemies(player));
            player.sendMessage(Text.translatable("light.description.activation.aqua.enemies"), true);
        }else if(component.getTargets().equals(TargetType.ALLIES)){
            targets.addAll(getAllies(player));
            player.sendMessage(Text.translatable("light.description.activation.aqua.allies"), true);
        }if(component.getTargets().equals(TargetType.SELF)){
            targets.add(player);
            player.sendMessage(Text.translatable("light.description.activation.aqua.self"), true);
        }
        activate(player, targets, component.getPowerMultiplier(), component.getDuration(), component.getMaxCooldown());
    }

    @Override
    public void activate(PlayerEntity caster, List<LivingEntity> targets, double power_multiplier, int duration, double cooldown_time){
        power_multiplier = checkSafety(power_multiplier, duration).getFirst();
        duration = checkSafety(power_multiplier, duration).getSecond();

        if(FabricLoader.getInstance().isModLoaded("coloredglowlib")){
            CGLCompat.getLib().setColor(caster, color);
        }


        caster.getWorld().playSound(null, BlockPos.ofFloored(caster.getPos()), LightSounds.TYPES_SOUNDS.get(lightId), SoundCategory.PLAYERS, 1f, 1f);
        LightComponent component = LIGHT_COMPONENT.get(caster);

        //ALL section (drowneds)
        if(component.getTargets().equals(TargetType.ALL)){
            LightParticlesUtil.spawnLightTypeParticle(LightParticles.TYPES_PARTICLES.get(lightId), (ServerWorld) caster.getWorld(), caster.getPos());

            for(int i = 0; i<power_multiplier; i++){
                DrownedEntity drowned = new DrownedEntity(EntityType.DROWNED, caster.getWorld());


                ItemStack iron_chest = new ItemStack(Items.CHAINMAIL_CHESTPLATE);
                //iron_chest.addEnchantment(Enchantments.PROTECTION, caster.getRandom().nextBetween(1, 3));

                Optional<RegistryEntry.Reference<Enchantment>> protection = caster.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.PROTECTION);

                if(protection.isPresent()){
                    iron_chest.addEnchantment(protection.get(), caster.getRandom().nextBetween(1, 3));
                }else{
                    //Ah this is a message to any modder who removed the protection enchant from the enchants list, not a "true" debug message.
                    LOGGER.debug("Who the heck removed protection from the enchants list? (The drowneds spawned with AquaLight won't be equipped with it, but it's not a critical issue");
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
                if(power_multiplier > 5){
                    trident.addEnchantment(Enchantments.CHANNELING, 1);
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

                if(power_multiplier > 5 && caster.getRandom().nextBetween(1, 100) == 1){
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
            for(LivingEntity target : targets){
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

                LightParticlesUtil.spawnLightTypeParticle(LightParticles.TYPES_PARTICLES.get(lightId), (ServerWorld) target.getWorld(), target.getPos());
            }
            //Depending on the level it will spawn a small moat and pillar around the user, a big pillar only and a big pillar with a big moat.
            //And will also give Solid Rock effect to self, making the player more resistant to knokback
        }

        //enemies section (water cage & tridents)
        else if(component.getTargets().equals(TargetType.ENEMIES)) {
            LightParticlesUtil.spawnLightTypeParticle(LightParticles.TYPES_PARTICLES.get(lightId), (ServerWorld) caster.getWorld(), caster.getPos());

            for(LivingEntity target : targets){
                //TODO should I play the sound for every enemy? Nah
                //target.playSound(LightSounds.TYPES_SOUNDS.get(lightId), 0.9f, 1);

                if(!caster.getWorld().isClient && CheckUtils.checkGriefable((ServerPlayerEntity) caster)) {
                    if(target instanceof PlayerEntity){
                        applyWaterCascade(target, (int) (power_multiplier*3));
                    }else{
                        target.addStatusEffect(new StatusEffectInstance(LightEffects.WATER_CASCADE, (int) (power_multiplier*3), 0, false, false));
                    }
                    if(power_multiplier >= 5){
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
                        if(power_multiplier >= 8){
                            LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, caster.getWorld());
                            lightning.setPos(target.getX(), target.getY(), target.getZ());
                            target.getWorld().spawnEntity(lightning);
                        }
                        tridentEntity.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
                        caster.getWorld().playSound(null, BlockPos.ofFloored(target.getPos()), SoundEvents.ITEM_TRIDENT_RETURN, SoundCategory.PLAYERS, 1, 0.7f);
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

    @Override
    public void triggerCheck(PlayerEntity player, LightComponent component, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        double trigger_sum = 0;
        /**If the player has ALL as target, he needs to be hurt (or an ally has to die, but that depends on the trigger)*/
        if(component.getTargets().equals(TargetType.ALL)){
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_ALL_VERY_LOW_HEALTH;
            }

            if(Config.CHECK_SURROUNDED && (CheckUtils.checkSurrounded(player))){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_ALL_SURROUNDED;
            }

            //Checks if the player'sallies have low armor durability
            if(!player.equals(target) && Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkAllyArmor(player, target, Config.DUR_PERCENTAGE_ALLIES)){
                trigger_sum=trigger_sum+TriggerConfig.AQUA_ALL_ALLY_ARMOR_DURABILITY;
            }

            if(player.getAir() == 0){
                trigger_sum=trigger_sum+TriggerConfig.AQUA_ALL_DROWNING;
            }

            if(checkLightConditions(player)){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_ALL_CONDITIONS;
            }

            if(trigger_sum >= getMinTrigger()) {
                sendLightTriggered((ServerPlayerEntity) player);
            }
        }
        /**CHECKS if the player has ENEMIES as target, either his or his allies health needs to be low*/
        else if(component.getTargets().equals(TargetType.ENEMIES)){
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_ENEMIES_VERY_LOW_HEALTH;
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+Config.HP_PERCENTAGE_INCREMENT)){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_ENEMIES_LOW_HEALTH;
            }

            if(Config.CHECK_SURROUNDED && (CheckUtils.checkSurrounded(player))){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_ENEMIES_SURROUNDED;
            }
            if(player.getAir() == 0){
                trigger_sum=trigger_sum+TriggerConfig.AQUA_ENEMIES_DROWNING;
            }

            //Checks if the player'sallies have low armor durability
            if(!player.equals(target) && Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkAllyArmor(player, target, Config.DUR_PERCENTAGE_ALLIES)){
                trigger_sum=trigger_sum+TriggerConfig.AQUA_ENEMIES_ALLY_ARMOR_DURABILITY;
            }

            if(checkLightConditions(player)){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_ENEMIES_CONDITIONS;
            }

            if(trigger_sum >= getMinTrigger()) {
                sendLightTriggered((ServerPlayerEntity) player);
            }
        }else if(component.getTargets().equals(TargetType.SELF)){
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_SELF_VERY_LOW_HEALTH;
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+Config.HP_PERCENTAGE_INCREMENT)){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_SELF_LOW_HEALTH;
            }

            if(Config.CHECK_SURROUNDED && (CheckUtils.checkSurrounded(player))){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_SELF_SURROUNDED;
            }
            if(player.getAir() == 0){
                trigger_sum=trigger_sum+TriggerConfig.AQUA_SELF_DROWNING;
            }

            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)){
                trigger_sum=trigger_sum+TriggerConfig.AQUA_SELF_ARMOR_DURABILITY;
            }

            if(checkLightConditions(player)){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_SELF_CONDITIONS;
            }

            if(trigger_sum >= getMinTrigger()) {
                sendLightTriggered((ServerPlayerEntity) player);
            }

        }else if(component.getTargets().equals(TargetType.ALLIES)){
            if(!player.equals(target) && CheckUtils.checkAllyHealth(player, attacker, Config.HP_PERCENTAGE_ALLIES+5)){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_ALLIES_ALLY_LOW_HEALTH;
            }
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_ALLIES_VERY_LOW_HEALTH;
            }

            //Checks if the player'sallies have low armor durability
            if(!player.equals(target) && Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkAllyArmor(player, target, Config.DUR_PERCENTAGE_ALLIES)){
                trigger_sum=trigger_sum+TriggerConfig.AQUA_ALLIES_ALLY_ARMOR_DURABILITY;
            }
            if(player.getAir() == 0){
                trigger_sum=trigger_sum+TriggerConfig.AQUA_ALLIES_DROWNING;
            }
            if(CheckUtils.CheckAllies.checkAlly(player, target) && target.getAir() == 0){
                trigger_sum=trigger_sum+TriggerConfig.AQUA_ALLIES_ALLY_DROWNING;
            }

            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+TriggerConfig.AQUA_ALLIES_SURROUNDED;
            }

            if(checkLightConditions(player)){
                trigger_sum=trigger_sum+TriggerConfig.AQUA_ALLIES_CONDITIONS;
            }

            if(trigger_sum >= getMinTrigger()) {
                sendLightTriggered((ServerPlayerEntity) player);
            }
        }
    }

    /**Used to check if the player has something that can be considered a Aqua source
     *
     * @param player The player to perform checks on*/
    @Override
    public boolean checkLightConditions(PlayerEntity player) {
        if(player.isTouchingWaterOrRain()){
            return true;
        }

        ItemStack main = player.getMainHandStack();
        ItemStack off = player.getOffHandStack();
        if(main.isIn(AquaLight.AQUA_TRIGGER_ITEMS) || off.isIn(AquaLight.AQUA_TRIGGER_ITEMS)){
            return true;
        }
        return checkWaterLoggedOrTag(player, Config.TRIGGER_BLOCK_RADIUS, AquaLight.AQUA_TRIGGER_BLOCKS);
    }

    @Override
    public String toString() {
        return this.lightId.getPath();
    }
}
