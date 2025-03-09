package me.emafire003.dev.lightwithin.entities.goals;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.util.CheckUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

public class TrackEarthGolemTargetGoal extends TrackTargetGoal {
    private final PathAwareEntity entity;
    @Nullable
    private LivingEntity target;

    public TrackEarthGolemTargetGoal (PathAwareEntity mob) {
        super(mob, false, true);
        this.entity = mob;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    @Override
    public boolean canStart() {
        PlayerEntity summoner = Objects.requireNonNull(entity.getServer()).getPlayerManager().getPlayer(LightWithin.SUMMONED_BY_COMPONENT.get(entity).getSummonerUUID());
        if (summoner != null) {
            if (summoner.getRecentDamageSource() != null) {
                Entity attacker = summoner.getRecentDamageSource().getAttacker();
                if (attacker instanceof LivingEntity && !attacker.equals(entity) && !attacker.equals(summoner)) {
                    this.target = (LivingEntity) summoner.getRecentDamageSource().getAttacker();
                }
                return true;
            } else {
                List<LivingEntity> entities = summoner.getWorld().getEntitiesByClass(LivingEntity.class, new Box(summoner.getBlockPos()).expand(LightWithin.getBoxExpansionAmount()),
                        (ent -> {
                            if (CheckUtils.CheckAllies.checkEnemies(summoner, ent)) {
                                return !(ent instanceof PlayerEntity) || (!ent.isSpectator() && !((PlayerEntity) ent).isCreative());
                                //Sould accoutn for pets i think
                            } else if (ent instanceof HostileEntity && !CheckUtils.CheckAllies.checkAlly(summoner, ent)) {
                                return false;
                            }
                            return false;
                        }));
                if (!entities.isEmpty()) {
                    this.target = entities.get(entity.getRandom().nextBetween(0, entities.size()));
                    return true;
                }
            }

        }
        if (this.target == null) {
            return false;
        }
        return !(this.target instanceof PlayerEntity) || !this.target.isSpectator() && !((PlayerEntity) this.target).isCreative();
    }

    @Override
    public void start() {
        this.entity.setTarget(this.target);
        super.start();
    }
}
