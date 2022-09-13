package me.emafire003.dev.lightwithin.util;

import me.emafire003.dev.lightwithin.compat.factions.FactionChecker;
import me.emafire003.dev.lightwithin.config.Config;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.box_expansion_amount;

public class CheckUtils {

    /**Checks if an entity is surrounded by hostile entities //TODO will need to check for hostile players in factions
     *
     * If not enabled returns true to not mess with the &&
     *
     * @param entity The entity that could be surrounded*/
    public static boolean checkSurrounded(@NotNull LivingEntity entity){
        if(!Config.CHECK_SURROUNDED){
            //returns true so it doesn't mess with the &&
            return true;
        }
        List<HostileEntity> entities = entity.getWorld().getEntitiesByClass(HostileEntity.class, new Box(entity.getBlockPos()).expand(Config.SURROUNDED_DISTANCE), (entity1 -> true));
        //their strength level

        //Should I check if two or more allies are surrounded or just do a bigger area search
        if(Config.CHECK_SURROUNDING_MOBS_HEALTH){
            int mobs_number = 0;
            for (HostileEntity ent : entities){
                if(!(ent.getHealth() <= (ent.getMaxHealth())*Config.SURROUNDING_HEALTH_THRESHOLD/100)){
                    mobs_number++;
                }
            }
            return mobs_number >= Config.SURROUNDED_AMOUNT;
        }else return entities.size() >= Config.SURROUNDED_AMOUNT;
    }

