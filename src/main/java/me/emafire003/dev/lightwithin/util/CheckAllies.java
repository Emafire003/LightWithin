package me.emafire003.dev.lightwithin.util;

import me.emafire003.dev.lightwithin.compat.ModChecker;
import me.emafire003.dev.lightwithin.compat.factions.FactionChecker;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class CheckAllies {

    public static boolean checkTeam(LivingEntity entity, LivingEntity teammate){
        if(entity.getScoreboardTeam() != null && entity.isTeammate(teammate)){
            return true;
        }return false;
    }

    public static boolean checkFaction(PlayerEntity player, PlayerEntity player1){
        if(FactionChecker.areInSameFaction(player, player1)){
            return true;
        }return false;
    }

    public static boolean checkAlly(LivingEntity entity, LivingEntity teammate){
        if(ModChecker.isLoaded("factions") && entity instanceof PlayerEntity && teammate instanceof PlayerEntity){
            return checkFaction((PlayerEntity) entity, (PlayerEntity) teammate);
        }
        return checkTeam(entity, teammate);

    }
}
