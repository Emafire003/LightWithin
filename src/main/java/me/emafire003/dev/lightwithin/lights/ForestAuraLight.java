package me.emafire003.dev.lightwithin.lights;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.BalanceConfig;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.config.TriggerConfig;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.CheckUtils;
import me.emafire003.dev.lightwithin.util.TargetType;
import me.emafire003.dev.lightwithin.util.TriggerChecks;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static me.emafire003.dev.lightwithin.LightWithin.*;
import static me.emafire003.dev.lightwithin.util.LightTriggerChecks.getMinTrigger;
import static me.emafire003.dev.lightwithin.util.LightTriggerChecks.sendLightTriggered;

public class ForestAuraLight extends InnerLight {

    public static final Item INGREDIENT = Items.OAK_SAPLING;
    private final List<TargetType> possibleTargetTypes = Arrays.asList(TargetType.ALL, TargetType.SELF);
    private final List<TriggerChecks> triggerChecks = List.of(TriggerChecks.ENTITY_ATTACKED, TriggerChecks.ENTITY_ATTACKS_ENTITY, TriggerChecks.ALLY_DIES);
    private final Identifier lightId = LightWithin.getIdentifier("forest_aura");

    public static final TagKey<Block> FOREST_AURA_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier(MOD_ID, "forest_aura_blocks"));

    public static final String COLOR = "1BC131";
    //public static final String ENEMY_COLOR = "560d03";
    //public static final String ALLY_COLOR = "2ee878";

    /**The dealy between each puff spawn*/
    private static final int PUFF_DELAY = 7;

    //Up to level 5
    public static List<Integer> LOW_TIER_COLOR_PUFFS = List.of(
            ForestPuffColor.GREEN, ForestPuffColor.YELLOW, ForestPuffColor.PURPLE, ForestPuffColor.PINK);

    //Available after power level 5
    public static List<Integer> HIGH_TIER_COLOR_PUFFS = List.of(
            ForestPuffColor.BLUE, ForestPuffColor.ORANGE, ForestPuffColor.BLACK, ForestPuffColor.RED);


    /**Max tries to spawn a single puff*/
    private static final int max_tries = 10000;

    /**
     * Creates an instance of this InnerLight. Remember to register it!
     *
     * @param regex A lambda function, which provides you with a string representing the UUID portion dedicated to type determination.
     *              You have to provide a check based on the string for which the player will have that particular light.
     *              Remember that the order with which the lights are registered matters a lot!
     */
    public ForestAuraLight(TypeCreationRegex regex) {
        super(regex);
        this.color = "forest_aura";
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
    protected Pair<Double, Integer> checkSafety(double power_multiplier, int duration) {
        if(power_multiplier > BalanceConfig.FOREST_AURA_MAX_POWER){
            power_multiplier = BalanceConfig.FOREST_AURA_MAX_POWER;
        }
        if(power_multiplier < BalanceConfig.FOREST_AURA_MIN_POWER){
            power_multiplier = BalanceConfig.FOREST_AURA_MIN_POWER;
        }
        int max_duration = BalanceConfig.FOREST_AURA_MAX_DURATION;
        if(Config.MULTIPLY_DURATION_LIMIT){
            max_duration = (int) (BalanceConfig.FOREST_AURA_MAX_DURATION * Config.DURATION_MULTIPLIER);
        }
        if(duration > max_duration){
            duration = max_duration;
        }
        if(duration < BalanceConfig.FOREST_AURA_MIN_DURATION){
            duration = BalanceConfig.FOREST_AURA_MIN_DURATION;
        }
        return new Pair<>(power_multiplier, duration);
    }

    @Override
    public void startActivation(LightComponent component, PlayerEntity player) {
        List<LivingEntity> targets = new ArrayList<>();

        if(component.getTargets().equals(TargetType.ALL)){
            targets.addAll(player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(getBoxExpansionAmount()), (entity1 -> true)));
            targets.remove(player);
            player.sendMessage(Text.translatable("light.description.activation.forest_aura.all"), true);
        }else if(component.getTargets().equals(TargetType.SELF)){
            targets.add(player);
            player.sendMessage(Text.translatable("light.description.activation.forest_aura.self"), true);
        }
        activate(player, targets, component.getPowerMultiplier(), component.getDuration(), component.getMaxCooldown());
    }

    @Override
    protected void activate(PlayerEntity caster, List<LivingEntity> targets, double power_multiplier, int duration, double cooldown_time) {
        super.activate(caster, targets, power_multiplier, duration, cooldown_time);
        power_multiplier = checkSafety(power_multiplier, duration).getFirst();
        duration = checkSafety(power_multiplier, duration).getSecond();
        
        caster.getWorld().playSound(null, BlockPos.ofFloored(caster.getPos()), LightSounds.TYPES_SOUNDS.get(lightId), SoundCategory.PLAYERS, 1f, 1f);
        LightComponent component = LIGHT_COMPONENT.get(caster);

        if(!caster.getWorld().isClient()){
            LightParticlesUtil.spawnLightTypeParticle(LightParticles.TYPES_PARTICLES.get(lightId), (ServerWorld) caster.getWorld(), caster.getPos());
        }

        //The self target type adds the forest aura effect, making the player merge with natural blocks and travel trough them, but not see through them
        //The player can't see because they usually are not a mole. And also because I would need to make every block render the insides too which is not ideal
        if(component.getTargets().equals(TargetType.SELF)){
            //The -1 is because status effect levels start from 0
            caster.addStatusEffect(new StatusEffectInstance(LightEffects.FOREST_AURA, duration*20, (int) power_multiplier-1, false, false));
        }
        else if(component.getTargets().equals(TargetType.ALL)) {
            //I think it should be 1-(2 +1 per level) puffs

            if (caster.getWorld().isClient()) {
                return;
            }

            //The bonus number of puffs added if the power level is above 5
            int bonus = 0;
            if (power_multiplier > 5) {
                bonus = Math.max(1, (int) ((power_multiplier - 5) / 2));
            }
            //The total number of puffs that are going to be spawned in
            //The max number is 10(power)+5(bonus)+2=17 puffs. Maybe a bit much? Well the minimum is 1. And above 5 it's 3. So it's more like 3-17.
            int puffs = caster.getRandom().nextBetween(1 + bonus, (int) (2 + power_multiplier + bonus));

            AtomicInteger spawnedPuffs = new AtomicInteger(0);
            AtomicInteger total_duration = new AtomicInteger((int) (duration + BalanceConfig.FOREST_AURA_PUFF_DURATION_MULTIPLIER));
            AtomicInteger tickCounter = new AtomicInteger(PUFF_DELAY);

            //Isn't there a nicer way to do this scheduling?
            double finalPower_multiplier = power_multiplier;
            ServerTickEvents.END_SERVER_TICK.register(server -> {
                //Checks the number of spawned puffs, if it's beyond the number of calculated puffs, stop the loop.
                if (spawnedPuffs.get() >= puffs) {
                    return;
                }
                //Every tick we check the tickCounter, if it's not ten then we only incremenet it and check the next tick
                // if it's puffDelay it means ten ticks have passed so a new puff should spawn, so the rest of the code runs
                if (tickCounter.get() != PUFF_DELAY) {
                    tickCounter.getAndIncrement();
                    return;
                }

                //Resets the tickCounter to 0, so for the next puff we have to way 10 ticks or whatever puffDelay is
                tickCounter.set(0);
                int puff_duration = caster.getRandom().nextBetween(5, 5 + total_duration.get());
                total_duration.set(total_duration.get() - (puff_duration - 5));

                //The list of the types of puffs that can be spawned
                List<Integer> possible_puffs = new ArrayList<>(LOW_TIER_COLOR_PUFFS);

                //If the power level is high enough, the high tier color puffs get added ti the pool
                if (finalPower_multiplier > 5) {
                    possible_puffs.addAll(HIGH_TIER_COLOR_PUFFS);
                }

                int puff = caster.getRandom().nextBetween(0, possible_puffs.size() - 1);
                Vec3d pos = getRandomPos(caster, caster.getPos().add(0, 1, 0), BalanceConfig.FOREST_AURA_PUFF_MAX_SPAWN_DIST, BalanceConfig.FOREST_AURA_PUFF_MIN_SPAWN_DIST);
                if (pos == null) {
                    caster.sendMessage(Text.literal("§c[LightWithin] There was an error spawning the puffs, Position null!"));
                } else {
                    createForestPuff(caster, pos, (ServerWorld) caster.getWorld(), possible_puffs.get(puff), puff_duration, (int) finalPower_multiplier);
                }

                //increments the number of spawned puffs
                spawnedPuffs.getAndIncrement();
            });
        }
    }
        
    /*The puffs mechanism.
        
    The duration might be equal to twice the duration of the caster, divided by the number of puffs
    This for the total duration of all the generated puffs.
    For each puff it's a random number between 1 second and the total duration-the number of puffs yet to create
    */


    /** Gets a random position between an origin point and a specified distance
     * This position must not collide with the model of another block
     *
     * @param entity A living entity that will be used to get the world, random and stuff
     * @param origin The point of origin, which will be used as the center of the box
     * @param max_dist The maximum distance at which a random position can be given
     * @param min_dist The minimum distance at which a random position can be given*/
    public static Vec3d getRandomPos(LivingEntity entity, Vec3d origin, double max_dist, double min_dist){
        Box box = new Box(origin.getX(), origin.getY(), origin.getZ(), (origin.getX() + 1), (origin.getY() + 1), (origin.getZ() + 1)).expand(max_dist);
        double i = origin.getX();
        double j = origin.getY();
        double k = origin.getZ();

        for(int l = 0; l<max_tries; l++){
            double m = i + MathHelper.nextDouble(entity.getRandom(), min_dist,  max_dist) * MathHelper.nextInt(entity.getRandom(), -1, 1);
            double n = j + MathHelper.nextDouble(entity.getRandom(), min_dist,  max_dist) * MathHelper.nextInt(entity.getRandom(), -1, 1);
            double o = k + MathHelper.nextDouble(entity.getRandom(), min_dist,  max_dist) * MathHelper.nextInt(entity.getRandom(), -1, 1);
            Vec3d pos = new Vec3d(m,n,o);
            //Checks if the position is inside the max distance box
            if(box.contains(pos)){
                //Gets the blockshape of the block at that position and check if it's either empty,
                // or the position is outside its bounding box
                VoxelShape block_shape = entity.getWorld().getBlockState(BlockPos.ofFloored(pos)).getCollisionShape(entity.getWorld(), BlockPos.ofFloored(pos));

                if(block_shape.isEmpty()){
                    return pos;
                }
                BlockPos current_blockpos = BlockPos.ofFloored(pos);
                Box bounding_box = block_shape.getBoundingBox();
                Box collision_box = new Box(current_blockpos.getX()+bounding_box.maxX,
                        current_blockpos.getY()+bounding_box.maxY,
                        current_blockpos.getZ()+bounding_box.maxZ,
                        current_blockpos.getX()+bounding_box.minX,
                        current_blockpos.getY()+bounding_box.minY,
                        current_blockpos.getZ()+bounding_box.minX);
                if(!collision_box.contains(pos)){
                    return pos;
                }
                //Otherwise it tries a new position if possible

            }
        }
        LOGGER.error("Exceeded max tries to spawn a new puff for " + entity.getName().toString() + ", skipping!");
        return null;

    }

    /**
     * Spawns a new forest puff of particles and applies the effect associated to the puff color to all the neraby entities
     * It will also play the puff sound
     *
     * @param duration In seconds*/
    public void createForestPuff(LivingEntity caster, Vec3d origin, ServerWorld world, int color, int duration, int power){
        //Converts from ticks to seconds
        int dur = duration * 20;
        float size = 0.7f;

        float pitch = (float) caster.getRandom().nextBetween(9, 11) /10;

        caster.getWorld().playSound(
                null, // Player - if non-null, will play sound for every nearby player *except* the specified player
                BlockPos.ofFloored(origin), // The position of where the sound will come from
                LightSounds.FOREST_AURA_PUFF, // The sound that will play, in this case, the sound the anvil plays when it lands.
                SoundCategory.PLAYERS, // This determines which of the volume sliders affect this sound
                1f, //Volume multiplier, 1 is normal, 0.5 is half volume, etc
                pitch // Pitch multiplier, 1 is normal, 0.5 is half pitch, etc
        );

        List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class,
                new Box(origin.getX(), origin.getY(), origin.getZ(), (origin.getX() + 1), (origin.getY() + 1), (origin.getZ() + 1)).expand(BalanceConfig.FOREST_AURA_PUFF_ACTION_BLOCK_RANGE),
                (entity -> {
                    //If another player has the ForestAura it will not affect them, but some particles will be spawned
                    //TODO if i ever allow entities to have the light powers remember to change this bit here

                    if(entity instanceof PlayerEntity && LIGHT_COMPONENT.get(entity).getType() instanceof ForestAuraLight){
                        world.spawnParticles(LightParticles.TYPES_PARTICLES.get(lightId), entity.getX(), entity.getY(), entity.getZ(), 10, 0.11, 0.11, 0.11, 0.01);
                        ((PlayerEntity) entity).sendMessage(Text.translatable("light.description.negated.forest_aura"), true);
                        return false;
                    }

                    return !entity.equals(caster);

                }));

        if(color == ForestPuffColor.GREEN){
            LightParticlesUtil.spawnForestPuff(origin, Vec3d.unpackRgb(color).toVector3f(), Vec3d.unpackRgb(ForestPuffColor.GREEN_END).toVector3f(), size, world);
            targets.forEach(entity -> entity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, dur, power), caster));
        }else if(color == ForestPuffColor.YELLOW){
            LightParticlesUtil.spawnForestPuff(origin, Vec3d.unpackRgb(color).toVector3f(), Vec3d.unpackRgb(ForestPuffColor.YELLOW_END).toVector3f(), size, world);
            targets.forEach(entity -> entity.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, dur, power), caster));
        }else if(color == ForestPuffColor.RED){
            LightParticlesUtil.spawnForestPuff(origin, Vec3d.unpackRgb(color).toVector3f(), Vec3d.unpackRgb(ForestPuffColor.RED_END).toVector3f(), size, world);
            targets.forEach(entity -> entity.addStatusEffect(new StatusEffectInstance(LightEffects.LIGHT_FATIGUE, dur, power), caster));
        }else if(color == ForestPuffColor.PINK){
            LightParticlesUtil.spawnForestPuff(origin, Vec3d.unpackRgb(color).toVector3f(), Vec3d.unpackRgb(ForestPuffColor.PINK_END).toVector3f(), size, world);
            targets.forEach(entity -> entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, dur, power), caster));
        }else if(color == ForestPuffColor.BLUE){
            LightParticlesUtil.spawnForestPuff(origin, Vec3d.unpackRgb(color).toVector3f(), Vec3d.unpackRgb(ForestPuffColor.BLUE_END).toVector3f(), size, world);
            targets.forEach(entity -> entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, dur, power), caster));
        }else if(color == ForestPuffColor.BLACK){
            LightParticlesUtil.spawnForestPuff(origin, Vec3d.unpackRgb(color).toVector3f(), Vec3d.unpackRgb(ForestPuffColor.BLACK_END).toVector3f(), size, world);
            targets.forEach(entity -> entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, dur, power), caster));
        }else if(color == ForestPuffColor.ORANGE) {
            LightParticlesUtil.spawnForestPuff(origin, Vec3d.unpackRgb(color).toVector3f(), Vec3d.unpackRgb(ForestPuffColor.ORANGE_END).toVector3f(), size, world);
            targets.forEach(entity -> entity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, dur, power), caster));
        }else if(color == ForestPuffColor.PURPLE){
            LightParticlesUtil.spawnForestPuff(origin, Vec3d.unpackRgb(color).toVector3f(), Vec3d.unpackRgb(ForestPuffColor.PURPLE_END).toVector3f(), size, world);
            targets.forEach(entity -> entity.addStatusEffect(new StatusEffectInstance(LightEffects.INTOXICATION, dur, power), caster));
        }
    }

    public static class ForestPuffColor {
        /* Possible effects:
         * Green -> poison.
         * Purple -> Drunk-like effect
         * Red -> Inverted controls ?
         * Yellow -> hunger
         * Pink -> nausea
         *
         * */
        public static int YELLOW = 13493002; // hunger, #CDE30A
        public static int YELLOW_END = 8436752; // hunger, #80bc10
        public static int GREEN = 1469729; // poison, #166D21
        public static int GREEN_END = 8436752; // poison, #09391C
        public static int RED = 10552340; // light fatigue, #A10414
        public static int RED_END = 5243931; // light fatigue, #50041B
        public static int PURPLE = 3736132; // intoxicated (drunk-like effect) (most common along with green i guess), #390244
        public static int PURPLE_END = 6948697; // intoxicated (drunk-like effect) (most common along with green i guess), #6A0759
        public static int PINK = 12406156; // nausea, #BD4D8C
        public static int PINK_END = 16293842; // nausea, #F89FD2
        public static int BLUE = 1980527; // slowness, #1E386F
        public static int BLUE_END = 69681; // slowness, #011031
        public static int BLACK = 1250561; // wither, #131501
        public static int BLACK_END = 2106123; // wither, #20230B
        public static int ORANGE = 14707743; // blindess, #E06C1F (like an orange in your eyes)
        public static int ORANGE_END = 11291660; // blindess, #AC4C0C
    }

    @Override
    public void triggerCheck(PlayerEntity player, LightComponent component, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        double trigger_sum = 0;

        if(component.getTargets().equals(TargetType.ALL)){

            //Triggers with: Low health, Very low health, Surrounded,
            // forest, allies (+1), checkLeavesAround, poisoned or debuffed maybe

            //Checks if the player is very low health
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum+ TriggerConfig.FOREST_AURA_ALL_VERY_LOW_HEALTH; //+3
                //Checks if the player has low health
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+Config.HP_PERCENTAGE_INCREMENT)){
                trigger_sum=trigger_sum+TriggerConfig.FOREST_AURA_ALL_LOW_HEALTH; //+2
            }
            if(CheckUtils.checkAllyHealth(player, attacker, Config.HP_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum + TriggerConfig.FOREST_AURA_ALL_ALLY_LOW_HEALTH; //+1
            }
            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+TriggerConfig.FOREST_AURA_ALL_SURROUNDED;//+2
            }
            if(CheckUtils.checkNearLeaves(player, TriggerConfig.FOREST_AURA_PERCENT_OF_LEAVES_REQUIRED)){
                trigger_sum = trigger_sum+TriggerConfig.FOREST_AURA_ALL_LEAVES; //+1
            }
            //Checks if the player has the optimal criteria for activation
            if(checkLightConditions(player)){
                trigger_sum=trigger_sum+TriggerConfig.FOREST_AURA_ALL_CONDITIONS; //+3
            }
            if(trigger_sum >= getMinTrigger()){
                sendLightTriggered((ServerPlayerEntity) player);
            }
        }
        /**CHECKS if the player has ENEMIES as target, either his or his allies health needs to be low*/
        else if(component.getTargets().equals(TargetType.SELF)){

            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum+TriggerConfig.FOREST_AURA_SELF_VERY_LOW_HEALTH; //+3
                //Checks if the player has low health
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+Config.HP_PERCENTAGE_INCREMENT)){
                trigger_sum=trigger_sum+TriggerConfig.FOREST_AURA_SELF_LOW_HEALTH; //+2
            }
            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+TriggerConfig.FOREST_AURA_SELF_SURROUNDED;//+2
            }
            if(CheckUtils.checkHasHarmfulStatusEffect(player)){
                trigger_sum = trigger_sum+TriggerConfig.FOREST_AURA_SELF_HARMFUL_EFFECT; //+1
            }
            //Checks if the player has the optimal criteria for activation
            if(checkLightConditions(player)){
                trigger_sum=trigger_sum+TriggerConfig.FOREST_AURA_SELF_CONDITIONS; //+3
            }
            if(trigger_sum >= getMinTrigger()){
                sendLightTriggered((ServerPlayerEntity) player);
            }
        }
    }

    /**Used to check if the player has something that can be considered a ForestAura source
     *
     * @param player The player to perform checks on*/
    @Override
    public boolean checkLightConditions(PlayerEntity player) {
        RegistryEntry<Biome> biome = player.getWorld().getBiome(player.getBlockPos());
        if(biome.isIn(BiomeTags.IS_FOREST) || biome.isIn(BiomeTags.IS_JUNGLE) || biome.isIn(BiomeTags.IS_TAIGA)){
            return true;
        }

        ItemStack main = player.getMainHandStack();
        ItemStack off = player.getOffHandStack();
        return main.isIn(ItemTags.SAPLINGS) || off.isIn(ItemTags.SAPLINGS);
    }

    @Override
    public String toString() {
        return this.lightId.getPath();
    }
}
