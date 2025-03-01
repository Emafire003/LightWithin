package me.emafire003.dev.lightwithin.util;

import me.emafire003.dev.lightwithin.compat.argonauts.ArgonautsChecker;
import me.emafire003.dev.lightwithin.compat.factions.FactionChecker;
import me.emafire003.dev.lightwithin.compat.flan.FlanCompat;
import me.emafire003.dev.lightwithin.compat.ftb_teams.FTBTeamsChecker;
import me.emafire003.dev.lightwithin.compat.open_parties_and_claims.OPACChecker;
import me.emafire003.dev.lightwithin.compat.yawp.YawpCompat;
import me.emafire003.dev.lightwithin.component.SummonedByComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.lights.AquaLight;
import me.emafire003.dev.lightwithin.lights.BlazingLight;
import me.emafire003.dev.lightwithin.lights.FrostLight;
import me.emafire003.dev.lightwithin.lights.WindLight;
import me.emafire003.dev.lightwithin.lights.ThunderAuraLight;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static me.emafire003.dev.lightwithin.LightWithin.*;

public class CheckUtils {

    /**Checks if an entity is surrounded by hostile entities
     * <p>
     * If not enabled returns true to not mess with the &&
     *
     * @param entity The entity that could be surrounded*/
    public static boolean checkSurrounded(@NotNull LivingEntity entity){
        if(!Config.CHECK_SURROUNDED){
            return false;
        }

        List<PlayerEntity> players = entity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(entity.getBlockPos()).expand(Config.SURROUNDED_DISTANCE), (entity1 -> true));
        int enemies = 0;
        for(PlayerEntity p : players){
            if(CheckAllies.checkEnemies(p, entity)){
                enemies++;
            }
        }

        List<HostileEntity> entities = entity.getWorld().getEntitiesByClass(HostileEntity.class, new Box(entity.getBlockPos()).expand(Config.SURROUNDED_DISTANCE), (entity1 -> true));
        //Should I check if two or more allies are surrounded or just do a bigger area search
        if(Config.CHECK_SURROUNDING_MOBS_HEALTH){
            for (HostileEntity ent : entities){
                if(!(ent.getHealth() <= (ent.getMaxHealth())*Config.SURROUNDING_HEALTH_THRESHOLD/100)){
                    enemies++;
                }
            }
        }else{
            enemies = enemies + entities.size();
        }

