package me.emafire003.dev.lightwithin.entities.goals;

import java.util.EnumSet;
import java.util.Objects;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.SummonedByComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class FollowSummonerGoal extends Goal {
    public static final int TELEPORT_DISTANCE = 12;
    private static final int HORIZONTAL_RANGE = 2;
    private static final int HORIZONTAL_VARIATION = 3;
    private static final int VERTICAL_VARIATION = 1;
    private final PathAwareEntity summoned;
    private LivingEntity summoner;
    private final WorldView world;
    private final double speed;
    private final EntityNavigation navigation;
    private int updateCountdownTicks;
    private final float maxDistance;
    private final float minDistance;
    private float oldWaterPathfindingPenalty;
    private final boolean leavesAllowed;

    public FollowSummonerGoal(PathAwareEntity tameable, double speed, float minDistance, float maxDistance, boolean leavesAllowed) {
        this.summoned = tameable;
        this.world = tameable.getWorld();
        this.speed = speed;
        this.navigation = tameable.getNavigation();
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.leavesAllowed = leavesAllowed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        if (!(tameable.getNavigation() instanceof MobNavigation) && !(tameable.getNavigation() instanceof BirdNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowSummonerGoal");
        }
    }

    public boolean canStart() {

        SummonedByComponent component = LightWithin.SUMMONED_BY_COMPONENT.get(summoned);
        /*if(!summoned.getWorld().isClient()){

        }*/

        LivingEntity livingEntity = Objects.requireNonNull(this.summoned.getServer()).getPlayerManager().getPlayer(component.getSummonerUUID());

        if (livingEntity == null) {
            return false;
        } else if (livingEntity.isSpectator()) {
            return false;
        } else if (this.cannotFollow()) {
            return false;
        } else if (this.summoned.squaredDistanceTo(livingEntity) < (double)(this.minDistance * this.minDistance)) {
            return false;
        } else {
            this.summoner = livingEntity;
            return true;
        }
    }

    public boolean shouldContinue() {
        if (this.navigation.isIdle()) {
            return false;
        } else if (this.cannotFollow()) {
            return false;
        } else {
            return !(this.summoned.squaredDistanceTo(this.summoner) <= (double)(this.maxDistance * this.maxDistance));
        }
    }

    private boolean cannotFollow() {
        return this.summoned.hasVehicle() || this.summoned.isLeashed();
    }

    public void start() {
        this.updateCountdownTicks = 0;
        this.oldWaterPathfindingPenalty = this.summoned.getPathfindingPenalty(PathNodeType.WATER);
        this.summoned.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
    }

    public void stop() {
        this.summoner = null;
        this.navigation.stop();
        this.summoned.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty);
    }

    public void tick() {
        this.summoned.getLookControl().lookAt(this.summoner, 10.0F, (float)this.summoned.getMaxLookPitchChange());
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = this.getTickCount(10);
            this.navigation.startMovingTo(this.summoner, this.speed);
        }
    }

}
