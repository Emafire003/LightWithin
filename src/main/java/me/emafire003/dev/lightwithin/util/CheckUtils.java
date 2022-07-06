package me.emafire003.dev.lightwithin.util;

import me.emafire003.dev.lightwithin.compat.ModChecker;
import me.emafire003.dev.lightwithin.compat.factions.FactionChecker;
import me.emafire003.dev.lightwithin.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
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

    /**Will check for nearby allies, if they are on low health and possibly surrounded
     * it will return true
     *
     * @param player The player that could trigger their light
     * @param entity The entity that is attacking it
     * @param health_percent The percentage (15, 25, 70) below which the target is in danger (hence light activatable)
     * */
    public static boolean checkAllyHealth(@NotNull PlayerEntity player, Entity entity, int health_percent){
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
            if(ModChecker.isLoaded("factions") && entity instanceof PlayerEntity && teammate instanceof PlayerEntity){
                return checkFaction((PlayerEntity) entity, (PlayerEntity) teammate);
            }
            return checkTeam(entity, teammate) || checkPet(entity, teammate);

        }
    }

    //TODO make list configable
    private static final List<Item> fire_items = Arrays.asList(Items.TORCH, Items.FIRE_CHARGE, Items.FLINT_AND_STEEL, Items.CAMPFIRE, Items.SOUL_CAMPFIRE, Items.SOUL_TORCH, Items.LAVA_BUCKET);
    private static final List<Block> fire_blocks = Arrays.asList(Blocks.LAVA, Blocks.MAGMA_BLOCK, Blocks.FIRE, Blocks.SOUL_FIRE, Blocks.TORCH, Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH, Blocks.WALL_TORCH, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE);

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
        for(Item item : fire_items){
            if(item.equals(main) || item.equals(main)){
                return true;
            }
        }

        //TODO config this since it could be performance heavy
        BlockPos origin = player.getBlockPos();
        int rad = 3;
        for(int y = -rad; y <= rad; y++)
        {
            for(int x = -rad; x <= rad; x++)
            {
                for(int z = -rad; z <= rad; z++)
                {
                    BlockPos pos = origin.add(x, y, z);
                    for(Block block : fire_blocks){
                        if(player.getWorld().getBlockState(pos).equals(block)){
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }

}