        return enemies >= Config.SURROUNDED_AMOUNT;
    }

    /**Checks if an entity is surrounded by allied entities
     * <p>
     * If not enabled returns true to not mess with the &&
     *
     * @param entity The entity that could be surrounded*/
    public static boolean checkSurroundedByAllies(@NotNull LivingEntity entity){
        if(!Config.CHECK_SURROUNDED_BY_ALLIES){
            return false;
        }

        List<PlayerEntity> players = entity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(entity.getBlockPos()).expand(Config.SURROUNDED_DISTANCE), (entity1 -> true));
        int allies = 0;
        for(PlayerEntity p : players){
            if(CheckAllies.checkAlly(p, entity)){
                allies++;
            }
        }

        return allies >= Config.SURROUNDED_AMOUNT;

    }

    /**Sums up all of the durability of the armor items and if it's below
     * a certain percentage it will return true.
     * <p>
     * An empty armor slot counts as an iron armor with 0 durability*/
    public static boolean checkArmorDurability(PlayerEntity player, int dur_percent){
        if(!Config.CHECK_ARMOR_DURABILITY){
            //returns true so it doesn't mess with the &&
            return true;
        }
        ItemStack helmet = player.getInventory().getArmorStack(0);
        int helmet_dmg = 0;
        int helmet_max = 165;
        if(helmet != null){
            helmet_dmg = helmet.getMaxDamage()-helmet.getDamage();
            helmet_max = helmet.getMaxDamage();
        }
        ItemStack chest = player.getInventory().getArmorStack(1);
        int chest_dmg = 0;
        int chest_max = 240;
        if(chest != null){
            chest_dmg = chest.getMaxDamage()-chest.getDamage();
            chest_max = chest.getMaxDamage();
        }
        ItemStack legs = player.getInventory().getArmorStack(2);
        int legs_dmg = 0;
        int legs_max = 165;
        if(legs != null){
            legs_dmg = legs.getMaxDamage()-legs.getDamage();
            legs_max = legs.getMaxDamage();
        }
        ItemStack boots = player.getInventory().getArmorStack(3);
        int boots_dmg = 0;
        int boots_max = 165;
        if(boots != null){
            boots_dmg = boots.getMaxDamage()-boots.getDamage();
            boots_max = boots.getMaxDamage();
        }

        int total_dmg = helmet_dmg+chest_dmg+legs_dmg+boots_dmg;
        int total_max_dmg = helmet_max+chest_max+legs_max+boots_max;

        return total_dmg <= (total_max_dmg)*dur_percent/100;
    }

    public static boolean checkAllyArmor(PlayerEntity player, Entity ally, int dur_percent){
        if(ally instanceof LivingEntity && CheckAllies.checkAlly(player, (LivingEntity) ally) && ally instanceof PlayerEntity){
            return checkArmorDurability((PlayerEntity) ally, dur_percent);
        }
        return false;
    }

    /**Checks both the health and armor of the player,
     * and check if it is below a certain percentage.
     * If it is, returns true
     *
     * @param player The player that could trigger their light
     * @param health_percent The percentage (15, 25, 70) below which the target is in danger (hence light activatable)
     * */
    public static boolean checkSelfHealthAndArmor(@NotNull PlayerEntity player, int health_percent){
        return player.getArmor()+player.getHealth() <= (player.getMaxHealth())*health_percent/100;
    }


    private static float getModifyAppliedDamage(DamageSource source, float amount, LivingEntity entity){
        if (source.isIn(DamageTypeTags.BYPASSES_EFFECTS)) {
            return amount;
        } else {
            int i;
            if (entity.hasStatusEffect(StatusEffects.RESISTANCE) && !source.isIn(DamageTypeTags.BYPASSES_RESISTANCE)) {
                try{
                    i = (entity.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1) * 5;
                    int j = 25 - i;
                    float f = amount * (float)j;
                    amount = Math.max(f / 25.0F, 0.0F);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (amount <= 0.0F) {
                return 0.0F;
            } else if (source.isIn(DamageTypeTags.BYPASSES_ENCHANTMENTS)) {
                return amount;
            } else {
                i = EnchantmentHelper.getProtectionAmount(entity.getArmorItems(), source);
                if (i > 0) {
                    amount = DamageUtil.getInflictedDamage(amount, (float)i);
                }

                return amount;
            }
        }
    }

    private static float getAppliedArmorToDamage(DamageSource source, float amount, LivingEntity entity){
        if (!source.isIn(DamageTypeTags.BYPASSES_ARMOR)) {
            amount = DamageUtil.getDamageLeft(amount, (float)entity.getArmor(), (float)entity.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));
        }

        return amount;
    }

    /**Calculates the attack damage that an entity could do to another entity, not accounting for its speed*/
    private static float getAttackDamage(@NotNull LivingEntity attacker, @NotNull LivingEntity target){
        float dmg = (float)attacker.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        dmg += EnchantmentHelper.getAttackDamage(attacker.getMainHandStack(), target.getGroup());
        if(target instanceof PlayerEntity){
            target.sendMessage(Text.literal("The non calcl damage is §6" +dmg));

        }
        return dmg;
    }

    /**Calculates the attack damage that an entity could do to another entity, accounting for its speed*/
    private static float getAttackDamageWithSpeed(@NotNull LivingEntity attacker,@NotNull LivingEntity target){
        float dmg = (float)attacker.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        dmg += EnchantmentHelper.getAttackDamage(attacker.getMainHandStack(), target.getGroup());
        if(attacker instanceof PlayerEntity){
            float spd = (float)attacker.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED);
            return dmg*spd;
        }
        return dmg;
    }


    /**
     * Calculates the next attack damage of the last entity that attacked the player
     * I can't just use the last attack variable because mixins refuse to work and
     * I could only get the non-armor-filtered damage*/
    public static float getTotDamage(@NotNull LivingEntity player){
        DamageSource damage_source = player.getRecentDamageSource();
        if(damage_source == null){
            //Probably the player hasn't been hit by anything recently, so using the simpler checks
            return -1;
        }

        //Checking if the attacker is (not null) a living entity
        if(player.getRecentDamageSource().getAttacker() != null && player.getRecentDamageSource().getAttacker() instanceof LivingEntity){
            LivingEntity attacker = (LivingEntity) player.getRecentDamageSource().getAttacker();
            float amount = getAttackDamageWithSpeed(attacker, player);
            amount = getAppliedArmorToDamage(player.getRecentDamageSource(), amount, player);
            amount = getModifyAppliedDamage(player.getRecentDamageSource(), amount, player);
            amount = Math.max(amount - player.getAbsorptionAmount(), 0.0F);
            return amount;

        }

        return -1;
    }

    /**Checks to see if the player would be below a certain threshold
     * after the next attack, if it is returns true.
     * <p>
     * Alternatively, Checks both the health and armor of the player,
     * and check if it is below a certain percentage.
     * If it is, returns true
     *
     * @param player The player that could trigger their light
     * @param health_percent The percentage (15, 25, 70) below which the target is in danger (hence light activatable)
     * */
    public static boolean checkSelfDanger(@NotNull LivingEntity player, int health_percent){
        float tot_damaged = getTotDamage(player);
        if(tot_damaged == -1){
            return player.getArmor()+player.getHealth() <= (player.getMaxHealth())*health_percent/100;
        }

        //If with the next hit the player would reach less than the health_percent activate
        if(player.getHealth()-tot_damaged <= (player.getMaxHealth())*health_percent/100){
            return true;
        }

        //Simpler check TODO maybe make it a bit more precise with the right formula
        return player.getArmor()+player.getHealth() <= (player.getMaxHealth())*health_percent/100;
    }



    /**Just checks if the health of the player is below a certain percentage.
     * If it is, returns true
     *
     * @param player The player that could trigger their light
     * @param health_percent The percentage (15, 25, 70) below which the target is in danger (hence light activatable)
     * */
    public static boolean checkSelfHealth(@NotNull PlayerEntity player, int health_percent){
        return player.getHealth() <= (player.getMaxHealth())*health_percent/100;

    }

    /**Will check for nearby allies, if they are on low health
     * it will return true
     *
     * @param player The player that could trigger their light
     * @param attacker The entity that is attacking it
     * @param health_percent The percentage (15, 25, 70) below which the target is in danger (hence light activatable)
     * */
    @Deprecated
    public static boolean checkAllyHealthOld(@NotNull PlayerEntity player, Entity attacker, int health_percent){
        List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true));
        int ent_number = 0;
        //I need to this to prevent a ConcurrentModificationError
        List<LivingEntity> team_entities = new ArrayList<>();
        //loops through the entities near the player, if the entities are in the same team as the player
        //and they are not the entity that has been hit then add them to the team_entities and check if their health is ok
        for(LivingEntity ent : entities){
            //Checks if the entity in the list is in the same team/faction/party/pet or not
            if(!attacker.equals(ent) && CheckAllies.checkAlly(player, ent) ){
                //if it is, check the health
                if(ent.getHealth() <= (ent.getMaxHealth())*health_percent/100){
                    ent_number++;
                }
                team_entities.add(ent);
            }
        }
        //If the total team targets && the number of entities of team with the right health are true then
        //return true
        return team_entities.size() == ent_number;
        //otherwise, false
    }

    /**Will check for nearby allies, if they are on low health
     * it will return true. Depending on the configuration more than one ally may need to
     * be on low health
     *
     * @param player The player that could trigger their light
     * @param attacker The entity that is attacking the player-triggerer (in order to not count them in the allies/triggering)
     * @param health_percent The percentage (15, 25, 70) below which the target is in danger (hence light activatable)
     * */
    public static boolean checkAllyHealth(@NotNull PlayerEntity player, Entity attacker, int health_percent){
        List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true));
        int n_allies_low_health = 0;
        //loops through the entities near the player, if the entities are in the same team as the player
        //and they are not the entity that has been hit then add them to the team_entities and check if their health is ok
        for(LivingEntity ent : entities){
            //Checks if the entity in the list is in the same team/faction/party/pet or not
            if(!player.equals(attacker) && !attacker.equals(ent) && !player.equals(ent) && CheckAllies.checkAlly(player, ent) ){
                //if it is, check the health
                if(checkSelfDanger(ent, health_percent)){
                    n_allies_low_health++;
                }
            }
        }
        //If the total team targets && the number of entities of team with the right health are true then
        //return true
        return n_allies_low_health >= Config.MIN_ALLIES_LOW;
        //otherwise, false
    }


    /**Cheks if there are passive mobs nearby, if there are
     * checks the helath. If it is below the health_percent returns true
     *
     * @param player The player that could trigger their light
     * @param entity The entity that attacked the player
     * @param health_percent The percentage (15, 25, 70) below which the target is in danger (hence light activatable)
     * */
    public static boolean checkPassiveHealth(PlayerEntity player, Entity entity, int health_percent){
        List<PassiveEntity> entities = entity.getWorld().getEntitiesByClass(PassiveEntity.class, new Box(player.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true));
        for(PassiveEntity ent : entities){
            if(ent.getHealth() <= (ent.getMaxHealth())*health_percent/100){
                return true;
            }
        }
        return false;
    }

    public static class CheckAllies {

        public static boolean checkTeam(LivingEntity entity, LivingEntity teammate){
            return entity.getScoreboardTeam() != null && entity.isTeammate(teammate);
        }

        public static boolean checkFaction(PlayerEntity player, PlayerEntity player1){
            return FactionChecker.areInSameFaction(player, player1) || FactionChecker.areAllies(player, player1);
        }

        public static boolean checkEnemyFaction(PlayerEntity player, PlayerEntity player1){
            return FactionChecker.areEnemies(player, player1);
        }

        public static boolean checkPartyArgo(PlayerEntity player, PlayerEntity player1){
            return ArgonautsChecker.areInSameParty(player,player1);
        }

        public static boolean checkGuildArgo(PlayerEntity player, PlayerEntity player1){
            return ArgonautsChecker.areInSameGuild(player,player1);
        }

        public static boolean checkArgonauts(PlayerEntity player, PlayerEntity player1){
            return checkPartyArgo(player,player1) || checkGuildArgo(player,player1);
        }

        public static boolean checkOPACParty(PlayerEntity player, PlayerEntity player1){
            return OPACChecker.areInSameParty(player, player1) || OPACChecker.areInAlliedParties(player, player1);
        }

        public static boolean checkFTBTeams(ServerPlayerEntity player, ServerPlayerEntity player1){
            return FTBTeamsChecker.areInSameParty(player, player1) || FTBTeamsChecker.areInAlliedPartis(player, player1);
        }

        public static boolean checkPet(LivingEntity entity, LivingEntity ent){
            if(ent instanceof TameableEntity){
                return entity.equals(((TameableEntity) ent).getOwner());
            }
            return false;
        }

        public static boolean checkSummoned(LivingEntity summoner, LivingEntity ent){
            SummonedByComponent component = SUMMONED_BY_COMPONENT.getNullable(ent);
            if(component != null && component.getIsSummoned()){
                return component.getSummonerUUID().equals(summoner.getUuid());
            }
            return false;
        }

        public static boolean checkAlly(LivingEntity entity, LivingEntity teammate){
            if(entity.getWorld().isClient()){
                return false;
            }
            if(FabricLoader.getInstance().isModLoaded(FactionChecker.getModId()) && entity instanceof PlayerEntity && teammate instanceof PlayerEntity){
                if(checkFaction((PlayerEntity) entity, (PlayerEntity) teammate)){
                    return true;
                }
            }
            if(FabricLoader.getInstance().isModLoaded(ArgonautsChecker.getModId()) && entity instanceof PlayerEntity && teammate instanceof PlayerEntity){
                if(checkArgonauts((PlayerEntity) entity, (PlayerEntity) teammate)){
                    return true;
                }
            }
            if(FabricLoader.getInstance().isModLoaded(OPACChecker.getModId()) && entity instanceof PlayerEntity && teammate instanceof PlayerEntity){
                if(checkOPACParty((PlayerEntity) entity, (PlayerEntity) teammate)){
                    return true;
                }
            }
            if(FabricLoader.getInstance().isModLoaded(FTBTeamsChecker.getModId()) && entity instanceof PlayerEntity && teammate instanceof PlayerEntity){
                if(checkFTBTeams((ServerPlayerEntity) entity, (ServerPlayerEntity) teammate)){
                    return true;
                }
            }
            return checkTeam(entity, teammate) || checkPet(entity, teammate) || checkSummoned(entity, teammate);

        }

        public static boolean checkEnemies(LivingEntity entity, LivingEntity enemy){
            if(FabricLoader.getInstance().isModLoaded("factions") && entity instanceof PlayerEntity && enemy instanceof PlayerEntity){
                if(Config.NOT_ALLY_THEN_ENEMY){
                    return !checkAlly(entity, enemy);
                }
                return checkEnemyFaction((PlayerEntity) entity, (PlayerEntity) enemy);
            }
            return false;
        }
    }

    /**Checks for the presence of a block in the radius around the player and have the selected tag
     *
     * @param player The player at the center of the blocks/the target
     * @param rad The radius around the player to check for the blocks
     * @param tag The block tagKey that the blocks need to have*/
    public static boolean checkBlocksWithTag(PlayerEntity player, int rad, TagKey<Block> tag){
        BlockPos origin = player.getBlockPos();
        boolean found = false;
        for(int y = -rad; y <= rad; y++)
        {
            for(int x = -rad; x <= rad; x++)
            {
                for(int z = -rad; z <= rad; z++)
                {
                    BlockPos pos = origin.add(x, y, z);
                    found = player.getWorld().getBlockState(pos).isIn(tag);

                }
            }
        }
        return found;
    }

    /** Checks multiple blocks around a player
     *  to see if more than a given number have a given tag
     *
     * @param player The player around which to check blocks
     * @param rad The radius of blocks to check (radius not diameter!)
     * @param block_number How many blocks should have the tag in order to be ok
     * @param tag The tag that the blocks need to have
     * */
    public static boolean checkMultipleBlocksWithTags(PlayerEntity player, int rad, int block_number, TagKey<Block> tag){
        //If the terrain under the player's feet is natural block (times 3 aka 3 blocks down), will create a moat,  if not a wall.
        int number = 0;
        BlockPos origin = player.getBlockPos();
        for(int y = -rad; y <= rad; y++)
        {
            for(int x = -rad; x <= rad; x++)
            {
                for(int z = -rad; z <= rad; z++)
                {
                    BlockPos pos = origin.add(x, y, z);
                    if(player.getWorld().getBlockState(pos).isIn(tag)){
                        number++;
                    }
                }
            }
        }
        return number >= block_number;
    }

    /** Checks for blocks in a certain radius from the player pos
     * if they match at least one from the given list.
     * <p>
     * If SHOULD_CHECK_BLOCKS from the config it's on false, it will only check the block
     * under the player's feet.
     *
     * @param player The player for which we are performing the check for
     * @param blocks A list of blocks that if found, will return a positive match
     * @param rad The radius in block in which to check (The lower, the better for the performance)
     * */
    public static boolean checkBlocks(PlayerEntity player, List<Block> blocks, int rad){
        if(!Config.SHOULD_CHECK_BLOCKS){
            BlockPos pos = player.getBlockPos().add(0, -1, 0);
            if(blocks.contains((player.getWorld().getBlockState(pos).getBlock()))){
                return true;
            }
        }

        BlockPos origin = player.getBlockPos();
        for(int y = -rad; y <= rad; y++)
        {
            for(int x = -rad; x <= rad; x++)
            {
                for(int z = -rad; z <= rad; z++)
                {
                    BlockPos pos = origin.add(x, y, z);
                    if(blocks.contains(player.getWorld().getBlockState(pos).getBlock())){
                        return true;
                    }

                }
            }
        }
        //If no match has been found, return false.
        return false;
    }

    /** Checks for blocks in a certain radius from the player pos
     * if they match a waterlogged block or at least one from a given list.
     * <p>
     * If SHOULD_CHECK_BLOCKS from the config it's on false, it will only check the block
     * under the player's feet.
     *
     * @param player The player for which we are performing the check for
     * @param rad The radius in block in which to check (The lower, the better for the performance)
     * @param blocksTagKey A tag of blocks that if found, will return a positive match
     * */
    public static boolean checkWaterLoggedOrTag(PlayerEntity player, int rad, TagKey<Block> blocksTagKey){
        if(!Config.SHOULD_CHECK_BLOCKS){
            BlockPos pos = player.getBlockPos().add(0, -1, 0);
            if(player.getWorld().getBlockState(pos).getProperties().contains(Properties.WATERLOGGED)){
                return player.getWorld().getBlockState(pos).get(Properties.WATERLOGGED);
            }
            return player.getWorld().getBlockState(pos).isIn(blocksTagKey);
        }

        BlockPos origin = player.getBlockPos();
        for(int y = -rad; y <= rad; y++)
        {
            for(int x = -rad; x <= rad; x++)
            {
                for(int z = -rad; z <= rad; z++)
                {
                    BlockPos pos = origin.add(x, y, z);

                    if(player.getWorld().getBlockState(pos).getProperties().contains(Properties.WATERLOGGED)){
                        if(player.getWorld().getBlockState(pos).get(Properties.WATERLOGGED)){
                            return true;
                        }
                    }
                    if(player.getWorld().getBlockState(pos).isIn(blocksTagKey)){
                        return true;
                    }

                }
            }
        }
        //If no match has been found, return false.
        return false;
    }

    /** Checks for multiple blocks in a certain radius from the player pos
     * if they match at least one from the given list.
     * <p>
     * If SHOULD_CHECK_BLOCKS from the config it's on false, it will only check the block
     * under the player's feet.
     *
     * @param player The player for which we are performing the check for
     * @param blocks A list of blocks that if found, will return a positive match
     * @param rad The radius in block in which to check (The lower, the better for the performance)
     * @param number The minimum number of blocks around the player needed for it to return true
     * */
    public static boolean checkMultipleBlocks(PlayerEntity player, List<Block> blocks, int rad, int number){
        if(!Config.SHOULD_CHECK_BLOCKS){
            BlockPos pos = player.getBlockPos().add(0, -1, 0);
            if(blocks.contains((player.getWorld().getBlockState(pos).getBlock()))){
                return true;
            }
        }
        int n = 0;

        BlockPos origin = player.getBlockPos();
        for(int y = -rad; y <= rad; y++)
        {
            for(int x = -rad; x <= rad; x++)
            {
                for(int z = -rad; z <= rad; z++)
                {
                    BlockPos pos = origin.add(x, y, z);
                    if(blocks.contains(player.getWorld().getBlockState(pos).getBlock())){
                        n++;
                    }

                }
            }
        }
        return n >= number;
        //If no match has been found, return false.
    }

    /** Checks for multiple blocks in a certain radius from the player pos
     * if they match at least one from the given list.
     * <p>
     * If SHOULD_CHECK_BLOCKS from the config it's on false, it will only check the block
     * under the player's feet.
     *
     * @param player The player for which we are performing the check for
     * @param blockTagKey A tag of blocks that if found, will return a positive match
     * @param rad The radius in block in which to check (The lower, the better for the performance)
     * @param number The minimum number of blocks around the player needed for it to return true
     * */
    public static boolean checkMultipleBlocks(PlayerEntity player, TagKey<Block> blockTagKey, int rad, int number){
        if(!Config.SHOULD_CHECK_BLOCKS){
            BlockPos pos = player.getBlockPos().add(0, -1, 0);
            if(player.getWorld().getBlockState(pos).isIn(blockTagKey)){
                return true;
            }
        }
        int n = 0;

        BlockPos origin = player.getBlockPos();
        for(int y = -rad; y <= rad; y++)
        {
            for(int x = -rad; x <= rad; x++)
            {
                for(int z = -rad; z <= rad; z++)
                {
                    BlockPos pos = origin.add(x, y, z);
                    if(player.getWorld().getBlockState(pos).isIn(blockTagKey)){
                        n++;
                    }

                }
            }
        }
        return n >= number;
        //If no match has been found, return false.
    }

    /** Checks for multiple blocks in a certain radius from the player pos
     * if they match at least one from the given list.
     * <p>
     * If SHOULD_CHECK_BLOCKS from the config it's on false, it will only check the block
     * under the player's feet.
     *
     * @param player The player for which we are performing the check for
     * @param blockTagKey A tag of blocks that if found, will return a positive match
     * @param rad The radius in block in which to check (The lower, the better for the performance)
     * @param percent The minimum percent of blocks around the player needed for it to return true
     * */
    public static boolean checkMultipleBlocksPercent(PlayerEntity player, TagKey<Block> blockTagKey, int rad, double percent){
        if(!Config.SHOULD_CHECK_BLOCKS){
            BlockPos pos = player.getBlockPos().add(0, -1, 0);
            if(player.getWorld().getBlockState(pos).isIn(blockTagKey)){
                return true;
            }
        }
        int n = 0;
        int tot = 0;

        BlockPos origin = player.getBlockPos();
        for(int y = -rad; y <= rad; y++)
        {
            for(int x = -rad; x <= rad; x++)
            {
                for(int z = -rad; z <= rad; z++)
                {
                    BlockPos pos = origin.add(x, y, z);
                    tot = tot+1;
                    if(player.getWorld().getBlockState(pos).isIn(blockTagKey)){
                        n++;
                    }

                }
            }
        }
        return percent >= ( (double) (100 * n)/tot );
    }

    /**Used to check if the player has something that can be considered a Heat Source
     * for the Blazing Light
     *
     * @param player The player to perform checks on*/
    public static boolean checkBlazing(PlayerEntity player){
        if(player.isOnFire()){
            return true;
        }

        ItemStack main = player.getMainHandStack();
        ItemStack off = player.getOffHandStack();
        if(main.isIn(BlazingLight.BLAZING_TRIGGER_ITEMS) || off.isIn(BlazingLight.BLAZING_TRIGGER_ITEMS)){
            return true;
        }
        return checkBlocksWithTag(player, Config.TRIGGER_BLOCK_RADIUS, BlazingLight.BLAZING_TRIGGER_BLOCKS);
    }

    /**Used to check if the player has something that can be considered a Cold Source
     * for the Frost Light
     *
     * @param player The player to perform checks on*/
    public static boolean checkFrost(PlayerEntity player){
        if(player.isFrozen()){
            return true;
        }

        ItemStack main = player.getMainHandStack();
        ItemStack off = player.getOffHandStack();
        if(main.isIn(FrostLight.FROST_TRIGGER_ITEMS) || off.isIn(FrostLight.FROST_TRIGGER_ITEMS)){
            return true;
        }
        return checkBlocksWithTag(player, Config.TRIGGER_BLOCK_RADIUS, FrostLight.FROST_TRIGGER_BLOCKS);
    }

    /**Used to check if the player can trigger the Earthen Light, aka if they have
     * dirt in their inventory or are sourronded by natural blocks
     *
     * @param player The player to perform checks on*/
    public static boolean checkEarthen(PlayerEntity player){

        //Moved this so it doesn't consume the dirt unless needed
        if(checkMultipleBlocksWithTags(player, Config.TRIGGER_BLOCK_RADIUS, 3, TagKey.of(RegistryKeys.BLOCK, BlockTags.LUSH_GROUND_REPLACEABLE.id()))){
            return true;
        }
        if(player.getInventory().contains(new ItemStack(Items.DIRT, 64))){
            player.getInventory().removeStack(player.getInventory().getSlotWithStack(new ItemStack(Items.DIRT, 64)));
            return true;
        }

        return false;
    }

    /**Used to check if the player has something that can be considered a Cold Source
     * for the Frost Light
     *
     * @param player The player to perform checks on*/
    public static boolean checkWind(PlayerEntity player){
        //If the player is above sea level and can see the sky winds are there right?
        if(player.getY() >= 64){
            if(!player.getEntityWorld().isSkyVisible(player.getBlockPos())){
                return true;
            }
            return true;
        }

        return checkMultipleBlocksWithTags(player, Config.TRIGGER_BLOCK_RADIUS, 7, WindLight.WIND_TRIGGER_BLOCKS);
    }


    /**Used to check if the player has something that can be considered a Aqua source
     *
     * @param player The player to perform checks on*/
    public static boolean checkAqua(PlayerEntity player){
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

    /**Used to check if the player has something that can be considered a ForestAura source
     *
     * @param player The player to perform checks on*/
    public static boolean checkForestAura(PlayerEntity player){
        RegistryEntry<Biome> biome = player.getWorld().getBiome(player.getBlockPos());
        if(biome.isIn(BiomeTags.IS_FOREST) || biome.isIn(BiomeTags.IS_JUNGLE) || biome.isIn(BiomeTags.IS_TAIGA)){
            return true;
        }

        ItemStack main = player.getMainHandStack();
        ItemStack off = player.getOffHandStack();
        return main.isIn(ItemTags.SAPLINGS) || off.isIn(ItemTags.SAPLINGS);
    }

    /**Used to check if the player is near some leaves
     *
     * @param player The player to perform checks on
     * @param percent The minimum percent of blocks that need to be leaves around the player (0-100)
     * */
    public static boolean checkNearLeaves(PlayerEntity player, double percent){
        return checkMultipleBlocksPercent(player, BlockTags.LEAVES, Config.TRIGGER_BLOCK_RADIUS, percent);
    }

    /**Used to check if the player has something that can be considered a ThunderAura source
     *
     * @param player The player to perform checks on*/
    //TODO somehow make these datadriven/customizable at some point?
    public static boolean checkThunderAura(PlayerEntity player){
        if(checkThundering(player.getWorld())){
            return true;
        }
        //If the player is standing on a is copper rod then lightning conditions met
        if(player.getWorld().getBlockState(player.getBlockPos().down()).isOf(Blocks.LIGHTNING_ROD)
            || player.getWorld().getBlockState(player.getBlockPos()).isOf(Blocks.LIGHTNING_ROD)){
            return true;
        }
        if(checkRecentlyStruckByLightning(player)){
            return true;
        }

        ItemStack main = player.getMainHandStack();
        ItemStack off = player.getOffHandStack();
        return main.isIn(ThunderAuraLight.THUNDER_AURA_TRIGGER_ITEMS) || off.isIn(ThunderAuraLight.THUNDER_AURA_TRIGGER_ITEMS);
    }

    public static boolean checkFalling(LivingEntity entity) {
        if(entity instanceof PlayerEntity){
            return entity.fallDistance > 5 && !entity.isFallFlying() && !entity.isOnGround() && !entity.isClimbing() && !((PlayerEntity) entity).getAbilities().flying && !entity.isSwimming();

        }
        return entity.fallDistance > 5 && !entity.isFallFlying() && !entity.isOnGround() && !entity.isClimbing()  && !entity.isSwimming();
    }

    public static boolean checkPoisoned(LivingEntity entity){
        return entity.hasStatusEffect(StatusEffects.POISON);
    }

    public static boolean checkHasHarmfulStatusEffect(LivingEntity entity){
        for(StatusEffect status : entity.getActiveStatusEffects().keySet()){
            if(status.getCategory().equals(StatusEffectCategory.HARMFUL) && !status.equals(LightEffects.LIGHT_FATIGUE)){
                return true;
            }
        }
        return false;
    }

    public static boolean checkAllyPoisoned(LivingEntity caster, LivingEntity target){
        if(CheckAllies.checkAlly(caster, target)){
            return target.hasStatusEffect(StatusEffects.POISON);
        }
        return false;
    }

    /** Checks if there is currently a stormy weather in the selected world*/
    public static boolean checkThundering(World world){
        return world.isThundering();
    }

    /** Checks if there is currently a rainy weather in the selected world*/
    public static boolean checkRaining(World world){
        return world.isRaining();
    }

    /**Checks if the most recent damage source done to an entity is a lightning bolt*/
    public static boolean checkRecentlyStruckByLightning(LivingEntity entity){
        return entity.getRecentDamageSource() != null && entity.getRecentDamageSource().isOf(DamageTypes.LIGHTNING_BOLT);
    }


    /**Rerturn a list of the player's enemies in the area
     * for entity checks, also know as LightWithin.box_exapansion_amount
     *
     * @param player The player used as the center of the area to search of its enemies*/
    public static List<LivingEntity> getEnemies(PlayerEntity player){
        List<LivingEntity> targets = new ArrayList<>();
        List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true));
        for(LivingEntity ent : entities){
            if(ent instanceof HostileEntity && !CheckUtils.CheckAllies.checkAlly(player, ent)){
                targets.add(ent);
            }
            if(ent instanceof PlayerEntity && FabricLoader.getInstance().isModLoaded("factions")){
                FactionChecker.areEnemies(player, (PlayerEntity) ent);
            }
        }
        return targets;
    }

    /**Rerturn a list of an entity's enemies in the area
     * for entity checks, also known as LightWithin.box_exapansion_amount
     *
     * @param entity The entity used as the center of the area to search of its enemies*/
    public static List<LivingEntity> getEnemies(LivingEntity entity){
        List<LivingEntity> targets = new ArrayList<>();
        List<LivingEntity> entities = entity.getWorld().getEntitiesByClass(LivingEntity.class, new Box(entity.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true));
        for(LivingEntity ent : entities){
            if(ent instanceof HostileEntity && !CheckUtils.CheckAllies.checkAlly(entity, ent)){
                targets.add(ent);
            }
            if(ent instanceof PlayerEntity && FabricLoader.getInstance().isModLoaded("factions") && entity instanceof PlayerEntity){
                FactionChecker.areEnemies((PlayerEntity) entity, (PlayerEntity) ent);
            }
        }
        return targets;
    }

    /**
     * Checks for regions/claims/world protection of sorts, and
     * if the flags say the light can't be activated there, it
     * will return false
     * */
    public static boolean canActivateHere(ServerPlayerEntity player){
        if(FabricLoader.getInstance().isModLoaded("flan")){
            boolean b = FlanCompat.canActivateHere(player, player.getBlockPos());
            if(b != Config.LIGHT_DEFAULT_STATUS){
                return b;
            }
        }
        if(FabricLoader.getInstance().isModLoaded("yawp")){
            boolean b = YawpCompat.canActivateHere(player, player.getBlockPos());
            if(b != Config.LIGHT_DEFAULT_STATUS){
                return b;
            }
        }
        if(FabricLoader.getInstance().isModLoaded("factions")){
            return FactionChecker.canActivateHere(player, player.getBlockPos());
        }
        return Config.LIGHT_DEFAULT_STATUS;
    }

    /**
     * Checks for regions/claims/world protection of sorts, and
     * if the flags say the light that griefes the
     * terrain can't be activated there, it will return false
     * */
    public static boolean canActivateHereGriefing(ServerPlayerEntity player){
        if(FabricLoader.getInstance().isModLoaded("flan")){
            return FlanCompat.canActivateHereGriefing(player, player.getBlockPos());
        }
        if(FabricLoader.getInstance().isModLoaded("yawp")){
            return YawpCompat.canActivateHereGriefing(player, player.getBlockPos());
        }
        return true;
    }

    /**Checks to see if the light-griefing is enabled
     * <p>
     * Checks the config option and for land claims/regions*/
    public static boolean checkGriefable(ServerPlayerEntity player){
        return Config.STRUCTURE_GRIEFING || canActivateHereGriefing(player);
    }

}