    /**Sums up all of the durability of the armor items and if it below
     * a certain percentage it will return true.
     *
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
    public static boolean checkAllyHealth(@NotNull PlayerEntity player, Entity attacker, int health_percent){
        List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
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

    /**Will check for nearby enemies, if they are on high health and possibly surrounded
     * it will return true
     *
     * @param player The player that could trigger their light
     * @param entity The entity that is attacking it
     * @param health_percent The percentage (15, 25, 70) below which the target is in danger (hence light activatable)
     * */
    public static boolean checkEnemyHealthHigh(@NotNull PlayerEntity player, Entity entity, int health_percent){
        List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
        int ent_number = 0;
        //I need to this to prevent a ConcurrentModificationError
        List<LivingEntity> team_entities = new ArrayList<>();
        //loops through the entities near the player, if the entities are in the same team as the player
        //and they are not the entity that has been hit then add them to the team_entities and check if their health is ok
        for(LivingEntity ent : entities){
            //Checks if the entity in the list is in the same team/faction/party/pet or not
            if(!entity.equals(ent) && CheckAllies.checkAlly(player, ent) ){
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

    /**Cheks if there are passive mobs nearby, if there are
     * checks the helath. If it is below the health_percent returns true
     *
     * @param player The player that could trigger their light
     * @param entity The entity that attacked the player
     * @param health_percent The percentage (15, 25, 70) below which the target is in danger (hence light activatable)
     * */
    public static boolean checkPassiveHealth(PlayerEntity player, Entity entity, int health_percent){
        List<PassiveEntity> entities = entity.getWorld().getEntitiesByClass(PassiveEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
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
            return FactionChecker.areInSameFaction(player, player1) && FactionChecker.areAllies(player, player1);
        }

        public static boolean checkPet(LivingEntity entity, LivingEntity ent){
            if(ent instanceof TameableEntity){
                return entity.equals(((TameableEntity) ent).getOwner());
            }
            return false;
        }

        public static boolean checkAlly(LivingEntity entity, LivingEntity teammate){
            if(FabricLoader.getInstance().isModLoaded("factions") && entity instanceof PlayerEntity && teammate instanceof PlayerEntity){
                return checkFaction((PlayerEntity) entity, (PlayerEntity) teammate);
            }
            return checkTeam(entity, teammate) || checkPet(entity, teammate);

        }
    }

    //TODO make list configable
    private static final List<Item> fire_items = Arrays.asList(Items.TORCH, Items.FIRE_CHARGE, Items.FLINT_AND_STEEL, Items.CAMPFIRE, Items.SOUL_CAMPFIRE, Items.SOUL_TORCH, Items.LAVA_BUCKET);
    private static final List<Block> fire_blocks = Arrays.asList(Blocks.LAVA, Blocks.MAGMA_BLOCK, Blocks.FIRE, Blocks.SOUL_FIRE, Blocks.TORCH, Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH, Blocks.WALL_TORCH, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE);

    public static boolean checkBlocksWithTags(PlayerEntity player, int rad, TagKey tag){
        //If the terrain under the player's feet is natural block (times 3 aka 3 blocks down), will create a moat,  if not a wall.
        List<TagKey<Block>> tags = new ArrayList<>();

        BlockPos origin = player.getBlockPos();
        for(int y = -rad; y <= rad; y++)
        {
            for(int x = -rad; x <= rad; x++)
            {
                for(int z = -rad; z <= rad; z++)
                {
                    BlockPos pos = origin.add(x, y, z);
                    player.getWorld().getBlockState(pos).streamTags().forEach(tags::add);

                }
            }
        }
        return tags.contains(tag);
    }

    public static boolean checkMultipleBlocksWithTags(PlayerEntity player, int rad, int block_number, TagKey tag){
        //If the terrain under the player's feet is natural block (times 3 aka 3 blocks down), will create a moat,  if not a wall.
        List<TagKey<Block>> tags = new ArrayList<>();
        int number = 0;
        BlockPos origin = player.getBlockPos();
        for(int y = -rad; y <= rad; y++)
        {
            for(int x = -rad; x <= rad; x++)
            {
                for(int z = -rad; z <= rad; z++)
                {
                    BlockPos pos = origin.add(x, y, z);
                    player.getWorld().getBlockState(pos).streamTags().forEach(tags::add);
                    if(tags.contains(tag)){
                        number++;
                    }
                }
            }
        }
        return number >= block_number;
    }

    /** Checks for blocks in a certain radius from the player pos
     * if they match at least one from the given list.
     *
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

    /** Checks for multiple blocks in a certain radius from the player pos
     * if they match at least one from the given list.
     *
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

    /**Used to check if the player has something that can be considered a Heat Source
     * for the Blazing Light
     *
     * @param player The player to perform checks on*/
    public static boolean checkBlazing(PlayerEntity player){
        if(player.isOnFire()){
            return true;
        }

        Item main = player.getMainHandStack().getItem();
        Item off = player.getOffHandStack().getItem();
        if(fire_items.contains(main) || fire_items.contains(off)){
            return true;
        }
        //TODO make the rad configable?
        return checkBlocks(player, fire_blocks, 3);
    }

    private static final List<Item> ice_items = Arrays.asList(Items.ICE, Items.PACKED_ICE, Items.BLUE_ICE, Items.SNOW, Items.SNOW_BLOCK, Items.SNOWBALL, Items.POWDER_SNOW_BUCKET);
    private static final List<Block> ice_blocks = Arrays.asList(Blocks.POWDER_SNOW, Blocks.SNOW, Blocks.ICE, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW_CAULDRON);

    /**Used to check if the player has something that can be considered a Cold Source
     * for the Frost Light
     *
     * @param player The player to perform checks on*/
    public static boolean checkFrost(PlayerEntity player){
        if(player.isFrozen()){
            return true;
        }

        Item main = player.getMainHandStack().getItem();
        Item off = player.getOffHandStack().getItem();
        if(ice_items.contains(main) || ice_items.contains(off)){
            return true;
        }
        return checkBlocks(player, ice_blocks, 3);
    }

    /**Used to check if the player can trigger the Earthen Light, aka if they have
     * dirt in their inventory or are sourronded by natural blocks
     *
     * @param player The player to perform checks on*/
    public static boolean checkEarthen(PlayerEntity player){

        if(player.getInventory().contains(new ItemStack(Items.DIRT, 64))){
            player.getInventory().removeStack(player.getInventory().getSlotWithStack(new ItemStack(Items.DIRT, 64)));
            return true;
        }

        return checkMultipleBlocksWithTags(player, 3, 3, TagKey.of(Registry.BLOCK_KEY, BlockTags.LUSH_GROUND_REPLACEABLE.id()));
    }

    /**Used to check if the player has something that can be considered a Cold Source
     * for the Frost Light
     *
     * @param player The player to perform checks on*/
    public static boolean checkWind(PlayerEntity player){

        if(player.getY() >= 64){
            if(!player.isOnGround()){
                return true;
            }
            return true;
        }

        return checkMultipleBlocks(player, Arrays.asList(Blocks.AIR, Blocks.CAVE_AIR, Blocks.VOID_AIR), 3, 6);
        //return checkMultipleBlocksWithTags(player, 3, 3, TagKey.of(Registry.BLOCK_KEY, BlockTags.AIR));
    }

    public static boolean checkFalling(LivingEntity entity) {
        if(entity instanceof PlayerEntity){
            return ((PlayerEntity) entity).fallDistance > 5 && !entity.isFallFlying() && !entity.isOnGround() && !entity.isClimbing() && !((PlayerEntity) entity).getAbilities().flying && !entity.isSwimming();
        }
        return entity.fallDistance > 5 && !entity.isFallFlying() && !entity.isOnGround() && !entity.isClimbing()  && !entity.isSwimming();
        /*if(!entity.isFallFlying() && !entity.isOnGround() && !entity.onClimbable() && !entity.getAbilities().flying && !entity.isSwimming()) {
            FallingData fallData = data.getData(this, () -> new FallingData(entity.getY()));
            if(entity instanceof LocalPlayer) {
                fallData.fallingSpeed = (float) (entity.getDeltaMovement().lengthSqr() / 11);
                return entity.fallDistance > 3;
            }
            if(entity.getY() == fallData.lastY) { //rerender in same tick
                return fallData.fallingSpeed > 0.5f / 3.5f;
            }else {
                fallData.fallingSpeed = (float) (fallData.lastY - entity.getY())/ 3.5f;
                fallData.lastY = entity.getY();
                return fallData.fallingSpeed > 0.5f / 3.5f;
            }
        }*/
    }

    public static boolean checkPoisoned(LivingEntity entity){
        return entity.hasStatusEffect(StatusEffects.POISON);
    }

    public static boolean checkDebuffed(LivingEntity entity){
        Collection<StatusEffectInstance> a = entity.getStatusEffects();
        for(StatusEffectInstance status : a){
            if(status.getEffectType().getCategory().equals(StatusEffectCategory.HARMFUL)){
                return true;
            }
        }
        return false;
    }

    /**Rerturn a list of the player's enemies in the area
     * for entity checks, also know as LightWithin.box_exapansion_amount
     *
     * @param player The player used as the center of the area to search of its enemies*/
    public static List<LivingEntity> getEnemies(PlayerEntity player){
        List<LivingEntity> targets = new ArrayList<>();
        List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
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
     * for entity checks, also know as LightWithin.box_exapansion_amount
     *
     * @param entity The entity used as the center of the area to search of its enemies*/
    public static List<LivingEntity> getEnemies(LivingEntity entity){
        List<LivingEntity> targets = new ArrayList<>();
        List<LivingEntity> entities = entity.getWorld().getEntitiesByClass(LivingEntity.class, new Box(entity.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
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

    /**Rerturn true if the two player are enemies. If they are not allied, they are considered enemies
     *
     * @param player The player used as the center of the area to search of its enemies*/
    public static boolean areEnemies(PlayerEntity player, PlayerEntity player1){
        //TODO Config If Not allied == ENEMIES aka will be attacked
        if(player.equals(player1)){
            return false;
        }
        if(!CheckUtils.CheckAllies.checkAlly(player, player1)){
            return true;
        }
        if(FabricLoader.getInstance().isModLoaded("factions")){
            return FactionChecker.areEnemies(player, player1);
        }
        return false;
    }

}
